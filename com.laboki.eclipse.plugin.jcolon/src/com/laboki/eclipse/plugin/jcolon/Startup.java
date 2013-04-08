package com.laboki.eclipse.plugin.jcolon;

import lombok.ToString;

import org.eclipse.ui.IStartup;

import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;

@ToString
public final class Startup implements IStartup, Runnable {

	public Startup() {}

	@Override
	public void earlyStartup() {
		EditorContext.asyncExec(this);
	}

	@Override
	public void run() {
		Startup.start();
	}

	private static void start() {
		EditorContext.asyncExec(Inserter.INSTANCE);
	}
}
