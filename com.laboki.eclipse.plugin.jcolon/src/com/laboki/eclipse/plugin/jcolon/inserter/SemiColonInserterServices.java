package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.jcolon.Instance;

public final class SemiColonInserterServices implements Instance {

	private List<Instance> instances = Lists.newArrayList();
	private EventBus eventBus = new EventBus();

	public SemiColonInserterServices() {}

	@Override
	public Instance begin() {
		this.startServices();
		return this;
	}

	private void startServices() {
		this.startService(new FileSyncer());
		this.startService(new SemiColonInserter(this.eventBus));
		this.startService(new ErrorLocator(this.eventBus));
		this.startService(new AnnotationsMonitor(this.eventBus));
	}

	private void startService(final Instance instance) {
		instance.begin();
		this.instances.add(instance);
	}

	@Override
	public Instance end() {
		this.stopServices();
		this.nullifyFields();
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

	private void nullifyFields() {
		this.instances.clear();
		this.instances = null;
		this.eventBus = null;
	}
}
