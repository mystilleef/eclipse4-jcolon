package com.laboki.eclipse.plugin.jcolon.listeners;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

import com.laboki.eclipse.plugin.jcolon.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.jcolon.main.EditorContext;
import com.laboki.eclipse.plugin.jcolon.main.EventBus;

public final class KeyEventListener extends AbstractListener implements KeyListener {

	private final Control control = EditorContext.getControl(EditorContext.getEditor());

	public KeyEventListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void keyPressed(final KeyEvent arg0) {
		EditorContext.cancelAllJobs();
	}

	@Override
	public void keyReleased(final KeyEvent arg0) {
		this.scheduleErrorChecking();
	}

	@Override
	public void add() {
		if (this.control == null) return;
		this.control.addKeyListener(this);
	}

	@Override
	public void remove() {
		if (this.control == null) return;
		this.control.removeKeyListener(this);
	}
}
