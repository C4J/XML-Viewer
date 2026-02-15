package com.commander4j.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public final class TreeExpandUtil
{

	private TreeExpandUtil()
	{
	}

	/*
	 * ------------------------------------------------------------ Expand to
	 * level N, collapse everything deeper
	 * ------------------------------------------------------------
	 */

	/**
	 * Expands the tree up to and including the given level and collapses all
	 * deeper nodes. Depth is counted from root: root=0, root children=1, etc.
	 *
	 * @param tree
	 *            the JTree to update
	 * @param level
	 *            maximum depth to keep expanded (0 expands only root)
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
			tree.expandPath(rootPath);

			applyExpandCollapseToDepth(tree, model, root, rootPath, 0, maxLevel);
		});
	}

	private static void applyExpandCollapseToDepth(JTree tree, TreeModel model, Object node, TreePath path, int depth, int maxDepth)
	{
		if (depth <= maxDepth)
		{
			tree.expandPath(path);
		}
		else
		{
			tree.collapsePath(path);
			return; // once collapsed, no need to traverse deeper
		}

		int childCount = model.getChildCount(node);
		for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChild(node, i);
			TreePath childPath = path.pathByAddingChild(child);
			applyExpandCollapseToDepth(tree, model, child, childPath, depth + 1, maxDepth);
		}
	}

	/*
	 * ------------------------------------------------------------ Expand All /
	 * Collapse All ------------------------------------------------------------
	 */

	/**
	 * Expands every node in the tree (based on what the model currently
	 * exposes).
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
			expandAllFrom(tree, model, root, rootPath);
		});
	}

	/**
	 * Collapses every node in the tree, keeping the root expanded (common UI
	 * behaviour).
	 */
	public static void collapseAll(JTree tree)
	{
		collapseAll(tree, true);
	}

	/**
	 * Collapses every node in the tree.
	 *
	 * @param keepRootExpanded
	 *            if true, root remains expanded
	 */
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

			// Post-order collapse so we don't cut off traversal by collapsing
			// early.
			collapseAllFrom(tree, model, root, rootPath, keepRootExpanded);

			if (keepRootExpanded)
			{
				tree.expandPath(rootPath);
			}
		});
	}

	private static void expandAllFrom(JTree tree, TreeModel model, Object node, TreePath path)
	{
		tree.expandPath(path);

		int childCount = model.getChildCount(node);
		for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChild(node, i);
			TreePath childPath = path.pathByAddingChild(child);
			expandAllFrom(tree, model, child, childPath);
		}
	}

	private static void collapseAllFrom(JTree tree, TreeModel model, Object node, TreePath path, boolean keepRootExpanded)
	{
		int childCount = model.getChildCount(node);
		for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChild(node, i);
			TreePath childPath = path.pathByAddingChild(child);
			collapseAllFrom(tree, model, child, childPath, keepRootExpanded);
		}

		boolean isRoot = (path.getPathCount() == 1);
		if (!(keepRootExpanded && isRoot))
		{
			tree.collapsePath(path);
		}
	}

	/*
	 * ------------------------------------------------------------
	 * Expand/Collapse Selected Path (node only)
	 * ------------------------------------------------------------
	 */

	/**
	 * Expands the currently selected path(s) (node only, not the whole
	 * subtree). This makes the selected node visible and expands that node.
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
				if (p != null)
					tree.expandPath(p);
			}
		});
	}

	/**
	 * Collapses the currently selected path(s) (node only). Collapsing a node
	 * collapses its visible descendants in the UI.
	 */
	public static void collapseSelectedPath(JTree tree)
	{
		if (tree == null)
			return;

		runOnEdt(() -> {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0)
				return;

			// Deepest first (useful if nested selections exist).
			sortByDepthDescending(paths);

			for (TreePath p : paths)
			{
				if (p != null)
					tree.collapsePath(p);
			}
		});
	}

	/*
	 * ------------------------------------------------------------
	 * Expand/Collapse Selected Subtree (entire subtree)
	 * ------------------------------------------------------------
	 */

	/**
	 * Expands the entire subtree under the currently selected node(s). If
	 * multiple paths are selected, redundant descendant selections are ignored
	 * (parent wins).
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

			for (TreePath p : roots)
			{
				Object node = p.getLastPathComponent();
				if (node == null)
					continue;

				// Ensure it's visible, then expand subtree
				tree.expandPath(p);
				expandAllFrom(tree, model, node, p);
			}
		});
	}

	/**
	 * Collapses the entire subtree under the currently selected node(s),
	 * including the selected node itself.
	 */
	public static void collapseSelectedSubtree(JTree tree)
	{
		collapseSelectedSubtree(tree, true);
	}

	/**
	 * Collapses the entire subtree under the currently selected node(s).
	 *
	 * @param collapseSelectedNodeToo
	 *            if true, the selected node is collapsed after its children; if
	 *            false, children are collapsed but the selected node stays
	 *            expanded.
	 */
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

			for (TreePath p : roots)
			{
				Object node = p.getLastPathComponent();
				if (node == null)
					continue;

				// Collapse subtree children first, then optionally collapse the
				// selected node itself.
				collapseSubtreeFrom(tree, model, node, p, collapseSelectedNodeToo);

				if (!collapseSelectedNodeToo)
				{
					tree.expandPath(p); // keep the selected node open
				}
			}
		});
	}

	private static void collapseSubtreeFrom(JTree tree, TreeModel model, Object node, TreePath path, boolean collapseThisNode)
	{
		int childCount = model.getChildCount(node);
		for (int i = 0; i < childCount; i++)
		{
			Object child = model.getChild(node, i);
			TreePath childPath = path.pathByAddingChild(child);
			collapseSubtreeFrom(tree, model, child, childPath, true); // always
																		// collapse
																		// descendants
		}

		if (collapseThisNode)
		{
			tree.collapsePath(path);
		}
	}

	/*
	 * ------------------------------------------------------------ Selection
	 * helpers (avoid duplicate work)
	 * ------------------------------------------------------------
	 */

	/**
	 * From an array of selected paths, return only the top-most ones: if a path
	 * is a descendant of another selected path, drop it.
	 */
	private static List<TreePath> uniqueSelectionRoots(TreePath[] selection)
	{
		List<TreePath> list = new ArrayList<>(Arrays.asList(selection));
		list.removeIf(p -> p == null);

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
			return Integer.compare(b, a); // descending
		});
	}

	/*
	 * ------------------------------------------------------------ EDT helper
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
