package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

@ToString
final class SemiColonInserter implements Runnable {

	private static final String SEMICOLON = ";";
	@Getter private final Problem problem = new Problem();
	private final FileSyncer syncer = new FileSyncer();
	private final IDocument document = EditorContext.getDocument(EditorContext.getEditor());
	private final Runnable insertSemiColonRunnable = new InsertSemiColonRunnable();

	@Override
	public void run() {
		EditorContext.asyncExec(new ProblemAnnotations(this));
	}

	void insertSemiColon() {
		EditorContext.asyncExec(this.insertSemiColonRunnable);
	}

	protected void tryToInsertSemiColon() {
		try {
			this.insertSemiColonInDocument();
		} catch (final BadLocationException e) {} finally {
			EditorContext.asyncExec(this.syncer);
		}
	}

	private void insertSemiColonInDocument() throws BadLocationException {
		this.document.replace(this.problem.location(), 0, SemiColonInserter.SEMICOLON);
	}

	private final class InsertSemiColonRunnable implements Runnable {

		public InsertSemiColonRunnable() {}

		@Override
		public void run() {
			if (this.isMissingSemiColon()) this.tryToInsertSemiColon();
		}

		private boolean isMissingSemiColon() {
			return SemiColonInserter.this.getProblem().isMissingSemiColon();
		}

		private void tryToInsertSemiColon() {
			SemiColonInserter.this.tryToInsertSemiColon();
		}
	}
}
