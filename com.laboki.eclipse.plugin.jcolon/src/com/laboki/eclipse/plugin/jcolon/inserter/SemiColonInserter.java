package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.ToString;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

@ToString
final class SemiColonInserter implements Runnable {

	private static final String SEMICOLON = ";";
	private final Problem problem = new Problem();
	private final FileSyncer syncer = new FileSyncer();
	private final IDocument document = EditorContext.getDocument(EditorContext.getEditor());

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
			EditorContext.asyncExec(this.syncer);
		}
	}
}
