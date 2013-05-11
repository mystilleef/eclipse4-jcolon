package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

final class ErrorChecker extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();

	public ErrorChecker(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void checkError(@SuppressWarnings("unused") final CheckErrorEvent event) {
		new AsyncTask(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

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
}
