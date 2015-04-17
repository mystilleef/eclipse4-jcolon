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

	public FileSyncer() {
		super();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	syncFilesEventHandler(final SyncFilesEvent event) {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				if (FileSyncer.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void
			execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
				EventBus.post(new LocateSemiColonErrorEvent());
			}
		}.setFamily(EditorContext.ERROR_CHECKING_TASK)
			.setDelay(EditorContext.SHORT_DELAY_TIME)
			.start();
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
