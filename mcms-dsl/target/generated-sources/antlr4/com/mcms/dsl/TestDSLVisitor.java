// Generated from com/mcms/dsl/TestDSL.g4 by ANTLR 4.13.1
package com.mcms.dsl;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TestDSLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TestDSLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TestDSLParser#dsl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDsl(TestDSLParser.DslContext ctx);
}