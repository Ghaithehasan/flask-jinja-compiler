package symbol;

import java.util.*;

/**
 * Symbol table implementation using a stack-based approach for scope management.
 * Supports nested scopes and symbol lookups with proper scoping rules.
 */
public class SymbolTable {
    // Stack of scopes, where each scope is a map from symbol name to Symbol
    private Stack<Map<String, Symbol>> scopeStack;
    
    // Stack of scope names to track scope hierarchy
    private Stack<String> scopeNameStack;
    
    // List of all symbols in order of insertion (for printing)
    private List<Symbol> allSymbols;
    
    // Current scope name (for tracking the active scope)
    private String currentScopeName;

    /**
     * Constructs an empty symbol table with a global scope.
     */
    public SymbolTable() {
        this.scopeStack = new Stack<>();
        this.scopeNameStack = new Stack<>();
        this.allSymbols = new ArrayList<>();
        this.currentScopeName = "global";
        // Initialize with global scope
        enterScope("global");
    }

    /**
     * Enters a new scope, pushing it onto the scope stack.
     *
     * @param scopeName the name of the new scope
     */
    public void enterScope(String scopeName) {
        scopeStack.push(new HashMap<>());
        scopeNameStack.push(scopeName);
        this.currentScopeName = scopeName;
    }

    /**
     * Exits the current scope, popping it from the scope stack.
     * Does nothing if only the global scope remains.
     */
    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
            scopeNameStack.pop();
            // Restore the previous scope name
            if (!scopeNameStack.isEmpty()) {
                this.currentScopeName = scopeNameStack.peek();
            } else {
                this.currentScopeName = "global";
            }
        }
    }

    /**
     * Gets the current scope name.
     *
     * @return the current scope name
     */
    public String getCurrentScopeName() {
        return currentScopeName;
    }

    /**
     * Sets the current scope name.
     *
     * @param scopeName the scope name to set
     */
    public void setCurrentScopeName(String scopeName) {
        this.currentScopeName = scopeName;
    }

    /**
     * Defines a symbol in the current scope.
     * If a symbol with the same name already exists in the current scope,
     * it will be overwritten.
     *
     * @param symbol the symbol to define
     */
    public void define(Symbol symbol) {
        if (scopeStack.isEmpty()) {
            enterScope("global");
        }
        Map<String, Symbol> currentScope = scopeStack.peek();
        // Update symbol's scope to match current scope
        symbol.setScope(currentScopeName);
        currentScope.put(symbol.getName(), symbol);
        allSymbols.add(symbol);
    }

    /**
     * Looks up a symbol by name, searching from the current scope up to the global scope.
     * Returns the first matching symbol found in the closest scope.
     *
     * @param name the name of the symbol to lookup
     * @return the symbol if found, null otherwise
     */
    public Symbol lookup(String name) {
        // Search from current scope to global scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, Symbol> scope = scopeStack.get(i);
            Symbol symbol = scope.get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    /**
     * Looks up a symbol in the current scope only (does not search parent scopes).
     *
     * @param name the name of the symbol to lookup
     * @return the symbol if found in current scope, null otherwise
     */
    public Symbol lookupCurrentScope(String name) {
        if (scopeStack.isEmpty()) {
            return null;
        }
        return scopeStack.peek().get(name);
    }

    /**
     * Gets all symbols in the symbol table.
     *
     * @return a list of all symbols
     */
    public List<Symbol> getAllSymbols() {
        return new ArrayList<>(allSymbols);
    }

    /**
     * Gets the number of scopes currently active.
     *
     * @return the number of scopes
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }

    /**
     * Prints the symbol table in a readable, well-formatted manner.
     * Groups symbols by scope and displays them hierarchically.
     *
     * @return a formatted string representation of the symbol table
     */
    public String printSymbolTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Symbol Table\n");
        sb.append("============\n\n");

        if (allSymbols.isEmpty()) {
            sb.append("(Empty symbol table)\n");
            return sb.toString();
        }

        // Group symbols by scope
        Map<String, List<Symbol>> symbolsByScope = new LinkedHashMap<>();
        for (Symbol symbol : allSymbols) {
            String scope = symbol.getScope();
            symbolsByScope.putIfAbsent(scope, new ArrayList<>());
            symbolsByScope.get(scope).add(symbol);
        }

        // Print symbols grouped by scope
        for (Map.Entry<String, List<Symbol>> entry : symbolsByScope.entrySet()) {
            String scopeName = entry.getKey();
            List<Symbol> symbols = entry.getValue();

            sb.append("Scope: ").append(scopeName).append("\n");
            sb.append("  ");
            for (int i = 0; i < 50; i++) {
                sb.append("-");
            }
            sb.append("\n");

            // Sort symbols by line number for better readability
            symbols.sort(Comparator.comparingInt(Symbol::getLineNumber));

            for (Symbol symbol : symbols) {
                sb.append(String.format("  %-20s %-18s line %-5d\n",
                    symbol.getName(),
                    "[" + symbol.getType().getDisplayName() + "]",
                    symbol.getLineNumber()));
            }

            sb.append("\n");
        }

        sb.append("Total symbols: ").append(allSymbols.size()).append("\n");
        sb.append("Total scopes: ").append(symbolsByScope.size()).append("\n");

        return sb.toString();
    }
}

