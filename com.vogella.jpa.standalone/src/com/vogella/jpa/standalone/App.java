package com.vogella.jpa.standalone;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class App implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		// should not be used
		return EXIT_OK;
	}

	@Override
	public void stop() {

	}

}
