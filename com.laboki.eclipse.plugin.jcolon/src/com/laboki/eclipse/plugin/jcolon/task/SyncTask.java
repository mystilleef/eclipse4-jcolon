package com.laboki.eclipse.plugin.jcolon.task;

import com.laboki.eclipse.plugin.jcolon.main.EditorContext;

public abstract class SyncTask extends BaseTask implements ExecuteTask {

	public SyncTask() {}

	@Override
	protected TaskJob
	newTaskJob() {
		return new TaskJob() {

			@Override
			protected void
			runTask() {
				EditorContext.syncExec(() -> SyncTask.this.execute());
			}
		};
	}

	@Override
	public abstract void
	execute();
}
