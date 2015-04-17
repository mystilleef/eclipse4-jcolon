package com.laboki.eclipse.plugin.jcolon.instance;

import com.laboki.eclipse.plugin.jcolon.main.EventBus;

public class EventBusInstance extends InstanceObject {

	@Override
	public Instance
	start() {
		EventBus.register(this);
		return this;
	}

	@Override
	public Instance
	stop() {
		EventBus.unregister(this);
		return this;
	}
}
