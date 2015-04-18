package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.BaseListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;

public final class AnnotationsListener extends BaseListener
	implements
		IAnnotationModelListener {

	private final IAnnotationModel annotationModel =
		AnnotationsListener.getAnnotationModel();

	public AnnotationsListener() {
		super();
	}

	@Override
	public void
	modelChanged(final IAnnotationModel arg0) {
		BaseListener.scheduleErrorChecking();
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
