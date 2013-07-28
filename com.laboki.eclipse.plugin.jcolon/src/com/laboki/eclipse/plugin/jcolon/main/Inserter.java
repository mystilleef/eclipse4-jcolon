package com.laboki.eclipse.plugin.jcolon.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.jcolon.events.SemiColonErrorLocationEvent;
import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

final class Inserter extends AbstractEventBusInstance {

	private static final Logger LOGGER = Logger.getLogger(Inserter.class.getName());
	private static final String SEMICOLON = ";";
	private final Problem problem = new Problem();
	private final IEditorPart editor = EditorContext.getEditor();
	private final IDocument document = EditorContext.getDocument(this.editor);
	private boolean completionAssistantIsActive;

	public Inserter(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void semiColonErrorLocationEventHandler(final SemiColonErrorLocationEvent event) {
		new AsyncTask(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public boolean shouldSchedule() {
				if (Inserter.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public boolean shouldRun() {
				if (Inserter.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void asyncExecute() {
				this.insertSemiColon(event.getLocation());
			}

			private void insertSemiColon(final int location) {
				try {
					this.tryToInsertSemiColon(location);
				} catch (final Exception e) {
					Inserter.LOGGER.log(Level.WARNING, e.getMessage(), e);
				}
			}

			private void tryToInsertSemiColon(final int location) throws Exception {
				if (this.cannotInsertSemiColon(location)) return;
				Inserter.this.document.replace(location, 0, Inserter.SEMICOLON);
			}

			private boolean cannotInsertSemiColon(final int location) throws Exception {
				return this.semiColonIsAlreadyInserted(location) || this.locationErrorMismatch(location) || EditorContext.isInEditMode(Inserter.this.editor);
			}

			private boolean semiColonIsAlreadyInserted(final int location) throws Exception {
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

	@Subscribe
	public void assistSessionStartedEventHandler(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
	}

	@Subscribe
	public void assistSessionEndedEventHandler(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}
}
