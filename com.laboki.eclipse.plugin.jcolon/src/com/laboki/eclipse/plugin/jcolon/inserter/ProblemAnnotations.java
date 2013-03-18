package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;

import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterAnnotationModelListenerHandler;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterListener;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.InserterAnnotationModelListener;

final class ProblemAnnotations implements Runnable, IInserterAnnotationModelListenerHandler {

	@Getter private final SemiColonInserter inserter;
	private final Runnable inserterRunnable = new InserterRunnable();
	private final IInserterListener listener = new InserterAnnotationModelListener(this);

	public ProblemAnnotations(final SemiColonInserter inserter) {
		this.inserter = inserter;
	}

	@Override
	public void run() {
		this.listener.start();
	}

	@Override
	public void annotationModelChanged() {
		EditorContext.asyncExec(this.inserterRunnable);
	}

	private final class InserterRunnable implements Runnable {

		private final Problem problem = new Problem();
		private final FileSyncer syncer = new FileSyncer();
		private final IEditorPart editor = EditorContext.getEditor();

		public InserterRunnable() {}

		@Override
		public void run() {
			if (this.hasMissingSemiColonError()) this.insertSemiColon();
		}

		private boolean hasMissingSemiColonError() {
			return this.hasJDTErrors() && this.problem.isMissingSemiColonError();
		}

		private boolean hasJDTErrors() {
			EditorContext.asyncExec(this.syncer);
			return EditorContext.hasJDTErrors(this.editor);
		}

		private void insertSemiColon() {
			ProblemAnnotations.this.getInserter().insertSemiColon();
		}
	}
}
