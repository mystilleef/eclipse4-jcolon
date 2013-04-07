package com.laboki.eclipse.plugin.jcolon.inserter.events;

import lombok.Getter;

public final class SemiColonErrorLocationEvent {

	@Getter private final int location;

	public SemiColonErrorLocationEvent(final int location) {
		this.location = location;
	}
}
