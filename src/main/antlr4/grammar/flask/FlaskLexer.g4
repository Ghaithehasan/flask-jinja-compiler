lexer grammar FlaskLexer;

// Tokens for indentation (must be declared)
tokens { INDENT, DEDENT }

// Options: We put the complex Java logic in a separate base class for cleanliness.
options { superClass=FlaskLexerBase; }

// @members (نفس السابق، لكن أبقيه كما هو – هو الأساسي. إذا احتجت، أخبرني لأشرح كيف تنشئ FlaskLexerBase.java)

// --- Keywords (مبسطة لـ Flask basics فقط) ---
AND      : 'and' ;
AT       : '@' ;
AS       : 'as' ;
BREAK    : 'break' ;
CLASS    : 'class' ;
CONTINUE : 'continue' ;
DEF      : 'def' ;
DEL      : 'del' ;
IF       : 'if' ;
ELIF     : 'elif' ;
ELSE     : 'else' ;
FALSE    : 'False' ;
FOR      : 'for' ;
FROM     : 'from' ;
GLOBAL   : 'global' ;
IMPORT   : 'import' ;
IN       : 'in' ;
IS       : 'is' ;
NONE     : 'None' ;
NOT      : 'not' ;
OR       : 'or' ;
PASS     : 'pass' ;
RETURN   : 'return' ;
TRUE     : 'True' ;
WHILE    : 'while' ;
WITH     : 'with' ;  // لـ with app.app_context() في Flask

// --- Operators (نفس، لأنها أساسية) ---
GTE         : '>=' ;
LTE         : '<=' ;
NEQ         : '!=' ;
EQ          : '==' ;
POWER       : '**' ;
FLOOR_DIV   : '//' ;
ADD_ASSIGN  : '+=' ;
SUB_ASSIGN  : '-=' ;
MUL_ASSIGN  : '*=' ;
DIV_ASSIGN  : '/=' ;
ELLIPSIS    : '...' ;
ARROW       : '->' ;
ASSIGN      : '=' ;
ADD         : '+' ;
SUB         : '-' ;
MUL         : '*' ;
DIV         : '/' ;
MOD         : '%' ;
GT          : '>' ;
LT          : '<' ;
DOT         : '.' ;

// --- Delimiters (مع brace tracking) ---
LPAREN      : '(' {openBrace();} ;
RPAREN      : ')' {closeBrace();} ;
LBRACK      : '[' {openBrace();} ;
RBRACK      : ']' {closeBrace();} ;
LBRACE      : '{' {openBrace();} ;
RBRACE      : '}' {closeBrace();} ;
COLON       : ':' ;
COMMA       : ',' ;
SEMICOLON   : ';' ;

STRING
    : ('f'|'F'|'r'|'R')? ( SHORT_STRING | LONG_STRING )
    ;

