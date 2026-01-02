package ast;

/**
 * Represents CSS content within a style block.
 * Contains raw CSS text and may have a child CssStylesheetNode containing
 * the parsed CSS AST structure.
 */
public class CssNode extends ASTNode {
    private String cssContent;

    /**
     * Constructs a CSS node.
     *
     * @param cssContent the CSS content text
     * @param lineNumber the line number where the CSS appears
     */
    public CssNode(String cssContent, int lineNumber) {
        super("Css", lineNumber);
        this.cssContent = cssContent;
    }

    /**
     * Gets the CSS content.
     *
     * @return the CSS content
     */
    public String getCssContent() {
        return cssContent;
    }

    /**
     * Sets the CSS content.
     *
     * @param cssContent the CSS content to set
     */
    public void setCssContent(String cssContent) {
        this.cssContent = cssContent;
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
        if (cssContent != null && cssContent.length() > 0) {
            sb.append(" (raw css)");
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

    @Override
    public String toString() {
        String displayCss = cssContent;
        if (displayCss.length() > 50) {
            displayCss = displayCss.substring(0, 47) + "...";
        }
        return super.toString() + " " + displayCss.replace("\n", "\\n");
    }
}

