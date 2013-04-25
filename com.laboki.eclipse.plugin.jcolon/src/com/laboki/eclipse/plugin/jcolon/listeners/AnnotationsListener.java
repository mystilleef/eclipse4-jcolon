package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.jcolon.inserter.EventBus;

public final class AnnotationsListener extends AbstractListener implements IAnnotationModelListener {

	private final IAnnotationModel annotationModel = EditorContext.getAnnotationModel();

	public AnnotationsListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void modelChanged(final IAnnotationModel arg0) {
		this.scheduleErrorChecking();
	}

	@Override
	public void add() {
		this.annotationModel.addAnnotationModelListener(this);
	}

	@Override
	public void remove() {
		this.annotationModel.removeAnnotationModelListener(this);
	}
}
