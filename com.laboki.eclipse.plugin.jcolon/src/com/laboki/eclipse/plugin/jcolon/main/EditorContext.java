package com.laboki.eclipse.plugin.jcolon.main;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.jcolon.events.ScheduleCheckErrorEvent;
import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.BaseListener;
import com.laboki.eclipse.plugin.jcolon.task.BaseTask;
import com.laboki.eclipse.plugin.jcolon.task.Task;
import com.laboki.eclipse.plugin.jcolon.task.TaskMutexRule;

public enum EditorContext {
	INSTANCE;

	public static final TaskMutexRule ERROR_CHECKER_RULE = new TaskMutexRule();
	public static final String ERROR_CHECKER_FAMILY =
		"jcolon semicolon error checking task";
	public static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();
	public static final Display DISPLAY = EditorContext.WORKBENCH.getDisplay();
	public static final IJobManager JOB_MANAGER = Job.getJobManager();
	public static final int SHORT_DELAY = 60;
	public static final int LONG_DELAY_TIME = 1000;
	private static final String LINK_SLAVE =
		"org.eclipse.ui.internal.workbench.texteditor.link.slave";
	private static final String LINK_MASTER =
		"org.eclipse.ui.internal.workbench.texteditor.link.master";
	private static final String LINK_TARGET =
		"org.eclipse.ui.internal.workbench.texteditor.link.target";
	private static final String LINK_EXIT =
		"org.eclipse.ui.internal.workbench.texteditor.link.exit";
	private static final String JDT_ANNOTATION_ERROR =
		"org.eclipse.jdt.ui.error";
	private static final List<String> LINK_ANNOTATIONS =
		Lists.newArrayList(EditorContext.LINK_EXIT, EditorContext.LINK_TARGET, EditorContext.LINK_MASTER, EditorContext.LINK_SLAVE);
	private static final DefaultMarkerAnnotationAccess ANNOTATION_ACCESS =
		new DefaultMarkerAnnotationAccess();

	public static Display
	getDisplay() {
		return EditorContext.DISPLAY;
	}

	public static Optional<Shell>
	getShell() {
		return Optional.fromNullable(EditorContext.WORKBENCH.getModalDialogShellProvider()
			.getShell());
	}

	public static Optional<IEditorPart>
	getEditor() {
		final Optional<IWorkbenchWindow> window =
			EditorContext.getActiveWorkbenchWindow();
		if (!window.isPresent()) return Optional.absent();
		final Optional<IWorkbenchPage> page = EditorContext.getActivePage(window);
		if (!page.isPresent()) return Optional.absent();
		return Optional.fromNullable(page.get().getActiveEditor());
	}

	private static Optional<IWorkbenchWindow>
	getActiveWorkbenchWindow() {
		return Optional.fromNullable(EditorContext.WORKBENCH.getActiveWorkbenchWindow());
	}

	private static Optional<IWorkbenchPage>
	getActivePage(final Optional<IWorkbenchWindow> window) {
		if (!window.isPresent()) return Optional.absent();
		return Optional.fromNullable(window.get().getActivePage());
	}

	public static Optional<IPartService>
	getPartService() {
		final Optional<IWorkbenchWindow> window =
			EditorContext.getActiveWorkbenchWindow();
		if (!window.isPresent()) return Optional.absent();
		return Optional.fromNullable((IPartService) window.get()
			.getService(IPartService.class));
	}

	public static Optional<Control>
	getControl(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((Control) editor.get()
			.getAdapter(Control.class));
	}

	public static Optional<StyledText>
	getBuffer(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((StyledText) editor.get()
			.getAdapter(Control.class));
	}

	public static Optional<SourceViewer>
	getView(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((SourceViewer) editor.get()
			.getAdapter(ITextOperationTarget.class));
	}

	public static Optional<IAnnotationModel>
	getAnnotationModel(final Optional<IEditorPart> editor) {
		final Optional<SourceViewer> view = EditorContext.getView(editor);
		if (!view.isPresent()) return Optional.absent();
		return Optional.fromNullable(view.get().getAnnotationModel());
	}

	public static boolean
	hasJDTErrors(final Optional<IEditorPart> editor) {
		return EditorContext.hasJDTAnnotationError(editor);
	}

