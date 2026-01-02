package ast;

/**
 * Represents an HTML element (tag) node.
 * Contains the tag name, attributes, and content.
 */
public class HtmlElementNode extends ASTNode {
    private String tagName;
    private boolean selfClosing;

    /**
     * Constructs an HTML element node.
     *
     * @param tagName     the name of the HTML tag
     * @param lineNumber  the line number where the tag appears
     * @param selfClosing whether this is a self-closing tag
     */
    public HtmlElementNode(String tagName, int lineNumber, boolean selfClosing) {
        super("HtmlElement", lineNumber);
        this.tagName = tagName;
        this.selfClosing = selfClosing;
    }

    /**
     * Gets the tag name.
     *
     * @return the tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Checks if this is a self-closing tag.
     *
     * @return true if self-closing, false otherwise
     */
    public boolean isSelfClosing() {
        return selfClosing;
    }



    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();
        sb.append(indentStr).append("HtmlElementNode <").append(tagName).append(">");
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
        return super.toString() + " <" + tagName + (selfClosing ? "/>" : ">");
    }
}

