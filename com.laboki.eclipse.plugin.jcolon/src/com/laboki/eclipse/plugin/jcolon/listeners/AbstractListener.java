package com.laboki.eclipse.plugin.jcolon.listeners;

import java.util.logging.Level;

import lombok.extern.java.Log;

import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.jcolon.inserter.EventBus;

@Log
public abstract class AbstractListener implements IListener, Instance {

	private static final Level FINEST = Level.FINEST;
	private final EventBus eventBus;

	public AbstractListener(final EventBus eventbus) {
		this.eventBus = eventbus;
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {
			AbstractListener.log.log(AbstractListener.FINEST, "failed to add listener");
		}
	}

	@Override
	public void add() {}

	@Override
	public void remove() {}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		this.tryToAdd();
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.tryToRemove();
		return this;
	}

	private void tryToRemove() {
		try {
			this.remove();
		} catch (final Exception e) {
			AbstractListener.log.log(AbstractListener.FINEST, "failed to remove listener");
		}
	}

	protected void scheduleErrorChecking() {
		EditorContext.scheduleErrorChecking(this.eventBus);
	}
}
