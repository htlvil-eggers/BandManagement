package pkgService;

import java.util.Vector;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pkgModel.*;

@Path("/locations")
public class Locations {
	private DatabaseManager db = new DatabaseManager();
	
  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public Location[] getLocations() {
	  Vector <Location> locations = db.getLocations();
	  
      return locations.toArray(new Location[locations.size()]);
  }
  
  @GET
  @Path("spatial")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
  public AddSpatialWrapper[] getLocationsSpatial() {
	  Vector <AddSpatialWrapper> wrappers = db.getLocationsSpatial();
	  
      return wrappers.toArray(new AddSpatialWrapper[wrappers.size()]);
  }
  
  @POST
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
  public String addLocation(AddSpatialWrapper wrapper) {
	  db.addLocation(wrapper);
	  
      return String.valueOf(db.getIdFromLocation(wrapper.getLocation()));
  }
  
  @PUT
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public void updateLocation(AddSpatialWrapper wrapper) {
	  db.updateLocation(wrapper);
  }
  
  @DELETE
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public void deleteLocation(String id) {
	  db.deleteLocation(Integer.parseInt(id));
  }
} 