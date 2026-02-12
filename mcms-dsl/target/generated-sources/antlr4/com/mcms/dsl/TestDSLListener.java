// Generated from com/mcms/dsl/TestDSL.g4 by ANTLR 4.13.1
package com.mcms.dsl;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TestDSLParser}.
 */
public interface TestDSLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TestDSLParser#dsl}.
	 * @param ctx the parse tree
	 */
	void enterDsl(TestDSLParser.DslContext ctx);
	/**
	 * Exit a parse tree produced by {@link TestDSLParser#dsl}.
	 * @param ctx the parse tree
	 */
	void exitDsl(TestDSLParser.DslContext ctx);
}