package pkgService;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.*;

@Path("/rehearsalRequests")
public class RehearsalRequests {
	private DatabaseManager db = new DatabaseManager();
	
  @GET
  @Path("{bandname}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public RehearsalRequest[] getRehearsalRequests(@PathParam("bandname") String bandname) {
	  Vector <RehearsalRequest> rehRequests = db.getRehearsalRequestsOfBand(bandname);
    return (RehearsalRequest[]) rehRequests.toArray(new RehearsalRequest[rehRequests.size()]);
  }
} 