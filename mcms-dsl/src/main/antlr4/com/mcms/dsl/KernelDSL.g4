/**
 * Kernel DSL v1 Grammar for ANTLR 4
 *
 * Implements the Kernel DSL v1 specification for describing:
 * - Structure: domain, level, model, service, fields
 * - Behavior: states, transitions, invariants
 * - Reactivity: emits (events), do (actions)
 * - Constraints: architecture rules (forbid/allow imports)
 *
 * Source of truth: docs/KERNEL_DSL_v1.txt
 */
grammar KernelDSL;

// ============================================================
// Parser Rules
// ============================================================

compilationUnit
    : domain+ EOF
    ;

domain
    : DOMAIN name=NAME LBRACE domainBody* RBRACE
    ;

domainBody
    : description
    | level
    | constraints
    ;

description
    : DESCRIPTION STRING
    ;

level
    : LEVEL name=NAME LBRACE levelBody+ RBRACE
    ;

levelBody
    : model
    | service
    ;

model
    : MODEL name=NAME LBRACE modelBody+ RBRACE
    ;

modelBody
    : description
    | fields
    | states
    | transitions
    | invariants
    ;

fields
    : FIELDS LBRACE field+ RBRACE
    ;

field
    : name=NAME COLON type
    ;

type
    : typePart+
    ;

typePart
    : NAME
    | LT
    | GT
    | COMMA
    | DOT
    | LBRACKET
    | RBRACKET
    ;

states
    : STATES LBRACE stateName (COMMA? stateName)* RBRACE
    ;

stateName
    : NAME
    ;

transitions
    : TRANSITIONS LBRACE transition+ RBRACE
    ;

transition
    : fromState=NAME ARROW toState=NAME ON trigger=NAME (LPAREN paramList? RPAREN)?
      guardClause?
      elseClause?
      emitsClause?
      doClause?
    ;

guardClause
    : IF conditionExpr
    ;

conditionExpr
    : conditionToken+
    ;

// A condition token is anything that is NOT a keyword that can follow a guard,
// and not structural tokens. This allows arbitrary boolean expressions.
conditionToken
    : NAME
    | STRING
    | EQUALS
    | NOT_EQUALS
    | LT
    | GT
    | LT_EQ
    | GT_EQ
    | AND
    | OR
    | NOT
    | LPAREN
    | RPAREN
    | DOT
    | COMMA
    | PLUS
    | MINUS
    | STAR
    | SLASH
    | INT_LITERAL
    | TRUE
    | FALSE
    ;

elseClause
    : ELSE_KW NAME
    ;

emitsClause
    : EMITS nameList
    ;

doClause
    : DO nameList
    ;

paramList
    : NAME (COMMA NAME)*
    ;

nameList
    : NAME (COMMA NAME)*
    ;

invariants
    : INVARIANTS LBRACE invariantContent* RBRACE
    ;

// Invariants capture everything inside braces as raw content
invariantContent
    : ~RBRACE
    ;

service
    : SERVICE name=NAME LBRACE serviceBody+ RBRACE
    ;

serviceBody
    : description
    | operation
    ;

operation
    : OPERATION name=NAME (LPAREN paramList? RPAREN)?
    ;

constraints
    : CONSTRAINTS LBRACE constraint+ RBRACE
    ;

constraint
    : constraintType=(FORBID | ALLOW) IMPORTS FROM PACKAGE constraintPattern
    ;

// Pattern is a parser rule to avoid lexer conflict with NAME
constraintPattern
    : NAME (DOT NAME)* (DOT STAR)?
    ;

// ============================================================
// Lexer Rules - Keywords (must appear BEFORE NAME)
// ============================================================

DOMAIN      : 'domain' ;
LEVEL       : 'level' ;
MODEL       : 'model' ;
FIELDS      : 'fields' ;
STATES      : 'states' ;
TRANSITIONS : 'transitions' ;
INVARIANTS  : 'invariants' ;
SERVICE     : 'service' ;
OPERATION   : 'operation' ;
CONSTRAINTS : 'constraints' ;
DESCRIPTION : 'description' ;

ON          : 'on' ;
IF          : 'if' ;
ELSE_KW     : 'else' ;
EMITS       : 'emits' ;
DO          : 'do' ;

FORBID      : 'forbid' ;
ALLOW       : 'allow' ;
IMPORTS     : 'imports' ;
FROM        : 'from' ;
PACKAGE     : 'package' ;

TRUE        : 'true' ;
FALSE       : 'false' ;

// ============================================================
// Lexer Rules - Operators and Delimiters
// ============================================================

ARROW       : '->' ;
LBRACE      : '{' ;
RBRACE      : '}' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
LBRACKET    : '[' ;
RBRACKET    : ']' ;
COLON       : ':' ;
COMMA       : ',' ;
DOT         : '.' ;
LT_EQ       : '<=' ;
GT_EQ       : '>=' ;
LT          : '<' ;
GT          : '>' ;
EQUALS      : '==' ;
NOT_EQUALS  : '!=' ;
AND         : '&&' ;
OR          : '||' ;
NOT         : '!' ;
PLUS        : '+' ;
MINUS        : '-' ;
STAR        : '*' ;
SLASH       : '/' ;

// ============================================================
// Lexer Rules - Literals
// ============================================================

INT_LITERAL
    : [0-9]+
    ;

STRING
    : '"' (~["\r\n\\] | '\\' .)* '"'
    ;

NAME
    : [A-Za-z_] [A-Za-z0-9_]*
    ;

// ============================================================
// Lexer Rules - Whitespace and Comments
// ============================================================

WS
    : [ \t\r\n]+ -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;
