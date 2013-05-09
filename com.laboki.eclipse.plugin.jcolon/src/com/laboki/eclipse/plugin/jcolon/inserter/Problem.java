package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IEditorPart;

final class Problem {

	private static final String SEMICOLON = ";";
	private static final List<Integer> PROBLEM_IDS = Arrays.asList(IProblem.ParsingErrorInsertToComplete, IProblem.ParsingErrorInsertToCompletePhrase, IProblem.ParsingErrorInsertToCompleteScope, IProblem.ParsingErrorInsertTokenAfter, IProblem.ParsingErrorInsertTokenBefore);
	private final IEditorPart editor = EditorContext.getEditor();
	private final ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(EditorContext.getFile(this.editor));

	public int location() throws Exception {
		return this.getSemiColonProblem().getSourceEnd() + 1;
	}

	public boolean isMissingSemiColonError() {
		if (this.getSemiColonProblem() == null) return false;
		return true;
	}

	private IProblem getSemiColonProblem() {
		for (final IProblem problem : this.getCompilerProblems())
			if (Problem.isValidSemiColonProblem(problem)) return problem;
		return null;
	}

	private IProblem[] getCompilerProblems() {
		return this.createCompilationUnitNode().getProblems();
	}

	private CompilationUnit createCompilationUnitNode() {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(this.compilationUnit);
		return (CompilationUnit) parser.createAST(null);
	}

	private static boolean isValidSemiColonProblem(final IProblem problem) {
		return Problem.isInsertionErrorID(problem) && Problem.containsSemiColon(problem);
	}

	private static boolean isInsertionErrorID(final IProblem problem) {
		return Problem.PROBLEM_IDS.contains(problem.getID());
	}

	private static boolean containsSemiColon(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (Problem.isSemiColon(string)) return true;
		return false;
	}

	private static boolean isSemiColon(final String string) {
		return string.trim().equals(Problem.SEMICOLON);
	}
}
