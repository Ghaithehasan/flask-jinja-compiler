package ast;

/**
 * Represents a CSS rule with a selector.
 * Contains selector list and declaration nodes as children.
 */
public class CssRuleNode extends ASTNode {
    private final String selector;

    /**
     * Constructs a CSS rule node.
     *
     * @param selector   the CSS selector (may contain commas for multiple selectors)
     * @param lineNumber the line number where the rule appears
     */
    public CssRuleNode(String selector, int lineNumber) {
        super("CssRule", lineNumber);
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
        return "CssRule (selector: " + selector + ")";
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
            sb.append(" (selector: ").append(selector).append(")");
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

