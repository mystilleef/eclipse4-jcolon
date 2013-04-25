package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class FileSyncer implements Instance {

	private final IEditorPart editor = EditorContext.getEditor();
	private final EventBus eventBus;

	public FileSyncer(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.TASK_FAMILY_NAME, EditorContext.SHORT_DELAY_TIME) {

			@Override
			protected void execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
			}

			@Override
			protected void postExecute() {
				FileSyncer.this.eventBus.post(new LocateSemiColonErrorEvent());
			}
		});
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
