package symbol;

/**
 * Represents a symbol entry in the symbol table.
 * Stores information about symbols found in the source code (HTML tags, attributes,
 * CSS selectors, CSS properties, Jinja blocks, variables, etc.).
 */
public class Symbol {
    private String name;
    private SymbolType type;
    private String scope;
    private int lineNumber;

    /**
     * Constructs a symbol with the given properties.
     *
     * @param name       the name of the symbol
     * @param type       the type of the symbol
     * @param scope      the scope where the symbol is defined
     * @param lineNumber the line number where the symbol appears
     */
    public Symbol(String name, SymbolType type, String scope, int lineNumber) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the symbol name.
     *
     * @return the symbol name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the symbol type.
     *
     * @return the symbol type
     */
    public SymbolType getType() {
        return type;
    }

    /**
     * Gets the scope where the symbol is defined.
     *
     * @return the scope name
     */
    public String getScope() {
        return scope;
    }

    /**
     * Gets the line number where the symbol appears.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the scope of the symbol.
     *
     * @param scope the scope name to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] in scope '%s' at line %d", name, type, scope, lineNumber);
    }
}

