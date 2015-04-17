package com.laboki.eclipse.plugin.jcolon.main;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.collect.Maps;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.task.AsyncTask;

public enum Factory implements Instance {
	INSTANCE;

	private static final Map<IEditorPart, Instance> SERVICES_MAP =
		Maps.newHashMap();
	private static final IPartService PART_SERVICE =
		EditorContext.getPartService();
	private static final PartListener PART_LISTENER = new PartListener();

	private static final class PartListener implements IPartListener {

		public PartListener() {}

		@Override
		public void
		partActivated(final IWorkbenchPart part) {
			PartListener.enableInserterServiceFor(part);
		}

		@Override
		public void
		partClosed(final IWorkbenchPart part) {
			PartListener.disableInserterServiceFor(part);
		}

		@Override
		public void
		partBroughtToTop(final IWorkbenchPart part) {}

		@Override
		public void
		partDeactivated(final IWorkbenchPart part) {
			PartListener.disableInserterServiceFor(part);
		}

		@Override
		public void
		partOpened(final IWorkbenchPart part) {}

		private static void
		enableInserterServiceFor(final IWorkbenchPart part) {
			new AsyncTask() {

				@Override
				public void
				execute() {
					Factory.enableAutomaticInserterFor(part);
				}
			}.start();
		}

		private static void
		disableInserterServiceFor(final IWorkbenchPart part) {
			new AsyncTask() {

				@Override
				public void
				execute() {
					Factory.stopInserterServiceFor(part);
				}
			}.start();
		}
	}

	protected static void
	enableAutomaticInserterFor(final IWorkbenchPart part) {
		if (Factory.isInvalidPart(part)) return;
		Factory.startInserterServiceFor(part);
	}

	private static boolean
	isInvalidPart(final IWorkbenchPart part) {
		return !Factory.isValidPart(part);
	}

	private static boolean
	isValidPart(final IWorkbenchPart part) {
		if (Factory.isNotEditorPart(part)) return false;
		if (EditorContext.isNotAJavaEditor((IEditorPart) part)) return false;
		return true;
	}

	private static boolean
	isNotEditorPart(final IWorkbenchPart part) {
		return !Factory.isEditorPart(part);
	}

	private static boolean
	isEditorPart(final IWorkbenchPart part) {
		return part instanceof IEditorPart;
	}

	private static void
	startInserterServiceFor(final IWorkbenchPart part) {
		Factory.stopAllInserterServices();
		Factory.SERVICES_MAP.put((IEditorPart) part, new Services().start());
	}

	private static void
	stopAllInserterServices() {
		for (final IEditorPart part : Factory.SERVICES_MAP.keySet())
			Factory.stopInserterServiceFor(part);
	}

	protected static void
	stopInserterServiceFor(final IWorkbenchPart part) {
		if (Factory.servicesMapDoesNotContain(part)) return;
		Factory.SERVICES_MAP.get(part).stop();
		Factory.SERVICES_MAP.remove(part);
	}

	private static boolean
	servicesMapDoesNotContain(final IWorkbenchPart part) {
		return !Factory.SERVICES_MAP.containsKey(part);
	}

	@Override
	public Instance
	start() {
		Factory.enableAutomaticInserterFor(Factory.PART_SERVICE.getActivePart());
		Factory.PART_SERVICE.addPartListener(Factory.PART_LISTENER);
		return this;
	}

	@Override
	public Instance
	stop() {
		Factory.PART_SERVICE.removePartListener(Factory.PART_LISTENER);
		Factory.stopAllInserterServices();
		return this;
	}
}
