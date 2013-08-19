package com.laboki.eclipse.plugin.jcolon.instance;

import com.laboki.eclipse.plugin.jcolon.main.EventBus;

public abstract class AbstractEventBusInstance implements Instance {

	private final EventBus eventBus;

	protected AbstractEventBusInstance(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		return this;
	}

	public final EventBus getEventBus() {
		return this.eventBus;
	}
}
