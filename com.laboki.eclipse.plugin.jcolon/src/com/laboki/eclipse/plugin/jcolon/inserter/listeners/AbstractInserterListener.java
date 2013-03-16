package com.laboki.eclipse.plugin.jcolon.inserter.listeners;

abstract class AbstractInserterListener implements IInserterListener {

	private boolean isListening;

	protected AbstractInserterListener() {}

	@Override
	public void start() {
		if (this.isListening) return;
		this.add();
		this.isListening = true;
	}

	@Override
	public void stop() {
		if (!this.isListening) return;
		this.remove();
		this.isListening = false;
	}

	protected void add() {}

	protected void remove() {}
}
