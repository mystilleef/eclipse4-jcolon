package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.events.SemiColonErrorLocationEvent;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

final class Inserter implements Instance {

	private final EventBus eventBus;
	private final Problem problem = new Problem();
	private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private static final String SEMICOLON = ";";

	public Inserter(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void semiColonErrorLocation(final SemiColonErrorLocationEvent event) {
		new AsyncTask(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void asyncExecute() {
				this.insertSemiColon(event.getLocation());
			}

			private void insertSemiColon(final int location) {
				try {
					this.tryToInsertSemiColon(location);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			private void tryToInsertSemiColon(final int location) throws BadLocationException {
				if (this.semiColonIsAlreadyInserted(location)) return;
				if (this.locationErrorMismatch(location)) return;
				if (EditorContext.isInEditMode(Inserter.this.editor)) return;
				EditorContext.flushEvents();
				Inserter.this.document.replace(location, 0, Inserter.SEMICOLON);
			}

			private boolean semiColonIsAlreadyInserted(final int location) throws BadLocationException {
				return String.valueOf(Inserter.this.document.getChar(location)).equals(Inserter.SEMICOLON);
			}

			private boolean locationErrorMismatch(final int location) {
				try {
					return location != Inserter.this.problem.location();
				} catch (final Exception e) {
					return false;
				}
			}
		}.begin();
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		return this;
	}
}
