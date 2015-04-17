package com.laboki.eclipse.plugin.jcolon.instance;

public class InstanceObject implements Instance {

	protected InstanceObject() {}

	@Override
	public Instance
	start() {
		return this;
	}

	@Override
	public Instance
	stop() {
		return this;
	}
}
