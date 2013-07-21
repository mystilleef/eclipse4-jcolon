package com.laboki.eclipse.plugin.jcolon.main;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	private final static Logger LOGGER = Logger.getLogger(ErrorLocator.class.getName());
	private boolean completionAssistantIsActive;

	public ErrorLocator(final EventBus eventBus) {
		super(eventBus);
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
				this.findErrorLocation();
			}

			private void findErrorLocation() {
				try {
					this.tryToFindErrorLocation();
				} catch (final Exception e) {
					ErrorLocator.LOGGER.log(Level.WARNING, "failed to find error location", e);
				}
			}

			private void tryToFindErrorLocation() throws Exception {
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

	@Subscribe
	public void assistSessionStartedEventHandler(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
	}

	@Subscribe
	public void assistSessionEndedEventHandler(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}
}
