package ast;

/**
 * Represents a Jinja2 block node ({% ... %}).
 * Contains the block content.
 */
public class JinjaBlockNode extends ASTNode {
    private String blockContent;

    /**
     * Constructs a Jinja block node.
     *
     * @param blockContent the content inside the Jinja block
     * @param lineNumber   the line number where the block appears
     */
    public JinjaBlockNode(String blockContent, int lineNumber) {
        super("JinjaBlock", lineNumber);
        this.blockContent = blockContent;
    }

    /**
     * Gets the block content.
     *
     * @return the block content
     */
    public String getBlockContent() {
        return blockContent;
    }

    /**
     * Sets the block content.
     *
     * @param blockContent the block content to set
     */
    public void setBlockContent(String blockContent) {
        this.blockContent = blockContent;
    }



    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();
        sb.append(indentStr).append("JinjaBlockNode {% ").append(blockContent).append(" %}");
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
        return super.toString() + " {% " + blockContent + " %}";
    }
}

