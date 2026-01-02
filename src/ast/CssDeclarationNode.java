package ast;

/**
 * Represents a CSS declaration (property: value).
 * Used as a child of CssRuleNode.
 */
public class CssDeclarationNode extends ASTNode {
    private final String property;
    private final String value;

    /**
     * Constructs a CSS declaration node.
     *
     * @param property   the CSS property name
     * @param value      the CSS property value
     * @param lineNumber the line number where the declaration appears
     */
    public CssDeclarationNode(String property, String value, int lineNumber) {
        super("CssDeclaration", lineNumber);
        this.property = property != null ? property.trim() : "";
        this.value = value != null ? value.trim() : "";
    }

    /**
     * Gets the CSS property name.
     *
     * @return the property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets the CSS property value.
     *
     * @return the property value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CssDeclaration (" + property + ": " + value + ")";
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
        if (property != null && value != null && !property.isEmpty() && !value.isEmpty()) {
            sb.append(" (").append(property).append(": ").append(value).append(")");
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

