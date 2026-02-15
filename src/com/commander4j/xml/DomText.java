package com.commander4j.xml;

import org.w3c.dom.*;

public final class DomText {

    public DomText() {}

    /** Returns only the element's direct text (no descendant element text). */
    public  String directText(Element e) {
        if (e == null) return "";

        StringBuilder sb = new StringBuilder();
        NodeList kids = e.getChildNodes();

        for (int i = 0; i < kids.getLength(); i++) {
            Node n = kids.item(i);

            short t = n.getNodeType();
            if (t == Node.TEXT_NODE || t == Node.CDATA_SECTION_NODE) {
                // text directly under the element (typically whitespace/newlines in pretty XML)
                sb.append(n.getNodeValue());
            }
        }

        // Trim removes indentation/newline-only text; use strip() on Java 11+
        return sb.toString().trim();
    }
}
