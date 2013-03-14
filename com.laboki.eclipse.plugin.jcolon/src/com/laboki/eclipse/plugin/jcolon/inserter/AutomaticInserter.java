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
	@Getter private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private final ICompilationUnit compilationUnit = JavaCore.getJavaCore().createCompilationUnitFrom(EditorContext.getFile(this.editor));
	private final InserterAnnotationModelListener listener = new InserterAnnotationModelListener(this);
	private final InserterRunnable inserterRunnable = new InserterRunnable();
	@SuppressWarnings("boxing") private final static List<Integer> PROBLEM_IDS = Arrays.asList(IProblem.ParsingErrorInsertToComplete, IProblem.ParsingErrorInsertToCompletePhrase, IProblem.ParsingErrorInsertToCompleteScope, IProblem.ParsingErrorInsertTokenAfter, IProblem.ParsingErrorInsertTokenBefore);

	public AutomaticInserter() {}

	@Override
	public void run() {
		this.listener.start();
	}

	@Override
	public void modelChanged() {
		EditorContext.asyncExec(this.inserterRunnable);
	}

	protected void insertSemiColon(final IProblem problem) {
		if (problem == null) return;
		this.tryToInsertSemiColon(problem);
	}

	private void tryToInsertSemiColon(final IProblem problem) {
		try {
			this.document.replace(problem.getSourceEnd() + 1, 0, AutomaticInserter.SEMICOLON);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		} finally {
			EditorContext.syncFile(this.editor);
		}
	}

	protected IProblem getSemiColonProblem() {
		for (final IProblem problem : this.createCompilationUnitNode().getProblems())
			if (AutomaticInserter.isSemiColonProblem(problem)) return problem;
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

	private CompilationUnit createCompilationUnitNode() {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(this.compilationUnit);
		return (CompilationUnit) parser.createAST(null);
	}

	private final class InserterRunnable implements Runnable {

		public InserterRunnable() {}

		@Override
		public void run() {
			if (!EditorContext.hasErrors(AutomaticInserter.this.getEditor())) return;
			AutomaticInserter.this.insertSemiColon(AutomaticInserter.this.getSemiColonProblem());
		}
	}
}
