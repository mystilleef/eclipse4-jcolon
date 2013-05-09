package com.laboki.eclipse.plugin.jcolon.task;

interface ITask {

	void execute();

	void asyncExecute();

	void postExecute();
}
