package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.ToString;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SemiColonErrorLocationEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

@ToString
final class SemiColonInserter {

	private final EventBus eventBus;
	private final IDocument document = EditorContext.getDocument(EditorContext.getEditor());
	private static final String SEMICOLON = ";";

	public SemiColonInserter(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void semiColonErrorLocation(final SemiColonErrorLocationEvent event) {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				SemiColonInserter.this.tryToInsertSemiColon(event.getLocation());
				SemiColonInserter.this.postEvent();
			}
		});
	}

	private void tryToInsertSemiColon(final int location) {
		try {
			this.insertSemiColonInDocument(location);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void insertSemiColonInDocument(final int location) throws BadLocationException {
		if (String.valueOf(this.document.getChar(location)).equals(SemiColonInserter.SEMICOLON)) return;
		SemiColonInserter.this.document.replace(location, 0, SemiColonInserter.SEMICOLON);
	}

	protected void postEvent() {
		this.eventBus.post(new SyncFilesEvent());
	}
}
