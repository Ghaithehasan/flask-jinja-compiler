parser grammar FlaskParser;

options {
    tokenVocab = FlaskLexer;
}


program
    : (NEWLINE | statement)* EOF
    ;


statement
    : simple_statement
    | compound_statement
    ;

// ============================================================================
// SIMPLE STATEMENTS (التعديل 1: أضف NEWLINE)
// ============================================================================

simple_statement
    : small_stmt NEWLINE  // ← التعديل: أضف NEWLINE هنا
    ;

small_stmt
    : importStatement
    | assignmentStatement
    | expression_statement
    | returnStatement
    | passStatement
    | breakStatement
    | continueStatement
    | delStatement
    | assertStatement
    | globalStatement
    ;

returnStatement
    : RETURN expression?
    ;

passStatement
    : PASS
    ;

breakStatement
    : BREAK
    ;

continueStatement
    : CONTINUE
    ;

delStatement
    : DEL targetList
    ;

targetList
    : target (COMMA target)*
    ;

assertStatement
    : ASSERT expression (COMMA expression)?
    ;

globalStatement
    : GLOBAL IDENTIFIER (COMMA IDENTIFIER)*
    ;


expression_statement
    : expression
    ;

// ============================================================================
// COMPOUND STATEMENTS
// ============================================================================

compound_statement
    : decoratedDef
    | functionDef
    ;

decoratedDef
    : decorator+ functionDef
    ;

decorator
    : AT dottedName (LPAREN arglist? RPAREN)? NEWLINE
    ;

functionDef
    : DEF IDENTIFIER LPAREN parameters? RPAREN COLON suite  // ← استخدم suite
    ;

parameters
    : parameter (COMMA parameter)*
    ;

parameter
    : IDENTIFIER
    ;

// ============================================================================
// SUITE & BLOCK (التعديل 2: أضف suite)
// ============================================================================

suite
    : simple_statement                    // ← one-liner: def foo(): return 5
    | NEWLINE INDENT statement+ DEDENT    // ← block
    ;

block
    : suite  // ← استخدم suite بدلاً من تعريف مباشر
    ;

// ============================================================================
// IMPORT STATEMENTS
// ============================================================================

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

// ============================================================================
// ASSIGNMENT STATEMENT
// ============================================================================

assignmentStatement
    : target (ASSIGN | augmentedAssignmentOp) expression
    ;

target
    : IDENTIFIER (target_trailer)*
    ;

target_trailer
    : DOT IDENTIFIER
    | LBRACK expression RBRACK
    ;

augmentedAssignmentOp
    : ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN
    ;

// ============================================================================
// EXPRESSIONS
// ============================================================================

expression
    : or_boolean_expression
    ;

or_boolean_expression
    : and_boolean_expression (OR and_boolean_expression)*
    ;

and_boolean_expression
    : not_boolean_expression (AND not_boolean_expression)*
    ;

not_boolean_expression
    : NOT not_boolean_expression
    | comparison_expression
    ;

comparison_expression
    : additive_expression (comp_op additive_expression)*
    ;

comp_op
    : EQ | NEQ | LT | GT | LTE | GTE | IN | IS
    ;

additive_expression
    : multiplicative_expression ((ADD | SUB) multiplicative_expression)*
    ;

multiplicative_expression
    : unary_expression ((MUL | DIV | MOD) unary_expression)*
    ;

unary_expression
    : (ADD | SUB) unary_expression
    | power_expression
    ;

power_expression
    : atom_expression (POWER power_expression)?
    ;

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
    | LBRACK expression_list? RBRACK
    | LBRACE NEWLINE? dict_or_set? NEWLINE? RBRACE
    ;

trailer
    : DOT IDENTIFIER
    | LPAREN arglist? RPAREN
    | LBRACK expression RBRACK
    ;

// ============================================================================
// HELPERS
// ============================================================================

dict_or_set
    : dict_items
    | expression_list
    ;

dict_items
    : dict_item (COMMA NEWLINE? dict_item)*
    ;

dict_item
    : expression COLON expression
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