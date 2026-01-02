package ast;

/**
 * Represents a <style> block node.
 * Contains CSS content and potentially embedded Jinja expressions.
 */
public class StyleNode extends ASTNode {
    /**
     * Constructs a style node.
     *
     * @param lineNumber the line number where the style block appears
     */
    public StyleNode(int lineNumber) {
        super("Style", lineNumber);
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

