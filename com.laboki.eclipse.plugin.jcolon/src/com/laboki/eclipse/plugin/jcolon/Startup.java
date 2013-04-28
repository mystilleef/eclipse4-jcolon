package com.laboki.eclipse.plugin.jcolon;

import lombok.ToString;

import org.eclipse.ui.IStartup;

@ToString
public final class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		Plugin.INSTANCE.begin();
	}
}
