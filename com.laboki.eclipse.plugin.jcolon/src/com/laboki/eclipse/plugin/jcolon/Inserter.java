package com.laboki.eclipse.plugin.jcolon;

import com.laboki.eclipse.plugin.jcolon.inserter.Factory;

public enum Inserter implements Runnable {
	INSTANCE;

	@Override
	public void run() {
		Factory.INSTANCE.begin();
	}
}
