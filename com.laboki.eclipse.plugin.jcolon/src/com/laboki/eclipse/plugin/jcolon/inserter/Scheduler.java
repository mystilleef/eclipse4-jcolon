package com.laboki.eclipse.plugin.jcolon.inserter;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.ScheduleCheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public final class Scheduler implements Instance {

	private final EventBus eventBus;

	public Scheduler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleErrorCheck(@SuppressWarnings("unused") final ScheduleCheckErrorEvent event) {
		new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				EditorContext.cancelErrorCheckingJobs();
				Scheduler.this.eventBus.post(new CheckErrorEvent());
			}
		}.begin();
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		EditorContext.cancelErrorCheckingJobs();
		return this;
	}
}
