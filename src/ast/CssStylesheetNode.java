package ast;

/**
 * Represents the root of a parsed CSS stylesheet AST.
 * Contains CSS rules as children.
 */
public class CssStylesheetNode extends ASTNode {
    /**
     * Constructs a CSS stylesheet node.
     *
     * @param lineNumber the line number where the stylesheet appears
     */
    public CssStylesheetNode(int lineNumber) {
        super("CssStylesheet", lineNumber);
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

