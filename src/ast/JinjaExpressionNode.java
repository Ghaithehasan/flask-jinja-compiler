package ast;

/**
 * Represents a Jinja2 expression node ({{ ... }}).
 * Contains the expression content.
 */
public class JinjaExpressionNode extends ASTNode {
    private String expressionContent;

    /**
     * Constructs a Jinja expression node.
     *
     * @param expressionContent the content inside the Jinja expression
     * @param lineNumber        the line number where the expression appears
     */
    public JinjaExpressionNode(String expressionContent, int lineNumber) {
        super("JinjaExpression", lineNumber);
        this.expressionContent = expressionContent;
    }

    /**
     * Gets the expression content.
     *
     * @return the expression content
     */
    public String getExpressionContent() {
        return expressionContent;
    }

    /**
     * Sets the expression content.
     *
     * @param expressionContent the expression content to set
     */
    public void setExpressionContent(String expressionContent) {
        this.expressionContent = expressionContent;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();

        sb.append(indentStr).append("JinjaExpressionNode {{ ").append(expressionContent).append(" }}");
        if (lineNumber > 0) {
            sb.append(" (line ").append(lineNumber).append(")");
        }
        sb.append("\n");

        for (ASTNode child : children) {
            sb.append(child.printTree(indent + 1));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return super.toString() + " {{ " + expressionContent + " }}";
    }
}

