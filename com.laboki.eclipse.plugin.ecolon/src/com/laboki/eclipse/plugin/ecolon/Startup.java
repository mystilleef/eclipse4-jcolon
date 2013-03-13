package com.laboki.eclipse.plugin.ecolon;

import lombok.ToString;

import org.eclipse.ui.IPartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import com.laboki.eclipse.plugin.ecolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.ecolon.inserter.Factory;

@ToString
public final class Startup implements IStartup, Runnable {

	public Startup() {}

	@Override
	public void earlyStartup() {
		EditorContext.asyncExec(this);
	}

	@Override
	public void run() {
		Startup.start();
	}

	private static void start() {
		EditorContext.asyncExec(new Factory((IPartService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IPartService.class)));
	}
}
