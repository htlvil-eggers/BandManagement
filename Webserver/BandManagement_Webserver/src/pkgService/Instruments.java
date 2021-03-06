package pkgService;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pkgModel.*;

@Path("/instruments")
public class Instruments {
	private DatabaseManager db = new DatabaseManager();
	
  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
  public Instrument[] getInstruments() {
	  Vector <Instrument> instruments = db.getInstruments();
    return instruments.toArray(new Instrument[instruments.size()]);
  }
} 