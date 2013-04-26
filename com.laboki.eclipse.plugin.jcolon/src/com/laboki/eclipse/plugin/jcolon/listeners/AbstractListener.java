package com.laboki.eclipse.plugin.jcolon.listeners;

import java.util.logging.Level;

import lombok.extern.java.Log;

import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.jcolon.inserter.EventBus;

@Log
public abstract class AbstractListener implements IListener, Instance {

	private static final Level LOG_LEVEL = Level.FINEST;
	private final EventBus eventBus;

	public AbstractListener(final EventBus eventbus) {
		this.eventBus = eventbus;
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		this.tryToAdd();
		return this;
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {
			AbstractListener.log.log(AbstractListener.LOG_LEVEL, "failed to add listener");
		}
	}

	@Override
	public void add() {}

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
			AbstractListener.log.log(AbstractListener.LOG_LEVEL, "failed to remove listener");
		}
	}

	@Override
	public void remove() {}

	protected void scheduleErrorChecking() {
		EditorContext.asyncExec(new Task() {

			@Override
			public void execute() {
				EditorContext.scheduleErrorChecking(AbstractListener.this.eventBus);
			}
		});
	}
}
