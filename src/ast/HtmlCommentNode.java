package ast;

/**
 * Represents an HTML comment node.
 * Contains the comment text.
 */
public class HtmlCommentNode extends ASTNode {
    private String comment;

    /**
     * Constructs an HTML comment node.
     *
     * @param comment    the comment text
     * @param lineNumber the line number where the comment appears
     */
    public HtmlCommentNode(String comment, int lineNumber) {
        super("HtmlComment", lineNumber);
        this.comment = comment;
    }

    /**
     * Gets the comment text.
     *
     * @return the comment text
     */
    public String getComment() {
        return comment;
    }


    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();

        sb.append(indentStr).append("HtmlCommentNode <!--");
        if (comment != null) {
            String displayComment = comment;
            if (displayComment.length() > 50) {
                displayComment = displayComment.substring(0, 47) + "...";
            }
            sb.append(displayComment);
        }
        sb.append("-->");
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
        String displayComment = comment;
        if (displayComment.length() > 50) {
            displayComment = displayComment.substring(0, 47) + "...";
        }
        return super.toString() + " <!--" + displayComment + "-->";
    }
}

