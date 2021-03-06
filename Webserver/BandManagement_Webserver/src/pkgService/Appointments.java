package pkgService;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.Appointment;
import pkgModel.DatabaseManager;

@Path("/appointments")
public class Appointments {
	private DatabaseManager db = new DatabaseManager();
	
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public void groundAppointment(String id) {
    	db.groundAppointment(Integer.parseInt(id));
    }
    
    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public void removeAppointment(Appointment app) {
    	db.removeAppointment(app);
    }
    
    @GET
    @Path("{bandname}/{appId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public String haveAllMembersAccepted(@PathParam("bandname") String bandname,
    		@PathParam("appId") String id) {
    	return Boolean.toString(db.haveAllMembersAcceptedAppointment(bandname, Integer.parseInt(id)));
    }
}
