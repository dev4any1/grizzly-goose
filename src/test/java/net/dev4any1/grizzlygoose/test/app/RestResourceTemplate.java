package net.dev4any1.grizzlygoose.test.app;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.google.inject.servlet.RequestScoped;

@Path("/rest")
@RequestScoped
public class RestResourceTemplate {

	public final static Logger LOG = Logger.getLogger(RestResourceTemplate.class.getName());	

	@Inject
	public ServiceTemplate service;

	@GET
	@Path("/trace")
	public Response trace() {
		String result = this.hashCode() + service.trace();
		LOG.info(result);
		return Response.status(Response.Status.OK).entity(result).build();
	}
}