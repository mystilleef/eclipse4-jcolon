package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

@ToString
final class SemiColonInserter implements Runnable {

	private static final String SEMICOLON = ";";
	private final Runnable syncFileRunnable = new SyncFileRunnable();
	@Getter private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private final Problem problem = new Problem();

	@Override
	public void run() {
		EditorContext.asyncExec(new ProblemAnnotations(this));
	}

	protected void insertSemiColon() {
		if (this.problem.isMissingSemiColon()) this.tryToInsertSemiColon();
	}

	private void tryToInsertSemiColon() {
		try {
			this.document.replace(this.problem.location(), 0, SemiColonInserter.SEMICOLON);
		} catch (final BadLocationException e) {} finally {
			this.syncFile();
		}
	}

	void syncFile() {
		EditorContext.asyncExec(this.syncFileRunnable);
	}

	private final class SyncFileRunnable implements Runnable {

		public SyncFileRunnable() {}

		@Override
		public void run() {
			EditorContext.syncFile(SemiColonInserter.this.getEditor());
		}
	}
}
