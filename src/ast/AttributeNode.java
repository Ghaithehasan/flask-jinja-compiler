package ast;

/**
 * Represents an HTML attribute node.
 * Contains the attribute name and optional value.
 */
public class AttributeNode extends ASTNode {
    private String attributeName;
    private String attributeValue;

    /**
     * Constructs an attribute node.
     *
     * @param attributeName  the name of the attribute
     * @param attributeValue the value of the attribute (can be null for boolean attributes)
     * @param lineNumber     the line number where the attribute appears
     */
    public AttributeNode(String attributeName, String attributeValue, int lineNumber) {
        super("Attribute", lineNumber);
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    /**
     * Gets the attribute name.
     *
     * @return the attribute name
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the attribute value.
     *
     * @return the attribute value (may be null for boolean attributes)
     */
    public String getAttributeValue() {
        return attributeValue;
    }



    @Override
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentBuilder.append("  ");
        }
        String indentStr = indentBuilder.toString();

        sb.append(indentStr).append("AttributeNode");
        if (attributeValue != null) {
            sb.append(" ").append(attributeName).append("=\"").append(attributeValue).append("\"");
        } else {
            sb.append(" ").append(attributeName);
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
        if (attributeValue != null) {
            return super.toString() + " " + attributeName + "=\"" + attributeValue + "\"";
        } else {
            return super.toString() + " " + attributeName;
        }
    }
}

