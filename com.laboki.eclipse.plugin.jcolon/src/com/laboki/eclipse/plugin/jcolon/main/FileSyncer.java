package com.laboki.eclipse.plugin.jcolon.main;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.jcolon.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.jcolon.instance.EventBusInstance;
import com.laboki.eclipse.plugin.jcolon.task.Task;

final class FileSyncer extends EventBusInstance {

	protected final IEditorPart editor = EditorContext.getEditor();
	protected boolean completionAssistantIsActive;

	public FileSyncer(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	syncFilesEventHandler(final SyncFilesEvent event) {
		new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public boolean
			shouldSchedule() {
				if (FileSyncer.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public boolean
			shouldRun() {
				if (FileSyncer.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void
			execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
				FileSyncer.this.getEventBus().post(new LocateSemiColonErrorEvent());
			}
		}.begin();
	}

	@Subscribe
	public void
	assistSessionStartedEventHandler(final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
	}

	@Subscribe
	public void
	assistSessionEndedEventHandler(final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}
}
