parser grammar FlaskParser;

options {
    tokenVocab = FlaskLexer;
}

program
    : (statement | NEWLINE)* EOF
    ;

statement
    : simple_statement
    | compound_statement
    ;

simple_statement
    : importStatement 
    | assignmentStatement
    | expression_statement    // function calls: app.run(debug=True)
    ;

expression_statement
    : expression
    ;

compound_statement
    : DOT
    ;
importStatement
    : importNameStatement
    | importFromStatement
    ;


importNameStatement
    : IMPORT dottedName (AS IDENTIFIER)?
    ;
importFromStatement
    : FROM dottedName IMPORT (importList | MUL)
    ;



importList
    : IDENTIFIER (COMMA IDENTIFIER)*
    ;

dottedName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

// =================================================================
// ASSIGNMENT STATEMENT (Simplified for Flask)
// =================================================================

assignmentStatement
    : target (ASSIGN | augmentedAssignmentOp) expression
    ;

target
    : IDENTIFIER (target_trailer)*
    ;

target_trailer
    : DOT IDENTIFIER                 // attribute: x.attr
    | LBRACK expression RBRACK       // indexing: x[0]
    // NO function call!
    ;

augmentedAssignmentOp
    : ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN
    ;

// =================================================================
// EXPRESSIONS (Complete with Precedence)
// =================================================================

expression
    : or_boolean_expression
    ;

// Level 1: OR (lowest precedence)
or_boolean_expression
    : and_boolean_expression (OR and_boolean_expression)*
    ;

// Level 2: AND
and_boolean_expression
    : not_boolean_expression (AND not_boolean_expression)*
    ;

// Level 3: NOT
not_boolean_expression
    : NOT not_boolean_expression
    | comparison_expression
    ;

// Level 4: Comparison
comparison_expression
    : additive_expression (comp_op additive_expression)*
    ;

comp_op
    : EQ | NEQ | LT | GT | LTE | GTE | IN | IS
    ;

// Level 5: Addition/Subtraction
additive_expression
    : multiplicative_expression ((ADD | SUB) multiplicative_expression)*
    ;

// Level 6: Multiplication/Division/Modulo
multiplicative_expression
    : unary_expression ((MUL | DIV | MOD) unary_expression)*
    ;

// Level 7: Unary
unary_expression
    : (ADD | SUB) unary_expression
    | power_expression
    ;

// Level 8: Power (right-associative)
power_expression
    : atom_expression (POWER power_expression)?
    ;

// Level 9: Atom with trailers (highest precedence)
atom_expression
    : atom (trailer)*
    ;

atom
    : IDENTIFIER
    | NUMBER
    | STRING
    | TRUE
    | FALSE
    | NONE
    | LPAREN expression RPAREN
    | LBRACK  expression_list?  RBRACK      // list literal: [1, 2, 3]
    | LBRACE NEWLINE? dict_or_set? NEWLINE? RBRACE
    ;

trailer
    : DOT IDENTIFIER                      // attribute access
    | LPAREN arglist? RPAREN              // function call
    | LBRACK expression RBRACK            // indexing
    ;

// =================================================================
// HELPERS
// =================================================================



dict_or_set
    : dict_items        // {key: value, ...} - dict
    | expression_list  // {1, 2, 3} - set
    ;

dict_items
    : dict_item (COMMA NEWLINE? dict_item)*
    ;

dict_item
    : expression COLON expression  // key: value
    ;
    
expression_list
    : expression (COMMA expression)*
    ;

arglist
    : argument (COMMA argument)*
    ;

argument
    : expression
    | IDENTIFIER ASSIGN expression
    ;
