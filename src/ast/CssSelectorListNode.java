package ast;

/**
 * Represents a list of CSS selectors (e.g., "div, span, p").
 * Contains CssSelectorNode children, one for each selector in the comma-separated list.
 */
public class CssSelectorListNode extends ASTNode {
    /**
     * Constructs a CSS selector list node.
     *
     * @param lineNumber the line number where the selector list appears
     */
    public CssSelectorListNode(int lineNumber) {
        super("CssSelectorList", lineNumber);
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

