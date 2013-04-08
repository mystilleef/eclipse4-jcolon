// $codepro.audit.disable methodChainLength
package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Iterator;
import java.util.logging.Level;

import lombok.ToString;
import lombok.extern.java.Log;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

@Log
@ToString
public enum EditorContext {
	INSTANCE;

	public static final Display DISPLAY = EditorContext.getDisplay();
	private static final String JDT_ANNOTATION_ERROR = "org.eclipse.jdt.ui.error";
	private static final FlushEventsRunnable FLUSH_EVENTS_RUNNABLE = new EditorContext.FlushEventsRunnable();

	public static Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getModalDialogShellProvider().getShell();
	}

	public static void asyncExec(final Runnable runnable) {
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void flushEvents() {
		EditorContext.asyncExec(EditorContext.FLUSH_EVENTS_RUNNABLE);
	}

	private static final class FlushEventsRunnable implements Runnable {

		public FlushEventsRunnable() {}

		@Override
		public void run() {
			while (EditorContext.DISPLAY.readAndDispatch())
				EditorContext.DISPLAY.update();
			EditorContext.DISPLAY.update();
		}
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

	public static boolean hasJDTErrors(final IEditorPart editor) {
		return EditorContext.hasJDTAnnotationError(editor);
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
		try {
			return ((FileEditorInput) editor.getEditorInput()).getFile();
		} catch (final Exception e) {
			return null;
		}
	}

	private static boolean hasJDTAnnotationError(final IEditorPart editor) {
		try {
			return EditorContext._hasJDTAnnotationError(editor);
		} catch (final Exception e) {}
		return false;
	}

	private static boolean _hasJDTAnnotationError(final IEditorPart editor) {
		final Iterator<Annotation> iterator = EditorContext.getView(editor).getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.isJdtError(iterator)) return true;
		return false;
	}

	private static boolean isJdtError(final Iterator<Annotation> iterator) {
		return iterator.next().getType().equals(EditorContext.JDT_ANNOTATION_ERROR);
	}

	public static boolean isNotAJavaEditor(final IEditorPart part) {
		return !EditorContext.isAJavaEditor(part);
	}

	public static boolean isAJavaEditor(final IEditorPart part) {
		final IFile file = EditorContext.getFile(part);
		if (file == null) return false;
		return JavaCore.isJavaLikeFileName(file.getName());
	}

	public static IDocument getDocument(final IEditorPart editor) {
		final ITextEditor textEditor = (ITextEditor) editor;
		return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	}

	public static IPartService getPartService() {
		return (IPartService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IPartService.class);
	}

	public static boolean isNotNull(final Object object) {
		return !EditorContext.isNull(object);
	}

	public static boolean isNull(final Object object) {
		return object == null;
	}
}
