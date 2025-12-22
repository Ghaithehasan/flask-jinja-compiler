package ast;

/**
 * Represents a content container node.
 * Used to group child elements and text nodes within an HTML element.
 */
public class ContentNode extends ASTNode {
    /**
     * Constructs a content node.
     *
     * @param lineNumber the line number where the content appears
     */
    public ContentNode(int lineNumber) {
        super("Content", lineNumber);
    }

    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();

        sb.append(indentStr).append("ContentNode");
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
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

