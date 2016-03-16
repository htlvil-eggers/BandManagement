package pkgService;

import java.util.Vector;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.AddSpatialWrapper;
import pkgModel.DatabaseManager;

@Path("/streets")
public class Streets {
	private DatabaseManager db = new DatabaseManager();
	
	@PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void updateStreet(AddSpatialWrapper wrapper) {
		db.updateStreet(wrapper);
	}
	
	@POST
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public String addStreet(AddSpatialWrapper wrapper) {
		db.addStreet(wrapper);
		  
	    return String.valueOf(db.getIdFromStreet(wrapper.getStreet()));
	}
	
	@GET
	@Path("spatial")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public AddSpatialWrapper[] getStreetsSpatial() {
		Vector <AddSpatialWrapper> wrappers = db.getStreetsSpatial();
		  
	    return wrappers.toArray(new AddSpatialWrapper[wrappers.size()]);
	}
	
	  @DELETE
	  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	  public void deleteStreet(String id) {
		  db.deleteStreet(Integer.parseInt(id));
	  }
}
