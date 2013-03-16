package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterAnnotationModelListenerHandler;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterListener;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.InserterAnnotationModelListener;

@ToString
final class SemiColonInserter implements Runnable, IInserterAnnotationModelListenerHandler {

	private static final String SEMICOLON = ";";
	private final Runnable inserterRunnable = new InserterRunnable();
	private final Runnable syncFileRunnable = new SyncFileRunnable();
	@Getter private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private final IInserterListener listener = new InserterAnnotationModelListener(this);
	private final Problem problem = new Problem();

	@Override
	public void run() {
		this.listener.start();
	}

	@Override
	public void annotationModelChanged() {
		EditorContext.asyncExec(this.inserterRunnable);
	}

	protected void insertSemiColon() {
		if (this.problem.isMissingSemiColon()) this.tryToInsertSemiColon();
	}

	private void tryToInsertSemiColon() {
		try {
			this.document.replace(this.problem.location() + 1, 0, SemiColonInserter.SEMICOLON);
		} catch (final BadLocationException e) {} finally {
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

	private final class InserterRunnable implements Runnable {

		public InserterRunnable() {}

		@Override
		public void run() {
			if (!SemiColonInserter.this.hasJDTErrors()) return;
			SemiColonInserter.this.insertSemiColon();
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
