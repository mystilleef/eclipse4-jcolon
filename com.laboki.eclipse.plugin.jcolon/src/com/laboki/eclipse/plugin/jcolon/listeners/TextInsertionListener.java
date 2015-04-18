package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.BaseListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;

public final class TextInsertionListener extends BaseListener
	implements
		VerifyListener {

	private final StyledText buffer =
		EditorContext.getBuffer(EditorContext.getEditor());

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
		if (this.buffer == null) return;
		this.buffer.addVerifyListener(this);
	}

	@Override
	public void
	remove() {
		if (this.buffer == null) return;
		this.buffer.removeVerifyListener(this);
	}
}
