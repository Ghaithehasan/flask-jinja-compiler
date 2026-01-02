package ast;

/**
 * Represents a Jinja2 comment node ({# ... #}).
 * Contains the comment content.
 */
public class JinjaCommentNode extends ASTNode {
    private String commentContent;

    /**
     * Constructs a Jinja comment node.
     *
     * @param commentContent the content inside the Jinja comment
     * @param lineNumber     the line number where the comment appears
     */
    public JinjaCommentNode(String commentContent, int lineNumber) {
        super("JinjaComment", lineNumber);
        this.commentContent = commentContent;
    }

    /**
     * Gets the comment content.
     *
     * @return the comment content
     */
    public String getCommentContent() {
        return commentContent;
    }

    /**
     * Sets the comment content.
     *
     * @param commentContent the comment content to set
     */
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }



    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();
        sb.append(indentStr).append("JinjaCommentNode {# ").append(commentContent).append(" #}");
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
        return super.toString() + " {# " + commentContent + " #}";
    }
}

