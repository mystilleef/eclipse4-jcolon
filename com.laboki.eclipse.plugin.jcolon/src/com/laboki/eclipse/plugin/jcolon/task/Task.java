package com.laboki.eclipse.plugin.jcolon.task;

import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.jcolon.Instance;

public class Task extends AbstractTask implements Instance {

	public Task() {
		super("", 0, Job.INTERACTIVE);
	}

	public Task(final String name) {
		super(name, 0, Job.INTERACTIVE);
	}

	public Task(final int delayTime) {
		super("", delayTime, Job.DECORATE);
	}

	public Task(final String name, final int delayTime) {
		super(name, delayTime, Job.DECORATE);
	}

	public Task(final String name, final int delayTime, final int priority) {
		super(name, delayTime, priority);
	}

	@Override
	public Instance begin() {
		this.run();
		return this;
	}

	@Override
	public Instance end() {
		this.cancel();
		return this;
	}
}
