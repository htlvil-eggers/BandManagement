package pkgService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import pkgModel.AvailableTimeWrapper;
import pkgModel.DatabaseManager;

@Path("/availableTimes")
public class AvailableTimes {
	private DatabaseManager db = new DatabaseManager();
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void addAvailableTime(AvailableTimeWrapper atWrapper) {
		db.addAvailableTime(atWrapper.getBandname(), atWrapper.getUsername(), atWrapper.getFrom(), atWrapper.getTo());
	}
}
