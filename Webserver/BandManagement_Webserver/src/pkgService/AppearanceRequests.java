package pkgService;

import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.AppearanceRequestWrapper;
import pkgModel.Appointment;
import pkgModel.DatabaseManager;

@Path("/appearanceRequests")
public class AppearanceRequests {
	private DatabaseManager db = new DatabaseManager();
	
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void answerAppearanceRequest(AppearanceRequestWrapper arWrapper) {
		db.answerAppearanceRequest(arWrapper.getSelectedAppointment(), arWrapper.getBandname(), arWrapper.getUsername(), arWrapper.getPassword(), arWrapper.getAccepted());
	}
	
	@GET
	@Path("{bandname}/{username}/{password}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public Appointment[] getUnansweredAppearanceRequests(@PathParam("bandname") String bandname, 
			@PathParam("username") String username, @PathParam("password") String password) {
		Vector<Appointment> appointments = db.getUnansweredAppearanceRequests(bandname, username, password);
		return appointments.toArray(new Appointment[appointments.size()]);
	}
	
	@DELETE
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public void deleteAppearanceRequest(String id) {
		db.deleteAppearanceRequest(Integer.parseInt(id));
	}
}