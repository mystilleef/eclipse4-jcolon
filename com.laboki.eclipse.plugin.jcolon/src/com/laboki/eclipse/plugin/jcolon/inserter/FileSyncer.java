package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class FileSyncer {

	@Getter private final IEditorPart editor = EditorContext.getEditor();

	public FileSyncer() {}

	@Subscribe
	@AllowConcurrentEvents
	private void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				EditorContext.syncFile(FileSyncer.this.editor);;
			}
		});
	}
}
