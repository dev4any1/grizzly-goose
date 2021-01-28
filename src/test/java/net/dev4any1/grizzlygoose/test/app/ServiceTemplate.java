package net.dev4any1.grizzlygoose.test.app;

import com.google.inject.Singleton;

@Singleton
public class ServiceTemplate {
	public String trace() {
		return " Service:" + this.hashCode() + ":" + System.currentTimeMillis();
	}
}