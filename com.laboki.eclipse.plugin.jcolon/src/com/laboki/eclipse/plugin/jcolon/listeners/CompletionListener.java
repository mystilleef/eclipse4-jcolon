package com.laboki.eclipse.plugin.jcolon.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.jcolon.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.jcolon.instance.EventBusInstance;
import com.laboki.eclipse.plugin.jcolon.instance.Instance;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.main.EventBus;
import com.laboki.eclipse.plugin.jcolon.task.Task;

public final class CompletionListener extends EventBusInstance
	implements
		ICompletionListener {

	private static final Logger LOGGER =
		Logger.getLogger(CompletionListener.class.getName());
	private final IEditorPart editor = EditorContext.getEditor();
	private final Optional<ContentAssistantFacade> contentAssistant =
		this.getContentAssistant();
	private final Optional<IQuickAssistAssistant> quickAssistant =
		this.getQuickAssistant();

	public CompletionListener() {
		super();
	}

	@Override
	public void
	assistSessionEnded(final ContentAssistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.post(new AssistSessionEndedEvent());
			}
		}.start();
	}

	@Override
	public void
	assistSessionStarted(final ContentAssistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.post(new AssistSessionStartedEvent());
			}
		}.start();
	}

	@Override
	public void
	selectionChanged(final ICompletionProposal arg0, final boolean arg1) {}

	@Override
	public Instance
	start() {
		this.tryToAdd();
		return super.start();
	}

	private void
	tryToAdd() {
		try {
			this.add();
		}
		catch (final Exception e) {
			CompletionListener.LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	private void
	add() {
		if (this.contentAssistant.isPresent()) this.contentAssistant.get()
			.addCompletionListener(this);
		if (this.quickAssistant.isPresent()) this.quickAssistant.get()
			.addCompletionListener(this);
	}

	@Override
	public Instance
	stop() {
		this.tryToRemove();
		return super.stop();
	}

	private void
	tryToRemove() {
		try {
			this.remove();
		}
		catch (final Exception e) {
			CompletionListener.LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	private void
	remove() {
		if (this.contentAssistant.isPresent()) this.contentAssistant.get()
			.removeCompletionListener(this);
		if (this.quickAssistant.isPresent()) this.quickAssistant.get()
			.removeCompletionListener(this);
	}

	private Optional<ContentAssistantFacade>
	getContentAssistant() {
		return Optional.fromNullable(EditorContext.getView(this.editor)
			.getContentAssistantFacade());
	}

	private Optional<IQuickAssistAssistant>
	getQuickAssistant() {
		return Optional.fromNullable(EditorContext.getView(this.editor)
			.getQuickAssistAssistant());
	}
}
