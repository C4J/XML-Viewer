package com.commander4j.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Tree expansion/collapse utilities.
 *
 * Drop-in replacement that avoids recursion and batches large operations to reduce
 * macOS AWT run-loop re-entrancy pressure during massive JTree expansion.
 *
 * Busy listener:
 * - Fires ONLY for batched operations that go through startOperation(...) (Timer-based work).
 * - Does NOT fire for immediate methods (expandSelectedPath/collapseSelectedPath).
 */
public final class TreeExpandUtil
{
	// Tune these with JVM props if needed:
	// -Dxmlviewer.treeExpand.batchSize=250
	// -Dxmlviewer.treeExpand.timerDelayMs=1
	private static final int DEFAULT_BATCH_SIZE = 250;
	private static final int DEFAULT_TIMER_DELAY_MS = 1;

	// ---------------- Busy listener support (batched ops only) ----------------

	public interface BusyListener
	{
		void onBusyChanged(JTree tree, boolean busy);
	}

	private static volatile BusyListener busyListener;

	public static void setBusyListener(BusyListener listener)
	{
		busyListener = listener;
	}

	public static boolean isBusy()
	{
		synchronized (OP_LOCK)
		{
			return activeOperation != null;
		}
	}

	private static void notifyBusy(JTree tree, boolean busy)
	{
		BusyListener l = busyListener;
		if (l == null)
			return;

		runOnEdt(() -> l.onBusyChanged(tree, busy));
	}

	// ------------------------------------------------------------------------

	private static final Object OP_LOCK = new Object();
	private static Operation activeOperation;

	private TreeExpandUtil()
	{
	}

	/*
	 * ------------------------------------------------------------
	 * Expand to level N, collapse everything deeper (BATCHED)
	 * ------------------------------------------------------------
	 */

	/**
	 * Expands the tree up to and including the given level and collapses all deeper nodes.
	 * Depth is counted from root: root=0, root children=1, etc.
	 */
	public static void expandToLevelAndCollapseDeeper(JTree tree, int level)
	{
		if (tree == null)
			return;

		final int maxLevel = Math.max(0, level);

		runOnEdt(() -> {
			TreeModel model = tree.getModel();
			if (model == null)
				return;
			Object root = model.getRoot();
			if (root == null)
				return;

			TreePath rootPath = new TreePath(root);
			// Make root visible immediately.
			if (!tree.isExpanded(rootPath))
				tree.expandPath(rootPath);

			startOperation(new DepthExpandCollapseOp(tree, model, root, rootPath, maxLevel));
		});
	}

	/*
	 * ------------------------------------------------------------
	 * Expand All / Collapse All (BATCHED)
	 * ------------------------------------------------------------
	 */

	public static void expandAll(JTree tree)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreeModel model = tree.getModel();
			if (model == null)
				return;
			Object root = model.getRoot();
			if (root == null)
				return;

			TreePath rootPath = new TreePath(root);
			if (!tree.isExpanded(rootPath))
				tree.expandPath(rootPath);

