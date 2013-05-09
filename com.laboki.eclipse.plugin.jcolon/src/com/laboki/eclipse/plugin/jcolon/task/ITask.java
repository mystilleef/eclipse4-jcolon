package com.laboki.eclipse.plugin.jcolon.task;

interface ITask {

	void execute();

	void asyncExec();

	void postExecute();
}
