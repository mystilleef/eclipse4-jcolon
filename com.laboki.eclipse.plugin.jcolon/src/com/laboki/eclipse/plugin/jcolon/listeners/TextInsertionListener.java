package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.main.EventBus;

public final class TextInsertionListener extends AbstractListener implements VerifyListener {

	private final StyledText buffer = EditorContext.getBuffer(EditorContext.getEditor());

	public TextInsertionListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void verifyText(final VerifyEvent arg0) {
		this.scheduleErrorChecking();
	}

	@Override
	public void add() {
		if (this.buffer == null) return;
		this.buffer.addVerifyListener(this);
	}

	@Override
	public void remove() {
		if (this.buffer == null) return;
		this.buffer.removeVerifyListener(this);
	}
}
