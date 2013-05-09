package com.laboki.eclipse.plugin.jcolon;

import com.laboki.eclipse.plugin.jcolon.inserter.Factory;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public enum Plugin implements Instance {
	INSTANCE;

	@Override
	public Instance begin() {
		new Task() {

			@Override
			public void asyncExecute() {
				Factory.INSTANCE.begin();
			}
		}.begin();
		return this;
	}

	@Override
	public Instance end() {
		new Task() {

			@Override
			public void asyncExecute() {
				Factory.INSTANCE.end();
			}
		}.begin();
		return this;
	}
}
