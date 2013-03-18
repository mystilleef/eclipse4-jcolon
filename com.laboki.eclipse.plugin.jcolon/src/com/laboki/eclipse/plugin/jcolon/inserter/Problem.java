package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

final class Problem {

	private static final String SEMICOLON = ";";
	private static final List<Integer> PROBLEM_IDS = Arrays.asList(IProblem.ParsingErrorInsertToComplete, IProblem.ParsingErrorInsertToCompletePhrase, IProblem.ParsingErrorInsertToCompleteScope, IProblem.ParsingErrorInsertTokenAfter, IProblem.ParsingErrorInsertTokenBefore);
	private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private final ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(EditorContext.getFile(this.editor));

	public int location() {
		return this.getSemiColonProblem().getSourceEnd() + 1;
	}

	public boolean isMissingSemiColonError() {
		if (this.getSemiColonProblem() == null) return false;
		return true;
	}

	private IProblem getSemiColonProblem() {
		for (final IProblem problem : this.createCompilationUnitNode().getProblems())
			if (this.isValidSemiColonProblem(problem)) return problem;
		return null;
	}

	private CompilationUnit createCompilationUnitNode() {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(this.compilationUnit);
		return (CompilationUnit) parser.createAST(null);
	}

	private boolean isValidSemiColonProblem(final IProblem problem) {
		if (this.lineEndsWithSemiColon(problem)) return false;
		if (Problem.isConstructorDeclaration(problem)) return false;
		if (Problem.isSemiColonProblem(problem)) return true;
		return false;
	}

	private static boolean isConstructorDeclaration(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals("ConstructorDeclaration")) return true;
		return false;
	}

	private boolean lineEndsWithSemiColon(final IProblem problem) {
		try {
			return this.getLineString(problem).endsWith(Problem.SEMICOLON);
		} catch (final BadLocationException e) {
			return false;
		}
	}

	private String getLineString(final IProblem problem) throws BadLocationException {
		final int lineNumber = problem.getSourceLineNumber() - 1;
		return this.document.get(this.document.getLineOffset(lineNumber), this.document.getLineLength(lineNumber)).trim();
	}

	private static boolean isSemiColonProblem(final IProblem problem) {
		return Problem.isInsertionErrorID(problem) && Problem.containsSemiColon(problem);
	}

	private static boolean isInsertionErrorID(final IProblem problem) {
		return Problem.PROBLEM_IDS.contains(problem.getID());
	}

	private static boolean containsSemiColon(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals(Problem.SEMICOLON)) return true;
		return false;
	}
}
