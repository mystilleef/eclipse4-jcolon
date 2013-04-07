package com.laboki.eclipse.plugin.jcolon.inserter;

public final class SemiColonInserterServices implements Runnable {

	private final EventBus eventBus = new EventBus();

	@Override
	@SuppressWarnings("unused")
	public void run() {
		new FileSyncer();
		new SemiColonInserter(this.eventBus);
		new ErrorLocator(this.eventBus);
		new AnnotationsMonitor(this.eventBus);
	}
}
