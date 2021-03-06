package pkgService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.DatabaseManager;

@Path("/login")
public class Login {
	private DatabaseManager db = new DatabaseManager();
	
	@GET
	@Path("{bandname}/{username}/{password}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public String login(@PathParam("bandname") String bandname, 
			@PathParam("username") String username, @PathParam("password") String password) {
		return Boolean.toString(db.checkLogin(bandname, username, password));
	}
}
