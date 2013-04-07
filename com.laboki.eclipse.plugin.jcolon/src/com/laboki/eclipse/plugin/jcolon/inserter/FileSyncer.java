package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class FileSyncer {

	private final IEditorPart editor = EditorContext.getEditor();

	public FileSyncer() {}

	@Subscribe
	@AllowConcurrentEvents
	public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
			}
		});
	}
}
