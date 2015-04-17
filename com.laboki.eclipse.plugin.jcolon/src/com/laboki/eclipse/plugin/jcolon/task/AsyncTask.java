package com.laboki.eclipse.plugin.jcolon.task;

import com.laboki.eclipse.plugin.jcolon.main.EditorContext;

public abstract class AsyncTask extends BaseTask implements ExecuteTask {

	public AsyncTask() {}

	@Override
	protected TaskJob
	newTaskJob() {
		return new TaskJob() {

			@Override
			protected void
			runTask() {
				EditorContext.asyncExec(() -> AsyncTask.this.execute());
			}
		};
	}

	@Override
	public abstract void
	execute();
}
