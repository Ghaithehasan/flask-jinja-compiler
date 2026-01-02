package ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all AST nodes.
 * Provides common functionality including node name, line number, and children management.
 * Supports the Visitor pattern for tree traversal and operations.
 */
public abstract class ASTNode {
    protected String nodeName;
    protected int lineNumber;
    protected List<ASTNode> children;

    /**
     * Constructs an AST node with the given node name and line number.
     *
     * @param nodeName   the name/type of the node
     * @param lineNumber the line number where this node appears in the source
     */
    public ASTNode(String nodeName, int lineNumber) {
        this.nodeName = nodeName;
        this.lineNumber = lineNumber;
        this.children = new ArrayList<>();
    }

    /**
     * Gets the node name/type.
     *
     * @return the node name
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Gets the line number where this node appears.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Adds a child node to this node.
     *
     * @param child the child node to add
     */
    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    /**
     * Adds multiple child nodes to this node.
     *
     * @param children the list of child nodes to add
     */
    public void addChildren(List<ASTNode> children) {
        if (children != null) {
            for (ASTNode child : children) {
                if (child != null) {
                    this.children.add(child);
                }
            }
        }
    }

    /**
     * Gets the list of child nodes.
     *
     * @return the list of children
     */
    public List<ASTNode> getChildren() {
        return children;
    }

    /**
     * Pretty-prints the AST tree structure with indentation.
     *
     * @param indent the current indentation level
     * @return a string representation of the tree
     */
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

    /**
     * Pretty-prints the AST tree structure starting from root level.
     *
     * @return a string representation of the tree
     */
    public String printTree() {
        return printTree(0);
    }

    @Override
    public String toString() {
        return nodeName + " [line: " + lineNumber + "]";
    }
}

