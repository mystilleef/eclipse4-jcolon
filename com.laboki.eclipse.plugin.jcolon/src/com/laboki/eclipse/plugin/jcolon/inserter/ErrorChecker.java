package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.inserter.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

final class ErrorChecker implements Instance {

	private final EventBus eventBus;
	private final IEditorPart editor = EditorContext.getEditor();

	public ErrorChecker(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void checkError(@SuppressWarnings("unused") final CheckErrorEvent event) {
		new AsyncTask(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void asyncExecute() {
				if (this.isInEditMode() || this.doesNotHaveJDTErrors()) return;
				ErrorChecker.this.eventBus.post(new SyncFilesEvent());
			}

			private boolean isInEditMode() {
				return EditorContext.isInEditMode(ErrorChecker.this.editor);
			}

			private boolean doesNotHaveJDTErrors() {
				return !EditorContext.hasJDTErrors(ErrorChecker.this.editor);
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
