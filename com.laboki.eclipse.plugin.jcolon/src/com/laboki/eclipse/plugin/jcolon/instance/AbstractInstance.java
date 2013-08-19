package com.laboki.eclipse.plugin.jcolon.instance;

public abstract class AbstractInstance implements Instance {

	protected AbstractInstance() {}

	@Override
	public final Instance begin() {
		return this;
	}

	@Override
	public final Instance end() {
		return this;
	}
}
