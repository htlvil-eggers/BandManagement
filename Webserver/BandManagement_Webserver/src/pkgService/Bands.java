package pkgService;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.AddBandWrapper;
import pkgModel.AppearanceRequest;
import pkgModel.Appointment;
import pkgModel.Band;
import pkgModel.DatabaseManager;
import pkgModel.Musician;

@Path("/bands")
public class Bands {
	private DatabaseManager db = new DatabaseManager();
	
    @POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void addBand (AddBandWrapper abWrapper) {
		db.addBand(abWrapper.getBand(), abWrapper.getUsername());
	}
    
    @PUT
 	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
 	public void updateBand (Band band) {
 		db.updateBand(band);
 	}
    
    @GET
 	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.TEXT_XML})
 	public Band[] getBands() {
 		return db.getBands();
 	}
    
    @POST
    @Path("members")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void addMember (AddBandWrapper abWrapper) {
		db.addMember(abWrapper.getBand(), abWrapper.getUsername());
	}
    
    @POST
    @Path("appointments")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public int addAppointment (AddBandWrapper abWrapper) {
		db.addAppointment(abWrapper.getBand(), abWrapper.getAppointment());
		
		return db.getIdFromAppointment(abWrapper.getBand(), abWrapper.getAppointment());
	}
    
    @POST
    @Path("appearanceRequests")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void addAppearanceRequest (AddBandWrapper abWrapper) {
		db.addAppearanceRequest(abWrapper.getBand(), abWrapper.getAppearanceRequest());
	}
    
    @POST
    @Path("rehearsalRequests")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public int addRehearsalRequest (AddBandWrapper abWrapper) {
		db.addRehearsalRequest(abWrapper.getBand(), abWrapper.getRehearsalRequest());
		
		return db.getIdFromRehearsalRequest(abWrapper.getBand(), abWrapper.getRehearsalRequest());
	}
    
    @DELETE
    @Path("rehearsalRequests")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void removeRehearsalRequest (String id) {
		db.removeRehearsalRequest(Integer.parseInt(id));
	}
    
    @POST
    @Path("appearances")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public int addAppearace (AddBandWrapper abWrapper) {
		db.addAppearance(abWrapper.getBand(), abWrapper.getAppointment());
		
		return db.getIdFromAppointment(abWrapper.getBand(), abWrapper.getAppointment());
	}
    
    @PUT
    @Path("availableTimes")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public Band allAvailableTimes (Band band) {
		return db.allAvailableTimes(band);
	}
    
    @DELETE
    @Path("members")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public void deleteMember (AddBandWrapper abWrapper) {
		db.deleteMember(abWrapper.getBand(), abWrapper.getUsername());
	}
    
    @GET
    @Path("{bandname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public String getBandIdFromName(@PathParam("bandname") String bandname) {
    	return String.valueOf(db.getBandIdFromName(bandname));
    }
    
    @GET
    @Path("{bandname}/{username}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Band getBand(@PathParam("bandname") String bandname,
    		@PathParam("username") String username) {
    	return db.getBand(bandname, username);
    }
    
    @GET
    @Path("answers/{bandname}/{appId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public HashMap<String, Integer> getAnswersForAppointment(@PathParam("bandname") String bandname,
    		@PathParam("appId") String appId) {
    	HashMap<String, Integer> answers = db.getAllAnswersForAppointment(bandname, Integer.parseInt(appId));
    
    	return answers;
    }
    
    @GET
    @Path("musicians/{bandname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Musician[] getMusiciansOfBand(@PathParam("bandname") String bandname) {
    	return db.getMusiciansOfBand(bandname);
    }
    
    @GET
    @Path("appearanceRequests/{bandname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public AppearanceRequest[] getAppearanceRequestsOfBand(@PathParam("bandname") String bandname) {
    	return db.getAppearanceRequestsOfBand(bandname);
    }
    
    @GET
    @Path("appointments/{bandname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Appointment[] getAppointmentsOfBand(@PathParam("bandname") String bandname) {
    	return db.getAppointmentsOfBand(bandname);
    }
}
