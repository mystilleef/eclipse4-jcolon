package com.laboki.eclipse.plugin.jcolon.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.ScheduleCheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public final class Scheduler extends AbstractEventBusInstance {

	public Scheduler(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleErrorCheck(@SuppressWarnings("unused") final ScheduleCheckErrorEvent event) {
		new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public boolean shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public boolean shouldRun() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void execute() {
				EditorContext.cancelErrorCheckingJobs();
				Scheduler.this.eventBus.post(new CheckErrorEvent());
			}
		}.begin();
	}

	@Override
	public Instance end() {
		EditorContext.cancelErrorCheckingJobs();
		return super.end();
	}
}
