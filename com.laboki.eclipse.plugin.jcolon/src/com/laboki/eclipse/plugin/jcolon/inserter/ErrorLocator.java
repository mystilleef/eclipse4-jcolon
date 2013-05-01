package com.laboki.eclipse.plugin.jcolon.inserter;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SemiColonErrorLocationEvent;

final class ErrorLocator implements Instance {

	private final EventBus eventBus;
	private final Problem problem = new Problem();

	public ErrorLocator(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void locateSemiColonError(@SuppressWarnings("unused") final LocateSemiColonErrorEvent event) {
		new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				this.findErrorLocation();
			}

			private void findErrorLocation() {
				try {
					if (this.hasMissingSemiColonError()) this.postEvent(ErrorLocator.this.problem.location());
				} catch (final Exception e) {}
			}

			private boolean hasMissingSemiColonError() {
				return ErrorLocator.this.problem.isMissingSemiColonError();
			}

			private void postEvent(final int location) {
				ErrorLocator.this.eventBus.post(new SemiColonErrorLocationEvent(location));
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
		this.problem.end();
		return this;
	}
}
