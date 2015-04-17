package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.main.EventBus;

public final class AnnotationsListener extends AbstractListener
	implements
		IAnnotationModelListener {

	private final IAnnotationModel annotationModel =
		AnnotationsListener.getAnnotationModel();

	public AnnotationsListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void
	modelChanged(final IAnnotationModel arg0) {
		this.scheduleErrorChecking();
	}

	@Override
	public void
	add() {
		if (this.annotationModel == null) return;
		this.annotationModel.addAnnotationModelListener(this);
	}

	@Override
	public void
	remove() {
		if (this.annotationModel == null) return;
		this.annotationModel.removeAnnotationModelListener(this);
	}

	private static IAnnotationModel
	getAnnotationModel() {
		try {
			return EditorContext.getAnnotationModel();
		}
		catch (final Exception e) {
			return null;
		}
	}
}
