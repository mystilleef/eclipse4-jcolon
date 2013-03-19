package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;

import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterAnnotationModelListenerHandler;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.IInserterListener;
import com.laboki.eclipse.plugin.jcolon.inserter.listeners.InserterAnnotationModelListener;

final class ProblemAnnotations implements Runnable, IInserterAnnotationModelListenerHandler {

	@Getter private final SemiColonInserter inserter;
	@Getter private final JobScheduler inserterScheduler;
	private final Runnable inserterSchedulerRunnable = new InserterSchedulerRunnable();
	private final IInserterListener listener = new InserterAnnotationModelListener(this);

	public ProblemAnnotations(final SemiColonInserter inserter) {
		this.inserter = inserter;
		this.inserterScheduler = new JobScheduler("SemiColonInserterScheduler", ProblemAnnotations.this.getInserter());
	}

	@Override
	public void run() {
		this.listener.start();
	}

	@Override
	public void annotationModelChanged() {
		EditorContext.asyncExec(this.inserterSchedulerRunnable);
	}

	private final class InserterSchedulerRunnable implements Runnable {

		public InserterSchedulerRunnable() {}

		@Override
		public void run() {
			EditorContext.asyncExec(this.scheduleSemiColonInsertion());
		}

		private JobScheduler scheduleSemiColonInsertion() {
			return ProblemAnnotations.this.getInserterScheduler();
		}
	}
}
