package com.laboki.eclipse.plugin.jcolon.inserter;

import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.AnnotationModelChangedEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterAnnotationModelListenerHandler;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterListener;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.InserterAnnotationModelListener;

final class AnnotationsMonitor implements IInserterAnnotationModelListenerHandler {

	private final IInserterListener listener = new InserterAnnotationModelListener(this);
	private final EventBus eventBus;

	public AnnotationsMonitor(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.listener.start();
	}

	@Override
	public void annotationModelChanged() {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				AnnotationsMonitor.this.postEvent();
			}
		});
	}

	private void postEvent() {
		this.eventBus.post(new SyncFilesEvent());
		this.eventBus.post(new AnnotationModelChangedEvent());
	}
}
