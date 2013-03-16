package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterAnnotationModelListenerHandler;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.InserterAnnotationModelListener;

@ToString
final class SemiColonInserter implements Runnable, IInserterAnnotationModelListenerHandler {

	private static final String SEMICOLON = ";";
	private static final List<Integer> PROBLEM_IDS = Arrays.asList(IProblem.ParsingErrorInsertToComplete, IProblem.ParsingErrorInsertToCompletePhrase, IProblem.ParsingErrorInsertToCompleteScope, IProblem.ParsingErrorInsertTokenAfter, IProblem.ParsingErrorInsertTokenBefore);
	private final Runnable inserterRunnable = new InserterRunnable();
	private final Runnable syncFileRunnable = new SyncFileRunnable();
	@Getter private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private final ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(EditorContext.getFile(this.editor));
	private final InserterAnnotationModelListener listener = new InserterAnnotationModelListener(this);

	@Override
	public void run() {
		this.listener.start();
	}

	@Override
	public void annotationModelChanged() {
		EditorContext.asyncExec(this.inserterRunnable);
	}

	protected void insertSemiColon(final IProblem problem) {
		if (problem == null) return;
		this.tryToInsertSemiColon(problem);
	}

	private void tryToInsertSemiColon(final IProblem problem) {
		try {
			this.document.replace(problem.getSourceEnd() + 1, 0, SemiColonInserter.SEMICOLON);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		} finally {
			this.syncFile();
		}
	}

	protected IProblem getSemiColonProblem() {
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
		if (SemiColonInserter.isConstructorDeclaration(problem)) return false;
		if (this.lineEndsWithSemiColon(problem)) return false;
		if (SemiColonInserter.isSemiColonProblem(problem)) return true;
		return false;
	}

	private static boolean isConstructorDeclaration(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals("ConstructorDeclaration")) return true;
		return false;
	}

	private boolean lineEndsWithSemiColon(final IProblem problem) {
		try {
			return this.getLineString(problem).endsWith(SemiColonInserter.SEMICOLON);
		} catch (final BadLocationException e) {
			return false;
		}
	}

	private String getLineString(final IProblem problem) throws BadLocationException {
		final int lineNumber = problem.getSourceLineNumber() - 1;
		return this.document.get(this.document.getLineOffset(lineNumber), this.document.getLineLength(lineNumber)).trim();
	}

	private static boolean isSemiColonProblem(final IProblem problem) {
		return SemiColonInserter.isInsertionErrorID(problem) && SemiColonInserter.containsSemiColon(problem);
	}

	private static boolean isInsertionErrorID(final IProblem problem) {
		return SemiColonInserter.PROBLEM_IDS.contains(problem.getID());
	}

	private static boolean containsSemiColon(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals(SemiColonInserter.SEMICOLON)) return true;
		return false;
	}

	protected boolean hasJDTErrors() {
		this.syncFile();
		return EditorContext.hasJDTErrors(this.editor);
	}

	private void syncFile() {
		EditorContext.asyncExec(this.syncFileRunnable);
	}

	private final class InserterRunnable implements Runnable {

		public InserterRunnable() {}

		@Override
		public void run() {
			if (!SemiColonInserter.this.hasJDTErrors()) return;
			SemiColonInserter.this.insertSemiColon(SemiColonInserter.this.getSemiColonProblem());
		}
	}

	private final class SyncFileRunnable implements Runnable {

		public SyncFileRunnable() {}

		@Override
		public void run() {
			EditorContext.syncFile(SemiColonInserter.this.getEditor());
		}
	}
}
