package com.laboki.eclipse.plugin.jcolon;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class Activator extends AbstractUIPlugin {

	private static Activator instance;

	public Activator() {
		Activator.instance = this;
	}

	@Override
	public void
	start(final BundleContext context) throws Exception {
		super.start(context);
		Plugin.INSTANCE.start();
	}

	@Override
	public void
	stop(final BundleContext context) throws Exception {
		Plugin.INSTANCE.stop();
		super.stop(context);
		Activator.instance = null;
	}

	public static Activator
	getInstance() {
		return Activator.instance;
	}
}
