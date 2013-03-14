package com.laboki.eclipse.plugin.jcolon.inserter.listeners;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;

@ToString
public final class InserterAnnotationModelListener implements IAnnotationModelListener {

	private boolean isListening;
	@Getter private final IInserterAnnotationModelListenerHandler handler;
	private final ModelChangedRunnable modelChangedRunnable = new ModelChangedRunnable();
	private final IAnnotationModel annotationModel = EditorContext.getView().getAnnotationModel();

	public InserterAnnotationModelListener(final IInserterAnnotationModelListenerHandler handler) {
		this.handler = handler;
	}

	public void start() {
		if (this.isListening) return;
		this.annotationModel.addAnnotationModelListener(this);
		this.isListening = true;
	}

	public void stop() {
		if (!this.isListening) return;
		this.annotationModel.removeAnnotationModelListener(this);
		this.isListening = false;
	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		EditorContext.asyncExec(this.modelChangedRunnable);
	}

	private final class ModelChangedRunnable implements Runnable {

		public ModelChangedRunnable() {}

		@Override
		public void run() {
			InserterAnnotationModelListener.this.getHandler().modelChanged();
		}
	}
}
