package com.laboki.eclipse.plugin.jcolon.listeners;

import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.jcolon.inserter.EventBus;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public abstract class AbstractListener implements IListener, Instance {

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
			// AbstractListener.log.log(AbstractListener.LOG_LEVEL, "failed to add listener");
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
			// AbstractListener.log.log(AbstractListener.LOG_LEVEL, "failed to remove listener");
		}
	}

	@Override
	public void remove() {}

	protected void scheduleErrorChecking() {
		new Task(EditorContext.ERROR_CHECKING_TASK, 1000) {

			@Override
			public void execute() {
				EditorContext.scheduleErrorChecking(AbstractListener.this.eventBus);
			}
		}.begin();
	}
}
