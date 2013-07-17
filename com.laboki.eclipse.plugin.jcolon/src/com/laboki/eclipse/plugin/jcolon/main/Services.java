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
	private final EventBus eventBus = new EventBus();

	@Override
	public Instance begin() {
		this.startServices();
		return this;
	}

	private void startServices() {
		this.startService(new Inserter(this.eventBus));
		this.startService(new ErrorLocator(this.eventBus));
		this.startService(new FileSyncer(this.eventBus));
		this.startService(new ErrorChecker(this.eventBus));
		this.startService(new Scheduler(this.eventBus));
		this.startService(new KeyEventListener(this.eventBus));
		this.startService(new TextInsertionListener(this.eventBus));
		this.startService(new AnnotationsListener(this.eventBus));
		this.startService(new CompletionListener(this.eventBus));
	}

	private void startService(final Instance instance) {
		instance.begin();
		this.instances.add(instance);
	}

	@Override
	public Instance end() {
		this.stopServices();
		return this;
	}

	private void stopServices() {
		for (final Instance instance : ImmutableList.copyOf(this.instances))
			this.stopService(instance);
	}

	private void stopService(final Instance instance) {
		instance.end();
		this.instances.remove(instance);
	}
}
