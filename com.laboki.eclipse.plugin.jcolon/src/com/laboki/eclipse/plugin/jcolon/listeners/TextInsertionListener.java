package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.laboki.eclipse.plugin.jcolon.inserter.EditorContext;
import com.laboki.eclipse.plugin.jcolon.inserter.EventBus;
import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.AbstractListener;

public class TextInsertionListener extends AbstractListener implements VerifyListener {

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
		this.buffer.addVerifyListener(this);
	}

	@Override
	public void remove() {
		this.buffer.removeVerifyListener(this);
	}
}
