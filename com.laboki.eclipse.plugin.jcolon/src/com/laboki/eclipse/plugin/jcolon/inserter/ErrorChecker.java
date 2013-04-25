package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.Task;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class ErrorChecker implements Instance, VerifyListener, IAnnotationModelListener, KeyListener {

	private final EventBus eventBus;
	private final IEditorPart editor = EditorContext.getEditor();
	private final StyledText buffer = EditorContext.getBuffer(this.editor);
	private final IAnnotationModel annotationModel = EditorContext.getView(this.editor).getAnnotationModel();

	public ErrorChecker(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);
	}

	@Override
	public Instance begin() {
		this.checkError();
		this.buffer.addVerifyListener(this);
		this.buffer.addKeyListener(this);
		this.annotationModel.addAnnotationModelListener(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		ErrorChecker.cancelJobs();
		if (EditorContext.isNotNull(this.buffer) && this.bufferisNotDisposed()) this.buffer.removeVerifyListener(this);
		if (EditorContext.isNotNull(this.buffer) && this.bufferisNotDisposed()) this.buffer.removeKeyListener(this);
		if (EditorContext.isNotNull(this.annotationModel)) this.annotationModel.removeAnnotationModelListener(this);
		return this;
	}

	private boolean bufferisNotDisposed() {
		return !this.buffer.isDisposed();
	}

	@Override
	public void verifyText(final VerifyEvent event) {
		this.checkError();
	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		this.checkError();
	}

	private void checkError() {
		EditorContext.asyncExec(new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				ErrorChecker.cancelAllJobs();
			}

			@Override
			protected void postExecute() {
				ErrorChecker.this.findSemiColonError();
			}
		});
	}

	private static void cancelJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.ERROR_CHECKING_TASK);
	}

	private static void cancelAllJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.ERROR_CHECKING_TASK);
		ErrorChecker.cancelJobs();
	}

	private void findSemiColonError() {
		EditorContext.asyncExec(new Task(EditorContext.ERROR_CHECKING_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void asyncExec() {
				if (EditorContext.isInEditMode(ErrorChecker.this.editor)) return;
				ErrorChecker.this.postEvent();
			}
		});
	}

	private void postEvent() {
		try {
			this.tryToPostEvent();
		} catch (final Exception e) {}
	}

	private void tryToPostEvent() {
		this.eventBus.post(new SyncFilesEvent());
	}

	@Override
	public void keyPressed(final KeyEvent arg0) {
		this.checkError();
	}

	@Override
	public void keyReleased(final KeyEvent arg0) {
		this.checkError();
	}
}
