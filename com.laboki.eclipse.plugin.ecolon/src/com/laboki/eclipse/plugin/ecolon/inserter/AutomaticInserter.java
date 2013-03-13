package com.laboki.eclipse.plugin.ecolon.inserter;

import java.util.logging.Level;

import lombok.ToString;
import lombok.extern.java.Log;

@ToString
@Log
final class AutomaticInserter implements Runnable {

	public AutomaticInserter() {}

	@Override
	public void run() {
		AutomaticInserter.log.log(Level.INFO, "initialized automatic semicolon inserter");
	}
}
