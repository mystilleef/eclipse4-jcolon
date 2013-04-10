package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.DelayedTask;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class FileSyncer implements Instance {

	private IEditorPart editor = EditorContext.getEditor();

	public FileSyncer() {}

	@Subscribe
	@AllowConcurrentEvents
	public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		EditorContext.asyncExec(new DelayedTask("", EditorContext.DELAY_TIME_IN_MILLISECONDS) {

			@Override
			public void execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
			}
		});
	}

	@Override
	public Instance begin() {
		return this;
	}

	@Override
	public Instance end() {
		this.editor = null;
		return this;
	}
}
