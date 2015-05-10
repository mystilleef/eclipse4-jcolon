package com.laboki.eclipse.plugin.jcolon.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.listeners.AnnotationsListener;
import com.laboki.eclipse.plugin.jcolon.listeners.CompletionListener;
import com.laboki.eclipse.plugin.jcolon.listeners.KeyEventListener;
import com.laboki.eclipse.plugin.jcolon.listeners.TextInsertionListener;

public final class Services implements Instance {

	private final List<Instance> instances = Lists.newArrayList();

	@Override
	public Instance
	start() {
		this.startServices();
		return this;
	}

	private void
	startServices() {
		this.startService(new Inserter());
		this.startService(new ErrorLocator());
		this.startService(new ErrorChecker());
		this.startService(new Scheduler());
		this.startService(new KeyEventListener());
		this.startService(new TextInsertionListener());
		this.startService(new AnnotationsListener());
		this.startService(new CompletionListener());
	}

	private void
	startService(final Instance instance) {
		instance.start();
		this.instances.add(instance);
	}

	@Override
	public Instance
	stop() {
		Services.cancelTasks();
		this.stopServices();
		this.instances.clear();
		return this;
	}

	private static void
	cancelTasks() {
		EditorContext.cancelAllJobs();
		EditorContext.cancelEventTasks();
		EditorContext.cancelPluginTasks();
	}

	private void
	stopServices() {
		for (final Instance instance : ImmutableList.copyOf(this.instances))
			this.stopService(instance);
	}

	private void
	stopService(final Instance instance) {
		instance.stop();
		this.instances.remove(instance);
	}
}
