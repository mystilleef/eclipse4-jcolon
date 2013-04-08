package com.laboki.eclipse.plugin.jcolon.inserter;

import java.util.Map;

import lombok.ToString;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.collect.Maps;
import com.laboki.eclipse.plugin.jcolon.Instance;

@ToString
public final class Factory implements Instance {

	private final Map<IEditorPart, Instance> editorMap = Maps.newHashMap();
	private static final IPartService PART_SERVICE = EditorContext.getPartService();
	private final PartListener partListener = new PartListener();

	public Factory() {
		EditorContext.instance();
	}

	private final class PartListener implements IPartListener {

		public PartListener() {}

		@Override
		public void partActivated(final IWorkbenchPart part) {
			Factory.this.enableAutomaticInserterFor(part);
		}

		@Override
		public void partClosed(final IWorkbenchPart part) {}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {}

		@Override
		public void partOpened(final IWorkbenchPart part) {}
	}

	private void enableAutomaticInserterFor(final IWorkbenchPart part) {
		if (Factory.isInvalidPart(part)) return;
		this.startInserterService(part);
	}

	private static boolean isInvalidPart(final IWorkbenchPart part) {
		return !Factory.isValidPart(part);
	}

	private static boolean isValidPart(final IWorkbenchPart part) {
		if (Factory.isNotEditorPart(part)) return false;
		if (!EditorContext.isAJavaEditor((IEditorPart) part)) return false;
		return true;
	}

	private static boolean isNotEditorPart(final IWorkbenchPart part) {
		return !Factory.isEditorPart(part);
	}

	private static boolean isEditorPart(final IWorkbenchPart part) {
		return part instanceof IEditorPart;
	}

	private void startInserterService(final IWorkbenchPart part) {
		this.stopAllInserterServices();
		this.editorMap.put((IEditorPart) part, new SemiColonInserterServices().begin());
	}

	private void stopAllInserterServices() {
		for (final IEditorPart part : this.editorMap.keySet())
			this.stopInserterService(part);
	}

	private void stopInserterService(final IWorkbenchPart part) {
		this.editorMap.get(part).end();
		this.editorMap.remove(part);
	}

	@Override
	public Instance begin() {
		this.enableAutomaticInserterFor(Factory.PART_SERVICE.getActivePart());
		Factory.PART_SERVICE.addPartListener(this.partListener);
		return this;
	}

	@Override
	public Instance end() {
		Factory.PART_SERVICE.removePartListener(this.partListener);
		this.stopAllInserterServices();
		return this;
	}
}
