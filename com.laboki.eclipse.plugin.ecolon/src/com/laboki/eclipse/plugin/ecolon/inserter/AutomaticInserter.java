package com.laboki.eclipse.plugin.ecolon.inserter;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.ecolon.inserter.listeners.IInserterAnnotationModelListenerHandler;
import com.laboki.eclipse.plugin.ecolon.inserter.listeners.InserterAnnotationModelListener;

@ToString
final class AutomaticInserter implements Runnable, IInserterAnnotationModelListenerHandler {

	private static final String SEMICOLON = ";";
	@Getter private final static IEditorPart editor = EditorContext.getEditor();
	private final static ICompilationUnit compilationUnit = JavaCore.getJavaCore().createCompilationUnitFrom(EditorContext.getFile(EditorContext.getEditor()));
	private final InserterAnnotationModelListener listener = new InserterAnnotationModelListener(this);
	private final InserterRunnable inserterRunnable = new InserterRunnable();

	public AutomaticInserter() {}

	@Override
	public void run() {
		this.listener.start();
	}

	@Override
	public void modelChanged() {
		EditorContext.asyncExec(this.inserterRunnable);
	}

	protected static void insertSemiColon(final IProblem problem) {
		if (problem == null) return;
		EditorContext.getBuffer(AutomaticInserter.editor).getContent().replaceTextRange(problem.getSourceEnd() + 1, 0, AutomaticInserter.SEMICOLON);
		EditorContext.syncFile(AutomaticInserter.editor);
	}

	protected static IProblem getSemiColonProblem() {
		for (final IProblem problem : AutomaticInserter.createCompilationUnitNode().getProblems())
			if (problem.getID() == IProblem.ParsingErrorInsertToComplete) return AutomaticInserter._getSemicolonProblem(problem);
		return null;
	}

	private static IProblem _getSemicolonProblem(final IProblem problem) {
		if (!problem.getArguments()[0].equals(AutomaticInserter.SEMICOLON)) return null;
		return problem;
	}

	private static CompilationUnit createCompilationUnitNode() {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(AutomaticInserter.compilationUnit);
		return (CompilationUnit) parser.createAST(null);
	}

	private final class InserterRunnable implements Runnable {

		public InserterRunnable() {}

		@Override
		public void run() {
			if (!EditorContext.hasErrors(AutomaticInserter.getEditor())) return;
			AutomaticInserter.insertSemiColon(AutomaticInserter.getSemiColonProblem());
		}
	}
}
