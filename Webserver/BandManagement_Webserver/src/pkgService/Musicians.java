package pkgService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.*;

@Path("/musicians")
public class Musicians {
	private DatabaseManager db = new DatabaseManager();
	
  @GET
  @Path("{username}/{password}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public Musician getMusician(@PathParam("username") String username,
		                      @PathParam("password") String password) {
    return db.getMusician(username, password);
  }
  
  @PUT
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public void updateMusician (Musician m) {
	  db.updateMusician(m);
  }
  
  @POST
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public void addMusician (Musician m) {
	  db.addMusician(m);
  }
} 