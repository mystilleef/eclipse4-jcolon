package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;

import org.eclipse.ui.IEditorPart;

final class FileSyncer implements Runnable {

	private final Runnable syncFileRunnable = new SyncFileRunnable();
	@Getter private final IEditorPart editor = EditorContext.getEditor();

	public FileSyncer() {}

	@Override
	public void run() {
		this.syncFile();
	}

	void syncFile() {
		EditorContext.asyncExec(this.syncFileRunnable);
	}

	private final class SyncFileRunnable implements Runnable {

		public SyncFileRunnable() {}

		@Override
		public void run() {
			EditorContext.syncFile(FileSyncer.this.getEditor());
		}
	}
}
