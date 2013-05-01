package com.laboki.eclipse.plugin.jcolon.inserter.events;

public final class SemiColonErrorLocationEvent {

	private final int location;

	public SemiColonErrorLocationEvent(final int location) {
		this.location = location;
	}

	public int getLocation() {
		return this.location;
	}
}
