package com.laboki.eclipse.plugin.jcolon.inserter;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.ScheduleCheckErrorEvent;

public final class Scheduler implements Instance {

	private final EventBus eventBus;

	public Scheduler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleErrorCheck(@SuppressWarnings("unused") final ScheduleCheckErrorEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				EditorContext.cancelErrorCheckingJobs();
			}

			@Override
			public void postExecute() {
				Scheduler.this.eventBus.post(new CheckErrorEvent());
			}
		});
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
