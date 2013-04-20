package com.laboki.eclipse.plugin.jcolon.inserter;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.jcolon.DelayedTask;
import com.laboki.eclipse.plugin.jcolon.Instance;
import com.laboki.eclipse.plugin.jcolon.inserter.events.LocateSemiColonErrorEvent;
import com.laboki.eclipse.plugin.jcolon.inserter.events.SyncFilesEvent;

final class ErrorChecker implements Instance, VerifyListener, IAnnotationModelListener, CaretListener, KeyListener {

	private EventBus eventBus;
	private IEditorPart editor = EditorContext.getEditor();
	private StyledText buffer = EditorContext.getBuffer(this.editor);
	private IAnnotationModel annotationModel = EditorContext.getView(this.editor).getAnnotationModel();

	public ErrorChecker(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);
	}

	@Override
	public Instance begin() {
		this.checkError();
		this.buffer.addVerifyListener(this);
		this.buffer.addCaretListener(this);
		this.buffer.addKeyListener(this);
		this.annotationModel.addAnnotationModelListener(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		ErrorChecker.cancelJobs();
		if (EditorContext.isNotNull(this.buffer) && this.bufferisNotDisposed()) this.buffer.removeVerifyListener(this);
		if (EditorContext.isNotNull(this.buffer) && this.bufferisNotDisposed()) this.buffer.removeCaretListener(this);
		if (EditorContext.isNotNull(this.buffer) && this.bufferisNotDisposed()) this.buffer.removeKeyListener(this);
		if (EditorContext.isNotNull(this.annotationModel)) this.annotationModel.removeAnnotationModelListener(this);
		this.nullifyFields();
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
		EditorContext.asyncExec(new DelayedTask(EditorContext.TASK_FAMILY_NAME_2, EditorContext.DELAY_TIME_IN_MILLISECONDS) {

			@Override
			public void execute() {
				ErrorChecker.cancelAllJobs();
				if (EditorContext.isBusy()) ErrorChecker.this.checkErrorLater();
				else ErrorChecker.this.checkErrorNow();
			}
		});
	}

	private void checkErrorLater() {
		EditorContext.asyncExec(new DelayedTask(EditorContext.TASK_FAMILY_NAME_2, EditorContext.DELAY_TIME_IN_MILLISECONDS) {

			@Override
			public void execute() {
				ErrorChecker.cancelAllJobs();
				ErrorChecker.this.checkError();
			}
		});
	}

	private void checkErrorNow() {
		EditorContext.asyncExec(new DelayedTask(EditorContext.TASK_FAMILY_NAME_2, EditorContext.DELAY_TIME_IN_MILLISECONDS) {

			@Override
			public void execute() {
				ErrorChecker.cancelJobs();
				ErrorChecker.this.findSemiColonError();
			}
		});
	}

	private static void cancelJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.TASK_FAMILY_NAME);
	}

	private static void cancelAllJobs() {
		ErrorChecker.cancelJobs();
		EditorContext.cancelJobsBelongingTo(EditorContext.TASK_FAMILY_NAME_2);
	}

	private void findSemiColonError() {
		EditorContext.asyncExec(new DelayedTask(EditorContext.TASK_FAMILY_NAME, EditorContext.DELAY_TIME_IN_MILLISECONDS) {

			@Override
			public void execute() {
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
		this.eventBus.post(new LocateSemiColonErrorEvent());
	}

	private void nullifyFields() {
		this.editor = null;
		this.buffer = null;
		this.eventBus = null;
		this.annotationModel = null;
	}

	@Override
	public void caretMoved(final CaretEvent arg0) {
		this.checkError();
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
