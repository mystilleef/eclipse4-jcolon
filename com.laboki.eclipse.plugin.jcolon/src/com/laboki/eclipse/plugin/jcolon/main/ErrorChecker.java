package com.laboki.eclipse.plugin.jcolon.main;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.jcolon.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

final class ErrorChecker extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();
	private boolean completionAssistantIsActive;

	public ErrorChecker(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void checkErrorEventHandler(@SuppressWarnings("unused") final CheckErrorEvent event) {
		new AsyncTask(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public boolean shouldSchedule() {
				if (ErrorChecker.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public boolean shouldRun() {
				if (ErrorChecker.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void asyncExecute() {
				if (this.canPostEvent()) this.postEvent();
			}

			private boolean canPostEvent() {
				return this.isNotInEditMode() || this.hasJDTErrors();
			}

			private boolean isNotInEditMode() {
				return !EditorContext.isInEditMode(ErrorChecker.this.editor);
			}

			private boolean hasJDTErrors() {
				return EditorContext.hasJDTErrors(ErrorChecker.this.editor);
			}

			private void postEvent() {
				ErrorChecker.this.eventBus.post(new SyncFilesEvent());
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