	private static boolean
	hasJDTAnnotationError(final Optional<IEditorPart> editor) {
		final Optional<IAnnotationModel> model =
			EditorContext.getAnnotationModel(editor);
		if (!model.isPresent()) return false;
		final Iterator<Annotation> iterator = model.get().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.isJdtError(iterator)) return true;
		return false;
	}

	private static boolean
	isJdtError(final Iterator<Annotation> iterator) {
		final Annotation annotation = iterator.next();
		if (annotation.isMarkedDeleted()) return false;
		return EditorContext.ANNOTATION_ACCESS.isSubtype(annotation.getType(), EditorContext.JDT_ANNOTATION_ERROR);
	}

	public static boolean
	isNotAJavaEditor(final Optional<IEditorPart> part) {
		return !EditorContext.isAJavaEditor(part);
	}

	public static boolean
	isAJavaEditor(final Optional<IEditorPart> part) {
		if (!part.isPresent()) return false;
		final Optional<IFile> file = EditorContext.getFile(part);
		if (!file.isPresent()) return false;
		return JavaCore.isJavaLikeFileName(file.get().getName());
	}

	public static Optional<IFile>
	getFile(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final IEditorInput input = editor.get().getEditorInput();
		if (!(input instanceof IFileEditorInput)) return Optional.absent();
		return Optional.fromNullable(((FileEditorInput) input).getFile());
	}

	public static Optional<IDocument>
	getDocument(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final Optional<IDocumentProvider> provider =
			EditorContext.getDocumentProvider(editor);
		if (!provider.isPresent()) return Optional.absent();
		return Optional.fromNullable(provider.get()
			.getDocument(((ITextEditor) editor.get()).getEditorInput()));
	}

	private static Optional<IDocumentProvider>
	getDocumentProvider(final Optional<IEditorPart> editor) {
		return Optional.fromNullable(((ITextEditor) editor.get()).getDocumentProvider());
	}

	public static void
	cancelAllJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.ERROR_CHECKER_FAMILY, BaseListener.FAMILY, Scheduler.FAMILY);
	}

	public static void
	cancelEventTasks() {
		EditorContext.cancelJobsBelongingTo(EventBus.FAMILY);
	}

	public static void
	cancelErrorCheckingJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.ERROR_CHECKER_FAMILY);
	}

	public static void
	cancelJobsBelongingTo(final String... jobNames) {
		for (final String jobName : jobNames)
			EditorContext.JOB_MANAGER.cancel(jobName);
	}

	public static boolean
	isInEditMode(final Optional<IEditorPart> editor) {
		return EditorContext.hasSelection(editor)
			|| EditorContext.isInLinkMode(editor);
	}

	public static boolean
	hasSelection(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		final Optional<StyledText> buffer = EditorContext.getBuffer(editor);
		if (!buffer.isPresent()) return false;
		return (buffer.get().getSelectionCount() > 0)
			|| EditorContext.hasBlockSelection(editor);
	}

	public static boolean
	hasBlockSelection(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		final Optional<StyledText> buffer = EditorContext.getBuffer(editor);
		if (!buffer.isPresent()) return false;
		return buffer.get().getBlockSelection();
	}

	public static boolean
	isInLinkMode(final Optional<IEditorPart> editor) {
		return EditorContext.hasLinkAnnotations(editor);
	}

	private static boolean
	hasLinkAnnotations(final Optional<IEditorPart> editor) {
		final Optional<IAnnotationModel> model =
			EditorContext.getAnnotationModel(editor);
		if (!model.isPresent()) return false;
		final Iterator<Annotation> iterator = model.get().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.isLinkModeAnnotation(iterator)) return true;
		return false;
	}

	private static boolean
	isLinkModeAnnotation(final Iterator<Annotation> iterator) {
		if (EditorContext.LINK_ANNOTATIONS.contains(iterator.next().getType())) return true;
		return false;
	}

	public static void
	scheduleErrorChecking() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return BaseTask.noTaskFamilyExists(Scheduler.FAMILY);
			}

			@Override
			public void
			execute() {
				EventBus.post(new ScheduleCheckErrorEvent());
			}
		}.setRule(Scheduler.RULE)
			.setFamily(Scheduler.FAMILY)
			.setDelay(EditorContext.SHORT_DELAY)
			.start();
	}

	public static boolean
	taskDoesNotExist(final String name) {
		return EditorContext.JOB_MANAGER.find(name).length == 0;
	}

	public static void
	asyncExec(final Runnable runnable) {
		if (EditorContext.displayDoesNotExist()) return;
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void
	syncExec(final Runnable runnable) {
		if (EditorContext.displayDoesNotExist()) return;
		EditorContext.DISPLAY.syncExec(runnable);
	}

	private static boolean
	displayDoesNotExist() {
		return !EditorContext.displayExists();
	}

	private static boolean
	displayExists() {
		return !EditorContext.displayIsDisposed();
	}

	private static boolean
	displayIsDisposed() {
		if (EditorContext.DISPLAY == null) return true;
		return EditorContext.DISPLAY.isDisposed();
	}
}
