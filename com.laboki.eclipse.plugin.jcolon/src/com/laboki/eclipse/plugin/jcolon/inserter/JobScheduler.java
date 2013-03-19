package com.laboki.eclipse.plugin.jcolon.inserter;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;

@ToString
final class JobScheduler extends Job implements Runnable {

	@Getter private final SemiColonInserter inserter;
	private final Runnable inserterJobRunnable = this.new InserterRunnable();

	public JobScheduler(final String name, final SemiColonInserter inserter) {
		super(name);
		this.inserter = inserter;
		this.setPriority(Job.DECORATE);
	}

	@Override
	public void run() {
		this.schedule();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		EditorContext.asyncExec(this.inserterJobRunnable);
		return Status.OK_STATUS;
	}

	private final class InserterRunnable implements Runnable {

		private final Problem problem = new Problem();
		private final FileSyncer syncer = new FileSyncer();
		private final IEditorPart editor = EditorContext.getEditor();

		public InserterRunnable() {}

		@Override
		public void run() {
			if (this.hasMissingSemiColonError()) this.insertSemiColon();
		}

		private boolean hasMissingSemiColonError() {
			return this.hasJDTErrors() && this.problem.isMissingSemiColonError();
		}

		private boolean hasJDTErrors() {
			EditorContext.asyncExec(this.syncer);
			return EditorContext.hasJDTErrors(this.editor);
		}

		private void insertSemiColon() {
			JobScheduler.this.getInserter().insertSemiColon();
		}
	}
}
