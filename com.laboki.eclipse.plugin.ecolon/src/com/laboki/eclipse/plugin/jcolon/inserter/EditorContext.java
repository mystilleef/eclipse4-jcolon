// $codepro.audit.disable methodChainLength
package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Iterator;
import java.util.logging.Level;

import lombok.Synchronized;
import lombok.extern.java.Log;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

@Log
public final class EditorContext {

	private static EditorContext instance;
	private static final String ANNOTATION_SEVERITY_ERROR = "org.eclipse.jdt.ui.error";
	private static final Display DISPLAY = EditorContext.getDisplay();

	private EditorContext() {}

	@Synchronized
	public static EditorContext instance() {
		if (EditorContext.instance == null) EditorContext.instance = new EditorContext();
		return EditorContext.instance;
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) display = Display.getDefault();
		return display;
	}

	public static void asyncExec(final Runnable runnable) {
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void flushEvents() {
		while (EditorContext.DISPLAY.readAndDispatch())
			EditorContext.DISPLAY.update();
		EditorContext.DISPLAY.update();
	}

	public static IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static StyledText getBuffer(final IEditorPart editor) {
		return (StyledText) editor.getAdapter(Control.class);
	}

	public static SourceViewer getView() {
		return (SourceViewer) EditorContext.getEditor().getAdapter(ITextOperationTarget.class);
	}

	public static SourceViewer getView(final IEditorPart editor) {
		return (SourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}

	public static boolean hasErrors(final IEditorPart editor) {
		EditorContext.syncFile(editor);
		return EditorContext.getAnnotationSeverity(editor);
	}

	static void syncFile(final IEditorPart editor) {
		EditorContext.flushEvents();
		EditorContext.tryToSyncFile(editor);
	}

	private static void tryToSyncFile(final IEditorPart editor) {
		try {
			EditorContext.getFile(editor).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final CoreException e) {
			EditorContext.log.log(Level.FINEST, "Failed to sync IFile resource", e);
		} finally {
			EditorContext.flushEvents();
		}
	}

	public static IFile getFile(final IEditorPart editor) {
		return ((FileEditorInput) editor.getEditorInput()).getFile();
	}

	private static boolean getAnnotationSeverity(final IEditorPart editor) {
		final Iterator<Annotation> iterator = EditorContext.getView(editor).getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.hasProblems(iterator)) return true;
		return false;
	}

	private static boolean hasProblems(final Iterator<Annotation> iterator) {
		return iterator.next().getType().equals(EditorContext.ANNOTATION_SEVERITY_ERROR);
	}

	public static boolean isAJavaEditor(final IWorkbenchPart part) {
		return JavaCore.isJavaLikeFileName(EditorContext.getFile((IEditorPart) part).getName());
	}
}
