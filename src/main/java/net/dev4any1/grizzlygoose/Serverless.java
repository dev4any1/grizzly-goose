package net.dev4any1.grizzlygoose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

public class Serverless {

	public final static Logger LOG = Logger.getLogger(Serverless.class.getName());

	private final static Properties PROPS = loadProperties();

	public static final URI BASE_URI = UriBuilder.fromUri(PROPS.getProperty("net.dev4any1.grizzlygoose.base-url"))
			.port(Integer.parseInt(PROPS.getProperty("net.dev4any1.grizzlygoose.port"))).build();

	/**
	 * Starting glassfish.grizzly.http.server in daemon mode based on values of
	 * serverless.properties
	 * 
	 * @param appClass    class with RSApplication that should lay on the root
	 *                    package with the DI components awaiting for scanning
	 * @param configClass class with Guice Servlet bindings
	 * @param probes      HTTPServerProbes to trace or manage
	 */

	public static void startDaemon(Class<? extends ResourceConfig> appClass,
			Class<? extends GuiceServletContextListener> configClass, HttpServerProbe[] probes) {

		try {
			HttpServer server = startServer(BASE_URI, appClass, configClass, probes);
			// blocks until the process is terminated
			Thread.currentThread().join();
			server.shutdown();
		} catch (InterruptedException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Starting glassfish.grizzly.http.server
	 * 
	 * @param baseUri     URL to start on
	 * @param appClass    class with RSApplication that should lay on the root
	 *                    package of the DI components awaiting for scanning
	 * @param configClass class with Guice Servlet bindings
	 * @param probes      HTTPServerProbes to trace or manage
	 * @return instance of configured and running Grizzly server
	 */

	public static HttpServer startServer(URI baseUri, Class<? extends ResourceConfig> appClass,
			Class<? extends GuiceServletContextListener> configClass, HttpServerProbe[] probes) {

		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, false);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			server.shutdownNow();
		}));
		server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probes);
		try {
			greets();
			createGuiceWebappContext(appClass, configClass).deploy(server);
			server.start();
		} catch (IOException | ServletException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		return server;
	}

	/**
	 * Creates a @ServletContainer with provided @Application class And applies
	 * the @GuiceFilter for being able to DI and IOC with application resources
	 * within predefined @GuiceConfigContextListener context listener
	 * 
	 * @param rsApplicationClass Application class
	 * @return @ServletContainer
	 * @throws ServletException
	 */

	private static WebappContext createGuiceWebappContext(Class<? extends Application> appClass,
			Class<? extends ServletContextListener> configClass) throws ServletException {
		final WebappContext context = new WebappContext("GuiceWebappContext", "");
		context.addListener(configClass);

		ServletRegistration registration = context.addServlet("ServletContainer", ServletContainer.class);
		registration.addMapping("/*");
		registration.setInitParameter("javax.ws.rs.Application", appClass.getName());
		registration.setInitParameter("jersey.config.server.provider.classnames", MultiPartFeature.class.getName());
		final FilterRegistration filter = context.addFilter("GuiceFilter", GuiceFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");
		context.createListener(configClass);

		return context;
	}

	/**
	 * Do load properties
	 * 
	 * @return Properties loaded from serverless.properties resource
	 */

	private static Properties loadProperties() {
		Properties props = new Properties();
		try {
			InputStream is = Serverless.class.getClassLoader().getResource("serverless.properties").openStream();
			props.load(is);
			is.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		return props;
	}

	/**
	 * Printing out application Logo and version
	 * 
	 * @throws IOException
	 */

	private static void greets() throws IOException {
		if (Boolean.valueOf(PROPS.getProperty("net.dev4any1.grizzlygoose.showLogo"))) {
			final String logo = new BufferedReader(new InputStreamReader(
					Serverless.class.getClassLoader().getResource("logo").openStream(), StandardCharsets.UTF_8)).lines()
							.collect(Collectors.joining("\n"));
			final String version = PROPS.getProperty("net.dev4any1.grizzlygoose.version");
			System.out.println(logo + "\n v." + version);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getDefault());
		LOG.info("Alive at " + BASE_URI.toString() + " from " + sdf.format(new Date(System.currentTimeMillis())));
	}
}
