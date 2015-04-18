package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.BaseListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;

public final class TextInsertionListener extends BaseListener
	implements
		VerifyListener {

	private final Optional<StyledText> buffer =
		Optional.fromNullable(EditorContext.getBuffer(EditorContext.getEditor()));

	public TextInsertionListener() {
		super();
	}

	@Override
	public void
	verifyText(final VerifyEvent arg0) {
		BaseListener.scheduleErrorChecking();
	}

	@Override
	public void
	add() {
		if (!this.buffer.isPresent()) return;
		this.buffer.get().addVerifyListener(this);
	}

	@Override
	public void
	remove() {
		if (!this.buffer.isPresent()) return;
		this.buffer.get().removeVerifyListener(this);
	}
}
