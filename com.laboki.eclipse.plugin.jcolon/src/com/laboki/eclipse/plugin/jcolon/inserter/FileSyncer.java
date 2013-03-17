package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;

import org.eclipse.ui.IEditorPart;

final class FileSyncer implements Runnable {

	private final Runnable syncRunnable = new SyncFileRunnable();
	@Getter private final IEditorPart editor = EditorContext.getEditor();

	public FileSyncer() {}

	@Override
	public void run() {
		this.sync();
	}

	private void sync() {
		EditorContext.asyncExec(this.syncRunnable);
	}

	private final class SyncFileRunnable implements Runnable {

		public SyncFileRunnable() {}

		@Override
		public void run() {
			EditorContext.syncFile(FileSyncer.this.getEditor());
		}
	}
}
