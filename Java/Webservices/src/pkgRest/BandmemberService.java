package pkgRest;

import java.sql.SQLException;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pkgModel.Musician;
import pkgModel.OracleDatabaseManager;

//"jdbc:oracle:thin:@192.168.128.151:1521:ora11g"
//"jdbc:oracle:thin:@212.152.179.117:1521:ora11g"

@Path("BandmemberService")
public class BandmemberService {
	private final String CONNECTION_STRING = "jdbc:oracle:thin:@212.152.179.117:1521:ora11g";
	private final String USERNAME = "d5bhifs25";
	private final String PASSWORD = "edelBlech";
	
	@GET
	@Path("{bandname}")
	@Produces({MediaType.APPLICATION_XML})
	public Musician[] getBandmembers(@PathParam ("bandname") String bandname) {
		Musician []musicians = null;
		
		try (OracleDatabaseManager dbManager = new OracleDatabaseManager (CONNECTION_STRING, USERNAME, PASSWORD)) {
			musicians = dbManager.getMusiciansOfBand(bandname).toArray(new Musician[0]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return musicians;
	}
	
	@POST
	@Path("{bandname}/{musicianName}")
	public String postBandmember(@PathParam("musicianName") String musicianName, @PathParam ("bandname") String bandname) {
		Boolean successful = true;
		
		try (OracleDatabaseManager dbManager = new OracleDatabaseManager (CONNECTION_STRING, USERNAME, PASSWORD)) {
			dbManager.addBandmember(bandname, musicianName);
		} catch (SQLException e) {
			successful = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			successful = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return successful.toString();
	}
	
	@DELETE
	@Path("{bandname}/{musicianName}")
	public String deleteBandmember(@PathParam("bandname") String bandname, @PathParam("musicianName") String musicianName) {
		Boolean successful = true;
		
		try (OracleDatabaseManager dbManager = new OracleDatabaseManager (CONNECTION_STRING, USERNAME, PASSWORD)) {
			dbManager.removeBandmember(bandname, musicianName);
		} catch (SQLException e) {
			successful = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			successful = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return successful.toString();
	}
}
