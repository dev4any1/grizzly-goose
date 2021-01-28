package net.dev4any1.grizzlygoose.test.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Implementation of the @GuiceServletContextListener.
 * "Logical place to hold the Dependency Injector" (c) Guice Team.
 * 
 * Defines the producers of the application resources.
 * 
 */

public class GuiceConfigContextListenerTemplate extends GuiceServletContextListener {

	public static Injector injector = Guice.createInjector(new ServletModule() {
		@Override
		protected void configureServlets() {
			bind(ServiceTemplate.class);
			bind(RestResourceTemplate.class);
		}
	});

	@Override
	protected Injector getInjector() {
		return injector;
	}
}