			startOperation(new ExpandAllOp(tree, model, root, rootPath));
		});
	}

	public static void collapseAll(JTree tree)
	{
		collapseAll(tree, true);
	}

	public static void collapseAll(JTree tree, boolean keepRootExpanded)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreeModel model = tree.getModel();
			if (model == null)
				return;
			Object root = model.getRoot();
			if (root == null)
				return;

			TreePath rootPath = new TreePath(root);

			// Keeping root expanded is common UI behaviour; do it immediately.
			if (keepRootExpanded && !tree.isExpanded(rootPath))
				tree.expandPath(rootPath);

			startOperation(new CollapseAllOp(tree, model, root, rootPath, keepRootExpanded));
		});
	}

	/*
	 * ------------------------------------------------------------
	 * Expand/Collapse Selected Path (IMMEDIATE - no busy events)
	 * ------------------------------------------------------------
	 */

	public static void expandSelectedPath(JTree tree)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0)
				return;

			for (TreePath p : paths)
			{
				if (p != null && !tree.isExpanded(p))
					tree.expandPath(p);
			}
		});
	}

	public static void collapseSelectedPath(JTree tree)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0)
				return;

			// Deepest first is usually what people expect.
			sortByDepthDescending(paths);

			for (TreePath p : paths)
			{
				if (p != null && tree.isExpanded(p))
					tree.collapsePath(p);
			}
		});
	}

	/*
	 * ------------------------------------------------------------
	 * Expand/Collapse Selected Subtree (BATCHED)
	 * ------------------------------------------------------------
	 */

	public static void expandSelectedSubtree(JTree tree)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreeModel model = tree.getModel();
			if (model == null)
				return;

			TreePath[] sel = tree.getSelectionPaths();
			if (sel == null || sel.length == 0)
				return;

			List<TreePath> roots = uniqueSelectionRoots(sel);
			if (roots.isEmpty())
				return;

			startOperation(new ExpandSelectedSubtreeOp(tree, model, roots));
		});
	}

	public static void collapseSelectedSubtree(JTree tree)
	{
		collapseSelectedSubtree(tree, true);
	}

	public static void collapseSelectedSubtree(JTree tree, boolean collapseSelectedNodeToo)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreeModel model = tree.getModel();
			if (model == null)
				return;

			TreePath[] sel = tree.getSelectionPaths();
			if (sel == null || sel.length == 0)
				return;

			List<TreePath> roots = uniqueSelectionRoots(sel);
			if (roots.isEmpty())
				return;

			startOperation(new CollapseSelectedSubtreeOp(tree, model, roots, collapseSelectedNodeToo));
		});
	}

	/*
	 * ------------------------------------------------------------
	 * Operation framework (batched via Timer)
	 * ------------------------------------------------------------
	 */

	private static int batchSize()
	{
		int v = Integer.getInteger("xmlviewer.treeExpand.batchSize", DEFAULT_BATCH_SIZE);
		return Math.max(1, v);
	}

	private static int timerDelayMs()
	{
		int v = Integer.getInteger("xmlviewer.treeExpand.timerDelayMs", DEFAULT_TIMER_DELAY_MS);
		return Math.max(0, v);
	}

	private static void startOperation(Operation op)
	{
		if (op == null)
			return;

		synchronized (OP_LOCK)
		{
			// Cancel any existing operation and clear busy.
			if (activeOperation != null)
			{
				Operation prev = activeOperation;
				activeOperation = null;
				prev.stop();
				notifyBusy(prev.getTreeUnsafe(), false);
			}

			JTree t = op.getTreeUnsafe();
			if (t == null || !t.isDisplayable())
			{
				// Nothing to do.
				return;
			}

			activeOperation = op;
			op.start();
			notifyBusy(t, true);
		}
	}

	private static abstract class Operation implements ActionListener
	{
		private final WeakReference<JTree> treeRef;
		protected final TreeModel model;

		protected final Deque<NodeState> stack = new ArrayDeque<>();
		private Timer timer;

		protected Operation(JTree tree, TreeModel model)
		{
			this.treeRef = new WeakReference<>(tree);
			this.model = model;
		}

		final JTree getTreeUnsafe()
		{
			return treeRef.get();
		}

		final void start()
		{
			JTree tree = treeRef.get();
			if (tree == null)
				return;

			// Ensure we always run on EDT.
			if (!SwingUtilities.isEventDispatchThread())
			{
				SwingUtilities.invokeLater(this::start);
				return;
			}

			timer = new Timer(timerDelayMs(), this);
			timer.setCoalesce(true);
			timer.start();
		}

		final void stop()
		{
			if (timer != null)
			{
				timer.stop();
				timer = null;
			}
		}

		@Override
		public final void actionPerformed(ActionEvent e)
		{
			JTree tree = treeRef.get();
			if (tree == null || !tree.isDisplayable())
			{
				finishAndClear();
				return;
			}

			try
			{
				int budget = batchSize();
				while (budget-- > 0 && !stack.isEmpty())
				{
					NodeState s = stack.pop();
					processOne(tree, s);
				}

				if (stack.isEmpty())
				{
					onFinished(tree);
					finishAndClear();
				}
			}
			catch (Throwable t)
			{
				// Fail safe: stop the timer so we don't spin the EDT forever.
				finishAndClear();
				throw t;
			}
		}

		private void finishAndClear()
		{
			JTree tree = treeRef.get();

			stop();

			boolean cleared = false;
			synchronized (OP_LOCK)
			{
				if (activeOperation == this)
				{
					activeOperation = null;
					cleared = true;
				}
			}

			// Only notify if we really were the active operation.
			if (cleared)
				notifyBusy(tree, false);
		}

		protected abstract void processOne(JTree tree, NodeState s);

		protected void onFinished(JTree tree)
		{
			// Optional hook.
		}
	}

	private static final class NodeState
	{
		final Object node;
		final TreePath path;
		final int depth;
		final boolean post; // used for post-order collapse

		NodeState(Object node, TreePath path, int depth, boolean post)
		{
			this.node = node;
			this.path = path;
			this.depth = depth;
			this.post = post;
		}
	}

	private static final class DepthExpandCollapseOp extends Operation
	{
		private final int maxDepth;

		DepthExpandCollapseOp(JTree tree, TreeModel model, Object root, TreePath rootPath, int maxDepth)
		{
			super(tree, model);
			this.maxDepth = maxDepth;

			stack.push(new NodeState(root, rootPath, 0, false));
		}

		@Override
		protected void processOne(JTree tree, NodeState s)
		{
			if (s.depth <= maxDepth)
			{
				if (!tree.isExpanded(s.path))
					tree.expandPath(s.path);

				int childCount = model.getChildCount(s.node);
				// Push in reverse so natural order is preserved with a stack.
				for (int i = childCount - 1; i >= 0; i--)
				{
					Object child = model.getChild(s.node, i);
					TreePath childPath = s.path.pathByAddingChild(child);
					stack.push(new NodeState(child, childPath, s.depth + 1, false));
				}
			}
			else
			{
				// Collapsing this node hides all deeper descendants; don't traverse further.
				if (tree.isExpanded(s.path))
					tree.collapsePath(s.path);
			}
		}
	}

	private static final class ExpandAllOp extends Operation
	{
		ExpandAllOp(JTree tree, TreeModel model, Object root, TreePath rootPath)
		{
			super(tree, model);
			stack.push(new NodeState(root, rootPath, 0, false));
		}

		@Override
		protected void processOne(JTree tree, NodeState s)
		{
			if (!tree.isExpanded(s.path))
				tree.expandPath(s.path);

			int childCount = model.getChildCount(s.node);
			for (int i = childCount - 1; i >= 0; i--)
			{
				Object child = model.getChild(s.node, i);
				TreePath childPath = s.path.pathByAddingChild(child);
				stack.push(new NodeState(child, childPath, s.depth + 1, false));
			}
		}
	}

	private static final class CollapseAllOp extends Operation
	{
		private final boolean keepRootExpanded;

		CollapseAllOp(JTree tree, TreeModel model, Object root, TreePath rootPath, boolean keepRootExpanded)
		{
			super(tree, model);
			this.keepRootExpanded = keepRootExpanded;
			// Post-order collapse (two-phase node state).
			stack.push(new NodeState(root, rootPath, 0, false));
		}

		@Override
		protected void processOne(JTree tree, NodeState s)
		{
			if (!s.post)
			{
				// Schedule post step.
				stack.push(new NodeState(s.node, s.path, s.depth, true));

				int childCount = model.getChildCount(s.node);
				for (int i = childCount - 1; i >= 0; i--)
				{
					Object child = model.getChild(s.node, i);
					TreePath childPath = s.path.pathByAddingChild(child);
					stack.push(new NodeState(child, childPath, s.depth + 1, false));
				}
			}
			else
			{
				boolean isRoot = (s.path.getPathCount() == 1);
				if (!(keepRootExpanded && isRoot))
				{
					if (tree.isExpanded(s.path))
						tree.collapsePath(s.path);
				}
			}
		}

		@Override
		protected void onFinished(JTree tree)
		{
			if (keepRootExpanded)
			{
				Object root = model.getRoot();
				if (root != null)
				{
					TreePath rootPath = new TreePath(root);
					if (!tree.isExpanded(rootPath))
						tree.expandPath(rootPath);
				}
			}
		}
	}

	private static final class ExpandSelectedSubtreeOp extends Operation
	{
		ExpandSelectedSubtreeOp(JTree tree, TreeModel model, List<TreePath> roots)
		{
			super(tree, model);

			// Push roots in reverse so first selected root is processed first.
			for (int i = roots.size() - 1; i >= 0; i--)
			{
				TreePath p = roots.get(i);
				Object node = (p == null) ? null : p.getLastPathComponent();
				if (node != null)
					stack.push(new NodeState(node, p, p.getPathCount() - 1, false));
			}
		}

		@Override
		protected void processOne(JTree tree, NodeState s)
		{
			if (!tree.isExpanded(s.path))
				tree.expandPath(s.path);

			int childCount = model.getChildCount(s.node);
			for (int i = childCount - 1; i >= 0; i--)
			{
				Object child = model.getChild(s.node, i);
				TreePath childPath = s.path.pathByAddingChild(child);
				stack.push(new NodeState(child, childPath, s.depth + 1, false));
			}
		}
	}

	private static final class CollapseSelectedSubtreeOp extends Operation
	{
		private final boolean collapseSelectedNodeToo;

		CollapseSelectedSubtreeOp(JTree tree, TreeModel model, List<TreePath> roots, boolean collapseSelectedNodeToo)
		{
			super(tree, model);
			this.collapseSelectedNodeToo = collapseSelectedNodeToo;

			// Post-order for each root.
			for (int i = roots.size() - 1; i >= 0; i--)
			{
				TreePath p = roots.get(i);
				Object node = (p == null) ? null : p.getLastPathComponent();
				if (node != null)
					stack.push(new NodeState(node, p, p.getPathCount() - 1, false));
			}
		}

		@Override
		protected void processOne(JTree tree, NodeState s)
		{
			if (!s.post)
			{
				stack.push(new NodeState(s.node, s.path, s.depth, true));

				int childCount = model.getChildCount(s.node);
				for (int i = childCount - 1; i >= 0; i--)
				{
					Object child = model.getChild(s.node, i);
					TreePath childPath = s.path.pathByAddingChild(child);
					stack.push(new NodeState(child, childPath, s.depth + 1, false));
				}
			}
			else
			{
				if (collapseSelectedNodeToo)
				{
					if (tree.isExpanded(s.path))
						tree.collapsePath(s.path);
				}
				else
				{
					// Leave selected roots as-is; they may be re-expanded in onFinished.
				}
			}
		}

		@Override
		protected void onFinished(JTree tree)
		{
			if (!collapseSelectedNodeToo)
			{
				// Ensure selected paths remain open.
				TreePath[] sel = tree.getSelectionPaths();
				if (sel != null)
				{
					for (TreePath p : sel)
					{
						if (p != null && !tree.isExpanded(p))
							tree.expandPath(p);
					}
				}
			}
		}
	}

	/*
	 * ------------------------------------------------------------
	 * Selection helpers
	 * ------------------------------------------------------------
	 */

	private static List<TreePath> uniqueSelectionRoots(TreePath[] selection)
	{
		List<TreePath> list = new ArrayList<>();
		for (TreePath p : selection)
		{
			if (p != null)
				list.add(p);
		}

		// Parents first (shortest first)
		list.sort(Comparator.comparingInt(TreePath::getPathCount));

		List<TreePath> roots = new ArrayList<>();
		for (TreePath candidate : list)
		{
			boolean covered = false;
			for (TreePath root : roots)
			{
				if (isAncestorOrSame(root, candidate))
				{
					covered = true;
					break;
				}
			}
			if (!covered)
				roots.add(candidate);
		}
		return roots;
	}

	private static boolean isAncestorOrSame(TreePath ancestor, TreePath descendant)
	{
		if (ancestor == null || descendant == null)
			return false;

		int aLen = ancestor.getPathCount();
		int dLen = descendant.getPathCount();
		if (aLen > dLen)
			return false;

		Object[] a = ancestor.getPath();
		Object[] d = descendant.getPath();
		for (int i = 0; i < aLen; i++)
		{
			if (a[i] != d[i])
				return false;
		}
		return true;
	}

	private static void sortByDepthDescending(TreePath[] paths)
	{
		Arrays.sort(paths, (p1, p2) -> {
			int a = (p1 == null) ? -1 : p1.getPathCount();
			int b = (p2 == null) ? -1 : p2.getPathCount();
			return Integer.compare(b, a);
		});
	}

	/*
	 * ------------------------------------------------------------
	 * EDT helper
	 * ------------------------------------------------------------
	 */

	private static void runOnEdt(Runnable r)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			r.run();
		}
		else
		{
			SwingUtilities.invokeLater(r);
		}
	}
}