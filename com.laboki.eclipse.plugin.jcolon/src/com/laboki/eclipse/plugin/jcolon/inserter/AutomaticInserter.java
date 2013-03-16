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
final class AutomaticInserter implements Runnable, IInserterAnnotationModelListenerHandler {

	private static final String SEMICOLON = ";";
	private final Runnable inserterRunnable = new InserterRunnable();
	private final Runnable syncFileRunnable = new SyncFileRunnable();
	@Getter private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private final ICompilationUnit compilationUnit = JavaCore.getJavaCore().createCompilationUnitFrom(EditorContext.getFile(this.editor));
	private final InserterAnnotationModelListener listener = new InserterAnnotationModelListener(this);
	@SuppressWarnings("boxing") private final static List<Integer> PROBLEM_IDS = Arrays.asList(IProblem.ParsingErrorInsertToComplete, IProblem.ParsingErrorInsertToCompletePhrase, IProblem.ParsingErrorInsertToCompleteScope, IProblem.ParsingErrorInsertTokenAfter, IProblem.ParsingErrorInsertTokenBefore);

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
		AutomaticInserter.debugProblem(problem);
		this.tryToInsertSemiColon(problem);
	}

	private static void debugProblem(final IProblem problem) {
		System.out.println("*********************************************");
		System.out.println(problem.getID());
		System.out.println(problem.getMessage());
		for (final String string : problem.getArguments())
			System.out.println(string);
		System.out.println("*********************************************");
	}

	private void tryToInsertSemiColon(final IProblem problem) {
		try {
			this.document.replace(problem.getSourceEnd() + 1, 0, AutomaticInserter.SEMICOLON);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		} finally {
			this.syncFile();
		}
	}

	protected boolean hasJDTErrors() {
		this.syncFile();
		return EditorContext.hasJDTErrors(this.editor);
	}

	private void syncFile() {
		EditorContext.asyncExec(this.syncFileRunnable);
	}

	private boolean lineEndsWithSemiColon(final IProblem problem) {
		try {
			return this.getLineString(problem).endsWith(AutomaticInserter.SEMICOLON);
		} catch (final BadLocationException e) {
			return false;
		}
	}

	private String getLineString(final IProblem problem) throws BadLocationException {
		final int lineNumber = problem.getSourceLineNumber() - 1;
		return this.document.get(this.document.getLineOffset(lineNumber), this.document.getLineLength(lineNumber)).trim();
	}

	protected IProblem getSemiColonProblem() {
		for (final IProblem problem : this.createCompilationUnitNode().getProblems()) {
			if (AutomaticInserter.isConstructorDeclaration(problem)) continue;
			if (this.lineEndsWithSemiColon(problem)) continue;
			if (AutomaticInserter.isSemiColonProblem(problem)) return problem;
		}
		return null;
	}

	private static boolean isSemiColonProblem(final IProblem problem) {
		return AutomaticInserter.isInsertionErrorID(problem) && AutomaticInserter.containsSemiColon(problem);
	}

	@SuppressWarnings("boxing")
	private static boolean isInsertionErrorID(final IProblem problem) {
		return AutomaticInserter.PROBLEM_IDS.contains(problem.getID());
	}

	private static boolean containsSemiColon(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals(AutomaticInserter.SEMICOLON)) return true;
		return false;
	}

	private static boolean isConstructorDeclaration(final IProblem problem) {
		for (final String string : problem.getArguments())
			if (string.trim().equals("ConstructorDeclaration")) return true;
		return false;
	}

	private CompilationUnit createCompilationUnitNode() {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(this.compilationUnit);
		return (CompilationUnit) parser.createAST(null);
	}

	private final class InserterRunnable implements Runnable {

		public InserterRunnable() {}

		@Override
		public void run() {
			if (!AutomaticInserter.this.hasJDTErrors()) return;
			AutomaticInserter.this.insertSemiColon(AutomaticInserter.this.getSemiColonProblem());
		}
	}

	private final class SyncFileRunnable implements Runnable {

		public SyncFileRunnable() {}

		@Override
		public void run() {
			EditorContext.syncFile(AutomaticInserter.this.getEditor());
		}
	}
}
