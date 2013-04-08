package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.jcolon.DelayedTask;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.inserter.events.AnnotationModelChangedEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class AnnotationsMonitor implements IAnnotationModelListener, Instance {

	private EventBus eventBus;
	private IAnnotationModel annotationModel = EditorContext.getView(EditorContext.getEditor()).getAnnotationModel();

	public AnnotationsMonitor(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		EditorContext.asyncExec(new DelayedTask("", 1000) {

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

	public void add() {
		this.annotationModel.addAnnotationModelListener(this);
	}

	public void remove() {
		this.annotationModel.removeAnnotationModelListener(this);
	}

	@Override
	public Instance begin() {
		this.add();
		return this;
	}

	@Override
	public Instance end() {
		this.remove();
		this.eventBus = null;
		this.annotationModel = null;
		return this;
	}
}