fragment SHORT_STRING
    : '\'' ( STRING_ESCAPE_SEQ | ~[\\\r\n'] )* '\''
    | '"' ( STRING_ESCAPE_SEQ | ~[\\\r\n"] )* '"'
    ;

fragment LONG_STRING
    : '"""' LONG_STRING_ITEM*? '"""'
    | '\'\'\'' LONG_STRING_ITEM*? '\'\'\''
    ;

fragment LONG_STRING_ITEM
    : ~'\\'
    | STRING_ESCAPE_SEQ
    ;

fragment STRING_ESCAPE_SEQ
    : '\\' .
    ;

NUMBER
    : INT ('.' [0-9]+)? EXP?
    ;

fragment INT
    : [0-9]+
    ;

fragment EXP
    : [eE] [+-]? INT
    ;

// --- Identifiers ---
IDENTIFIER : [a-zA-Z_] [a-zA-Z0-9_]* ;

// --- Whitespace, Comments, Newline (نفس السابق، ضروري) ---
NEWLINE
 : ( '\r'? '\n' | '\r' | '\f' ) SPACES?
   {
     onNewLine();
   }
 ;

SKIP_
 : ( SPACES | COMMENT | LINE_JOINING ) -> skip
 ;


fragment SPACES
 : [ \t]+
 ;

fragment COMMENT
 : '#' ~[\r\n\f]*
 ;

fragment LINE_JOINING
 : '\\' SPACES? ( '\r'? '\n' | '\r' | '\f')
 ;

UNKNOWN_CHAR : . ;











//
//lexer grammar flaskLexer;
//
//// Tokens for indentation (must be declared)
//tokens { INDENT, DEDENT }
//
//// Options (for Java target in IntelliJ)
//options { superClass=FlaskLexerBase; }  // You need to create a base class or embed the logic in @members
//
//// Embed Java code for indentation handling
//@lexer::members {
//  // Queue for extra tokens (like INDENT/DEDENT)
//  private java.util.LinkedList<Token> tokens = new java.util.LinkedList<>();
//
//  // Stack for indentation levels
//  private java.util.Stack<Integer> indents = new java.util.Stack<>();
//
//  // Track opened braces (for ignoring indents inside ())
//  private int opened = 0;
//
//  // Last token produced
//  private Token lastToken = null;
//
//  @Override
//  public void emit(Token t) {
//    super.setToken(t);
//    tokens.offer(t);
//  }
//
//  @Override
//  public Token nextToken() {
//    if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
//      // Handle trailing DEDENTs at EOF
//      for (int i = tokens.size() - 1; i >= 0; i--) {
//        if (tokens.get(i).getType() == EOF) {
//          tokens.remove(i);
//        }
//      }
//      this.emit(commonToken(NEWLINE, "\n"));  // End statement
//      while (!indents.isEmpty()) {
//        this.emit(createDedent());
//        indents.pop();
//      }
//      this.emit(commonToken(EOF, "<EOF>"));
//    }
//
//    Token next = super.nextToken();
//    if (next.getChannel() == Token.DEFAULT_CHANNEL) {
//      this.lastToken = next;
//    }
//    return tokens.isEmpty() ? next : tokens.poll();
//  }
//
//  private Token createDedent() {
//    CommonToken dedent = commonToken(DEDENT, "");
//    dedent.setLine(this.lastToken.getLine());
//    return dedent;
//  }
//
//  private CommonToken commonToken(int type, String text) {
//    int stop = this.getCharIndex() - 1;
//    int start = text.isEmpty() ? stop : stop - text.length() + 1;
//    return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
//  }
//
//  // Calculate indent count (handles tabs as 8 spaces)
//  static int getIndentationCount(String spaces) {
//    int count = 0;
//    for (char ch : spaces.toCharArray()) {
//      switch (ch) {
//        case '\t':
//          count += 8 - (count % 8);
//          break;
//        default:
//          count++;
//      }
//    }
//    return count;
//  }
//
//  boolean atStartOfInput() {
//    return getCharPositionInLine() == 0 && getLine() == 1;
//  }
//
//  // Methods to track braces
//  public void openBrace() { this.opened++; }
//  public void closeBrace() { this.opened--; }
//  public void onNewLine() { /* Can be used if needed */ }
//}
//
//// --- Keywords (Expanded for full Python 3 support, relevant to Flask) ---
//AND        : 'and' ;
//AS         : 'as' ;
//ASSERT     : 'assert' ;
//ASYNC      : 'async' ;
//AWAIT      : 'await' ;
//BREAK      : 'break' ;
//CLASS      : 'class' ;
//CONTINUE   : 'continue' ;
//DEF        : 'def' ;
//DEL        : 'del' ;
//ELIF       : 'elif' ;
//ELSE       : 'else' ;
//EXCEPT     : 'except' ;
//FALSE      : 'False' ;
//FINALLY    : 'finally' ;
//FOR        : 'for' ;
//FROM       : 'from' ;
//GLOBAL     : 'global' ;
//IF         : 'if' ;
//IMPORT     : 'import' ;
//IN         : 'in' ;
//IS         : 'is' ;
//LAMBDA     : 'lambda' ;
//NONE       : 'None' ;
//NONLOCAL   : 'nonlocal' ;
//NOT        : 'not' ;
//OR         : 'or' ;
//PASS       : 'pass' ;
//RAISE      : 'raise' ;
//RETURN     : 'return' ;
//TRUE       : 'True' ;
//TRY        : 'try' ;
//WHILE      : 'while' ;
//WITH       : 'with' ;
//YIELD      : 'yield' ;
//
//// --- Operators ---
//GTE         : '>=' ;
//LTE         : '<=' ;
//NEQ         : '!=' ;
//EQ          : '==' ;
//POWER       : '**' ;
//FLOOR_DIV   : '//' ;
//ADD_ASSIGN  : '+=' ;
//SUB_ASSIGN  : '-=' ;
//MUL_ASSIGN  : '*=' ;
//DIV_ASSIGN  : '/=' ;
//ELLIPSIS    : '...' ;
//ARROW       : '->' ;
//ASSIGN      : '=' ;
//ADD         : '+' ;
//SUB         : '-' ;
//MUL         : '*' ;
//DIV         : '/' ;
//MOD         : '%' ;
//GT          : '>' ;
//LT          : '<' ;
//DOT         : '.' ;
//
//// --- Delimiters (with brace tracking) ---
//LPAREN      : '(' {openBrace();} ;
//RPAREN      : ')' {closeBrace();} ;
//LBRACK      : '[' {openBrace();} ;
//RBRACK      : ']' {closeBrace();} ;
//LBRACE      : '{' {openBrace();} ;
//RBRACE      : '}' {closeBrace();} ;
//COLON       : ':' ;
//COMMA       : ',' ;
//SEMICOLON   : ';' ;
//
//// --- Literals ---
//STRING
//    : STRING_PREFIX? ( SHORT_STRING | LONG_STRING )
//    ;
//
//fragment STRING_PREFIX
//    : 'r' | 'u' | 'R' | 'U' | 'f' | 'F' | 'fr' | 'Fr' | 'fR' | 'FR' | 'rf' | 'rF' | 'Rf' | 'RF'
//    | 'b' | 'B' | 'br' | 'Br' | 'bR' | 'BR' | 'rb' | 'rB' | 'Rb' | 'RB'
//    ;
//
//fragment SHORT_STRING
//    : '\'' ( STRING_ESCAPE_SEQ | ~[\\\r\n'] )* '\''
//    | '"' ( STRING_ESCAPE_SEQ | ~[\\\r\n"] )* '"'
//    ;
//
//fragment LONG_STRING
//    : '"""' LONG_STRING_ITEM*? '"""'
//    | '\'\'\'' LONG_STRING_ITEM*? '\'\'\''
//    ;
//
//fragment LONG_STRING_ITEM
//    : LONG_STRING_CHAR | STRING_ESCAPE_SEQ
//    ;
//
//fragment LONG_STRING_CHAR
//    : ~'\\'
//    ;
//
//fragment STRING_ESCAPE_SEQ
//    : '\\' .
//    ;
//
//NUMBER
//    : INTEGER | FLOAT_NUMBER | IMAG_NUMBER
//    ;
//
//fragment INTEGER
//    : DECIMAL_INTEGER | OCT_INTEGER | HEX_INTEGER | BIN_INTEGER
//    ;
//
//fragment DECIMAL_INTEGER
//    : NON_ZERO_DIGIT DIGIT* | '0'+
//    ;
//
//fragment OCT_INTEGER
//    : '0' [oO] OCT_DIGIT+
//    ;
//
//fragment HEX_INTEGER
//    : '0' [xX] HEX_DIGIT+
//    ;
//
//fragment BIN_INTEGER
//    : '0' [bB] BIN_DIGIT+
//    ;
//
//fragment FLOAT_NUMBER
//    : POINT_FLOAT | EXPONENT_FLOAT
//    ;
//
//fragment POINT_FLOAT
//    : INT_PART? FRACTION | INT_PART '.'
//    ;
//
//fragment EXPONENT_FLOAT
//    : ( INT_PART | POINT_FLOAT ) EXPONENT
//    ;
//
//fragment INT_PART
//    : DIGIT+
//    ;
//
//fragment FRACTION
//    : '.' DIGIT+
//    ;
//
//fragment EXPONENT
//    : [eE] [+-]? DIGIT+
//    ;
//
//fragment IMAG_NUMBER
//    : ( FLOAT_NUMBER | INT_PART ) [jJ]
//    ;
//
//fragment DIGIT
//    : [0-9]
//    ;
//
//fragment NON_ZERO_DIGIT
//    : [1-9]
//    ;
//
//fragment OCT_DIGIT
//    : [0-7]
//    ;
//
//fragment HEX_DIGIT
//    : [0-9a-fA-F]
//    ;
//
//fragment BIN_DIGIT
//    : [01]
//    ;
//
//// --- Identifiers ---
//IDENTIFIER : [a-zA-Z_] [a-zA-Z0-9_]* ;
//
//// --- Whitespace, Comments, Newline (with indent logic) ---
//NEWLINE
// : ( {atStartOfInput()}? SPACES
//   | ( '\r'? '\n' | '\r' | '\f' ) SPACES?
//   )
//   {
//     String newLine = getText().replaceAll("[^\\r\\n\\f]+", "");
//     String spaces = getText().replaceAll("[\\r\\n\\f]+", "");
//     int next = _input.LA(1);
//
//     if (opened > 0 || next == '\r' || next == '\n' || next == '\f' || next == '#') {
//       // Ignore if inside braces or blank/comment line
//       skip();
//     }
//     else {
//       emit(commonToken(NEWLINE, newLine));
//
//       int indent = getIndentationCount(spaces);
//       int previous = indents.isEmpty() ? 0 : indents.peek();
//
//       if (indent == previous) {
//         skip();
//       }
//       else if (indent > previous) {
//         indents.push(indent);
//         emit(commonToken(INDENT, spaces));
//       }
//       else {
//         while(!indents.isEmpty() && indents.peek() > indent) {
//           this.emit(createDedent());
//           indents.pop();
//         }
//       }
//     }
//   }
// ;
//
//SKIP_
// : ( SPACES | COMMENT | LINE_JOINING ) -> skip
// ;
//
//fragment SPACES
// : [ \t]+
// ;
//
//fragment COMMENT
// : '#' ~[\r\n\f]*
// ;
//
//fragment LINE_JOINING
// : '\\' SPACES? ( '\r'? '\n' | '\r' | '\f')
// ;
//
//UNKNOWN_CHAR : . ;
//
