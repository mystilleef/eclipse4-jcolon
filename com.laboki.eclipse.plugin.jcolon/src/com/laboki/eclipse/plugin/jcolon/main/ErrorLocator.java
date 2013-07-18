package com.laboki.eclipse.plugin.jcolon.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.jcolon.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.events.SemiColonErrorLocationEvent;
import com.laboki.eclipse.plugin.jcolon.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.jcolon.task.Task;

final class ErrorLocator extends AbstractEventBusInstance {

	private final Problem problem = new Problem();
	private boolean completionAssistantIsActive;

	public ErrorLocator(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	public void save(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
	}

	@Subscribe
	public void save(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void locateSemiColonErrorEventHandler(@SuppressWarnings("unused") final LocateSemiColonErrorEvent event) {
		this.locateSemiColonError();
	}

	private void locateSemiColonError() {
		new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public boolean shouldSchedule() {
				if (ErrorLocator.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public boolean shouldRun() {
				if (ErrorLocator.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void execute() {
				this.tryTofindErrorLocation();
			}

			private void tryTofindErrorLocation() {
				try {
					this.findErrorLocation();
				} catch (final Exception e) {}
			}

			private void findErrorLocation() throws Exception {
				if (this.hasMissingSemiColonError()) this.postEvent(ErrorLocator.this.problem.location());
			}

			private boolean hasMissingSemiColonError() {
				return ErrorLocator.this.problem.isMissingSemiColonError();
			}

			private void postEvent(final int location) {
				ErrorLocator.this.eventBus.post(new SemiColonErrorLocationEvent(location));
			}
		}.begin();
	}
}
