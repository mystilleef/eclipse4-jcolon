package com.laboki.eclipse.plugin.jcolon;

import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.jcolon.inserter.Factory;

public enum Plugin implements Instance {
	INSTANCE;

	@Override
	public Instance begin() {
		EditorContext.asyncExec(new Task() {

			@Override
			public void asyncExec() {
				Factory.INSTANCE.begin();
			}
		});
		return this;
	}

	@Override
	public Instance end() {
		EditorContext.asyncExec(new Task() {

			@Override
			public void asyncExec() {
				Factory.INSTANCE.end();
			}
		});
		return this;
	}
}
