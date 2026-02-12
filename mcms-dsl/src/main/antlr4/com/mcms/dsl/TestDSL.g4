grammar TestDSL;

// Simple test grammar to verify ANTLR plugin works
// This will be replaced with KernelDSL.g4 in Phase 2

// Parser Rules
dsl
    : 'test' ID ';' EOF
    ;

// Lexer Rules
ID
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;
