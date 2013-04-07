package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.AnnotationModelChangedEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SemiColonErrorLocationEvent;

final class ErrorLocator {

	private final EventBus eventBus;
	private final Problem problem = new Problem();
	private final IEditorPart editor = EditorContext.getEditor();

	public ErrorLocator(final EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void annotationModelChanged(@SuppressWarnings("unused") final AnnotationModelChangedEvent event) {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				ErrorLocator.this.findErrorLocation();
			}
		});
	}

	private void findErrorLocation() {
		if (this.hasMissingSemiColonError()) this.postEvent(this.problem.location());
	}

	private boolean hasMissingSemiColonError() {
		return this.hasJDTErrors() && this.problem.isMissingSemiColonError();
	}

	private boolean hasJDTErrors() {
		return EditorContext.hasJDTErrors(this.editor);
	}

	private void postEvent(final int location) {
		this.eventBus.post(new SemiColonErrorLocationEvent(location));
	}
}
