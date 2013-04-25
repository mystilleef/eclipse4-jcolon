package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SemiColonErrorLocationEvent;

final class ErrorLocator implements Instance {

	private final EventBus eventBus;
	private final Problem problem = new Problem();
	private final IEditorPart editor = EditorContext.getEditor();

	public ErrorLocator(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void locateSemiColonError(@SuppressWarnings("unused") final LocateSemiColonErrorEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.TASK_FAMILY_NAME, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void asyncExec() {
				ErrorLocator.this.findErrorLocation();
			}
		});
	}

	private void findErrorLocation() {
		if (this.isMissingSemiColonError()) this.postEvent(this.problem.location());
	}

	private boolean isMissingSemiColonError() {
		return this.hasJDTErrors() && this.hasMissingSemiColonError();
	}

	private boolean hasJDTErrors() {
		return EditorContext.hasJDTErrors(this.editor);
	}

	private boolean hasMissingSemiColonError() {
		return this.problem.isMissingSemiColonError();
	}

	private void postEvent(final int location) {
		this.eventBus.post(new SemiColonErrorLocationEvent(location));
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
