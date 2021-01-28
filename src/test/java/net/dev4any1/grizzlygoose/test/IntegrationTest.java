package net.dev4any1.grizzlygoose.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.dev4any1.grizzlygoose.Serverless;
import net.dev4any1.grizzlygoose.test.app.GuiceConfigContextListenerTemplate;
import net.dev4any1.grizzlygoose.test.app.JerseyGuiceApplicationTemplate;
import net.dev4any1.grizzlygoose.test.app.ProbesBinderTemplate;

public class IntegrationTest {

	private static HttpServer server;

	private static Client client = ClientBuilder.newClient();

	@BeforeClass
	public static void init() throws IOException, InterruptedException, ServletException {
		server = Serverless.startServer(
				UriBuilder.fromUri("http://localhost").port(8888).build(), 
				JerseyGuiceApplicationTemplate.class, 
				GuiceConfigContextListenerTemplate.class, 
				ProbesBinderTemplate.bind());
	}

	@AfterClass
	public static void stop() {
		server.shutdown();
	}

	@Test
	public void trace1() {
		Response response = client.target("http://localhost:8888/rest/trace").request().get();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.readEntity(String.class).contains("Service"));		
		response.close();
	}

	@Test
	public void trace2() {
		Response response = client.target("http://localhost:8888/rest/trace").request().get();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.readEntity(String.class).contains("Service"));		
		response.close();
	}
}
