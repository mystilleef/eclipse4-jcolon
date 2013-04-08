package com.laboki.eclipse.plugin.jcolon;

import com.laboki.eclipse.plugin.jcolon.inserter.Factory;

public enum Inserter implements Runnable {
	INSTANCE;

	private static final Instance FACTORY = new Factory();

	@Override
	public void run() {
		Inserter.FACTORY.begin();
	}
}
