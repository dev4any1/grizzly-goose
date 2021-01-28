package net.dev4any1.grizzlygoose.test.app;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;

public class ProbesBinderTemplate {

	public static HttpServerProbe[] bind() {
		HttpServerProbe onRequestTimeoutEventProbe = new HttpServerProbe.Adapter() {
			@SuppressWarnings({ "rawtypes" })
			public void onRequestTimeoutEvent(HttpServerFilter filter, Connection connection, Request request) {
				System.out.println("Timeout: " + request.getRequestURI() + ":" + System.currentTimeMillis());
			}
		};
		HttpServerProbe onRequestReceiveEventProbe = new HttpServerProbe.Adapter() {
			@SuppressWarnings("rawtypes")
			public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
				System.out.println(request.getRequestURI() + ":" + System.currentTimeMillis());
			}
		};
		HttpServerProbe[] result = {onRequestTimeoutEventProbe, onRequestReceiveEventProbe};		
		return result;
	}
}