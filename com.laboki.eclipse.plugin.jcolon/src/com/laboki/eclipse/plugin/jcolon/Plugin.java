package com.laboki.eclipse.plugin.jcolon;

import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.main.Factory;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

public enum Plugin implements Instance {
	INSTANCE;

	@Override
	public Instance
	start() {
		new AsyncTask() {

			@Override
			public void
			execute() {
				Factory.INSTANCE.start();
			}
		}.start();
		return this;
	}

	@Override
	public Instance
	stop() {
		Factory.INSTANCE.stop();
		return this;
	}
}
