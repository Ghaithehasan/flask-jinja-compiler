package ast;

/**
 * Represents a text content node.
 * Contains raw text content from the HTML document.
 */
public class TextNode extends ASTNode {
    private String text;

    /**
     * Constructs a text node.
     *
     * @param text       the text content
     * @param lineNumber the line number where the text appears
     */
    public TextNode(String text, int lineNumber) {
        super("Text", lineNumber);
        this.text = text;
    }

    /**
     * Gets the text content.
     *
     * @return the text content
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content.
     *
     * @param text the text content to set
     */
    public void setText(String text) {
        this.text = text;
    }



    @Override
    public String printTree(int indent) {
        // Filter out whitespace-only text (spaces, \r\n, etc.)
        String trimmedText = text != null ? text.trim() : "";
        
        // If text is empty or only whitespace, don't print anything
        if (trimmedText.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();

        sb.append(indentStr).append("TextNode");
        String displayText = trimmedText;
        // Truncate long text for display
        if (displayText.length() > 50) {
            displayText = displayText.substring(0, 47) + "...";
        }
        sb.append(" \"").append(displayText).append("\"");
        
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
        String displayText = text;
        if (displayText.length() > 50) {
            displayText = displayText.substring(0, 47) + "...";
        }
        return super.toString() + " \"" + displayText.replace("\n", "\\n") + "\"";
    }
}

