package ast;

/**
 * Represents a single CSS selector.
 * Used as a child of CssSelectorListNode when a rule has multiple selectors.
 */
public class CssSelectorNode extends ASTNode {
    private final String selector;

    /**
     * Constructs a CSS selector node.
     *
     * @param selector   the CSS selector string
     * @param lineNumber the line number where the selector appears
     */
    public CssSelectorNode(String selector, int lineNumber) {
        super("CssSelector", lineNumber);
        this.selector = selector != null ? selector.trim() : "";
    }

    /**
     * Gets the CSS selector.
     *
     * @return the selector
     */
    public String getSelector() {
        return selector;
    }

    @Override
    public String toString() {
        return "CssSelector \"" + selector + "\"";
    }

    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();

        sb.append(indentStr).append(nodeName);
        if (selector != null && !selector.isEmpty()) {
            sb.append(" \"").append(selector).append("\"");
        }
        if (lineNumber > 0) {
            sb.append(" (line ").append(lineNumber).append(")");
        }
        sb.append("\n");

        for (ASTNode child : children) {
            sb.append(child.printTree(indent + 1));
        }

        return sb.toString();
    }
}

