package com.laboki.eclipse.plugin.jcolon.instance;

public class InstanceObject implements Instance {

	protected InstanceObject() {}

	@Override
	public Instance
	begin() {
		return this;
	}

	@Override
	public Instance
	end() {
		return this;
	}
}
