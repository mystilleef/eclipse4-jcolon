package com.laboki.eclipse.plugin.jcolon.listeners.abstraction;

import com.laboki.eclipse.plugin.jcolon.instance.EventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.task.BaseTask;
import com.laboki.eclipse.plugin.jcolon.task.Task;
import com.laboki.eclipse.plugin.jcolon.task.TaskMutexRule;

public abstract class BaseListener extends EventBusInstance
	implements
		IListener {

	private static final int ONE_SECOND = 1000;
	public static final String FAMILY = "ABSTRACT_LISTENER_FAMILY";
	private static final TaskMutexRule RULE = new TaskMutexRule();

	public BaseListener() {
		super();
	}

	@Override
	public final Instance
	start() {
		this.add();
		return super.start();
	}

	@Override
	public void
	add() {}

	@Override
	public final Instance
	stop() {
		this.remove();
		return super.stop();
	}

	@Override
	public void
	remove() {}

	protected final static void
	scheduleErrorChecking() {
		EditorContext.cancelAllJobs();
		BaseListener.scheduleTask();
	}

	protected static void
	scheduleTask() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return BaseTask.noTaskFamilyExists(BaseListener.FAMILY);
			}

			@Override
			public void
			execute() {
				EditorContext.scheduleErrorChecking();
			}
		}.setRule(BaseListener.RULE)
			.setFamily(BaseListener.FAMILY)
			.setDelay(BaseListener.ONE_SECOND)
			.start();
	}
}
