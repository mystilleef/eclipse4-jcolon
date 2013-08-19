package com.laboki.eclipse.plugin.jcolon.instance;

import com.laboki.eclipse.plugin.jcolon.main.EventBus;

public class EventBusInstance extends InstanceObject {

	private final EventBus eventBus;

	protected EventBusInstance(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public final EventBus getEventBus() {
		return this.eventBus;
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
}
