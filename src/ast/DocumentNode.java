package ast;

/**
 * Represents the root document node containing all HTML elements.
 */
public class DocumentNode extends ASTNode {
    /**
     * Constructs a document node.
     *
     * @param lineNumber the line number where the document starts
     */
    public DocumentNode(int lineNumber) {
        super("Document", lineNumber);
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

