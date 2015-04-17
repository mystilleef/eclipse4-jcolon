package com.laboki.eclipse.plugin.jcolon.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.ScheduleCheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.instance.EventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.task.Task;
import com.laboki.eclipse.plugin.jcolon.task.TaskMutexRule;

public final class Scheduler extends EventBusInstance {

	private static final TaskMutexRule RULE = new TaskMutexRule();

	public Scheduler() {
		super();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void
	scheduleCheckErrorEventHandler(final ScheduleCheckErrorEvent event) {
		EditorContext.cancelErrorCheckingJobs();
		Scheduler.scheduleErrorChecking();
	}

	private static void
	scheduleErrorChecking() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void
			execute() {
				EventBus.post(new CheckErrorEvent());
			}
		}.setRule(Scheduler.RULE)
			.setFamily(EditorContext.ERROR_CHECKING_TASK)
			.setDelay(EditorContext.SHORT_DELAY_TIME)
			.start();
	}

	@Override
	public Instance
	stop() {
		EditorContext.cancelErrorCheckingJobs();
		return super.stop();
	}
}
