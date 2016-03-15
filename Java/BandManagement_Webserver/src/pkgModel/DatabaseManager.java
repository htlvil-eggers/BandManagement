package pkgModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import oracle.jdbc.OracleDriver;


public class DatabaseManager {
	//static final String DB_URL = "jdbc:oracle:thin:@192.168.128.151:1521:ora11g"; //intern
    static final String DB_URL = "jdbc:oracle:thin:@212.152.179.117:1521:ora11g"; //extern
	private String USER = "d5bhifs25";
	private String PASS = "edelBlech";
    private Connection conn = null;
   // private Statement stmt = null;
   
	public DatabaseManager(String username, String password){
		   this.USER  = username;
		   this.PASS = password;
	   }
	   
	public DatabaseManager(){
		super();
		try {
			this.createConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection createConnection() throws Exception {
		DriverManager.registerDriver(new OracleDriver());
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}
	   
	   
	public ResultSet getData(String statement){
		ResultSet retValue = null;
		try{
		conn = createConnection();
		PreparedStatement stmt = null;

		stmt = conn.prepareStatement(statement,	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		retValue  = stmt.executeQuery();
		
		}catch(Exception e){
			System.out.println("Error in get Data");
			retValue =  null;
		}
		
		return retValue;
	}

	public void closeCon(){
	   try {
		   conn.close();
	  } catch (SQLException e) {
		  e.printStackTrace();
	  }
	}

	public Vector<Location> getLocations(){
		ResultSet rs = this.getData("select id, name from locations");
		Vector<Location> locations = new Vector<Location>();
		
		try {
			while (rs.next()) {
				locations.add(new Location(rs.getString(2),rs.getInt(1)));
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return locations;
	}
	
	public Vector<Instrument> getInstruments(){
		ResultSet rs = this.getData("select id, name from instruments");
		Vector<Instrument> instruments = new Vector<Instrument>();
		
		try {
			while (rs.next()) {
				instruments.add(new Instrument(rs.getString(2),rs.getInt(1)));
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return instruments;
	}

	public Musician getMusician(String username, String password) {
		PreparedStatement stmt = null;
		Musician retValue = null;
				
		try {
			stmt = conn.prepareStatement("select m.id, m.username, m.password, m.first_name, " +
					 "m.last_name, m.birthdate, l.id, l.name from musicians m " +
					  "join locations l on m.habitation_id = l.id where m.username = ? and m.password = ?",	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next() && retValue == null) {
				//get instrument skills
				Vector<Instrument> instruments = this.getInstrumentsOfMusician(rs.getInt(1));				
				//get available times
				Vector<AvailableTime> availableTimes = this.getAvailableTimesOfMusician(rs.getInt(1));
				
				retValue =  new Musician(rs.getInt(1),rs.getString(2),rs.getString(3),
						rs.getString(4), rs.getString(5), instruments, 
						new Location(rs.getString(8), rs.getInt(7)), rs.getDate(6), availableTimes);
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retValue;
	}

	private Vector<AvailableTime> getAvailableTimesOfMusician(int musId) throws SQLException {
		Vector<AvailableTime> avTimes = new Vector<AvailableTime>();
		PreparedStatement stmt = conn.prepareStatement("select av.id, av.start_time, av.end_time from available_times av " +
				  "where av.musician_id = ?",	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, musId);
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			avTimes.add(new AvailableTime(rs.getInt(1), rs.getDate(2), rs.getDate(3)));
		}
		
		return avTimes;
	}

	private Vector<Instrument> getInstrumentsOfMusician(int musId) throws SQLException {
		Vector<Instrument> instruments = new Vector<Instrument>();
		PreparedStatement stmt = conn.prepareStatement("select i.id, i.name from instruments i " +
				  "join instrument_skills ins on i.id = ins.instrument_id where ins.musician_id = ?",	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		stmt.setInt(1, musId);
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			instruments.add(new Instrument(rs.getString(2), rs.getInt(1)));
		}
		
		return instruments;
	}

	public void updateMusician(Musician m) {
		try {
			PreparedStatement stmt = conn.prepareStatement("update musicians m set first_name = ?," + 
		         "last_name = ?, birthdate = ?, habitation_id = ?, password = ?",	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, m.getFirstName());
			stmt.setString(2, m.getLastName());
			stmt.setDate(3, new java.sql.Date(m.getBirthdate().getTime()));
			stmt.setInt(4, m.getHabitation().getId());
			stmt.setString(5, m.getPassword());
			stmt.executeUpdate();
			
			//set instrument_skills
			stmt = conn.prepareStatement("delete from instrument_skills where musician_id = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, m.getId());
			stmt.executeUpdate();
			
			for (Instrument i : m.getSkills()) {
				stmt = conn.prepareStatement("insert into instrument_skills values(?, ?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt.setInt(1, m.getId());
				stmt.setInt(2, i.getId());
				stmt.executeUpdate();
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public Vector<RehearsalRequest> getRehearsalRequestsOfBand(String bandname) {
		Vector<RehearsalRequest> rehearsalRequests = new Vector<RehearsalRequest>();
		
		try {
			PreparedStatement stmt = conn.prepareStatement("select r.id, r.start_time, r.end_time, r.duration from " +
		      "rehearsal_requests r join bands b on r.band_id = b.id where b.name = ?",	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, bandname);
			
			ResultSet rs = stmt.executeQuery();		
			while (rs.next()) {
				rehearsalRequests.add(new RehearsalRequest(rs.getInt(1), rs.getDate(2), rs.getDate(3), rs.getDouble(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rehearsalRequests;
	}

	public void addAvailableTime(String bandname, String username, Date from, Date to) {		
		try {
			int bandId = this.getBandIdFromName(bandname);
			int musId = this.getMusicianIdFromName(username);
			
			PreparedStatement stmt = conn.prepareStatement("insert into available_times values(0, ?, ?, ?, ?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, bandId);
			stmt.setInt(2, musId);
			stmt.setDate(3, new java.sql.Date(from.getTime()));
			stmt.setDate(4, new java.sql.Date(to.getTime()));
			stmt.executeUpdate();	
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	private int getMusicianIdFromName(String username) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("select id from musicians where username = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		int musId = -1;
		
		while (rs.next()) {
			musId = rs.getInt(1);
		}
		
		return musId;
	}

	private int getBandIdFromName(String bandname) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("select id from bands where name = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		stmt.setString(1, bandname);
		ResultSet rs = stmt.executeQuery();
		int bandId = -1;
		
		while (rs.next()) {
			bandId = rs.getInt(1);
		}
		
		return bandId;
	}

	public void answerAppearanceRequest(Appointment appReq, String bandname, String username, String password,
			boolean accepted) {		
		try {
			if (checkLogin(bandname, username, password)) {
				PreparedStatement stmt = conn.prepareStatement("insert into appointment_attendances values (?, ?, ?, ?)",	ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt.setInt(1, appReq.getId());
				stmt.setInt(2, this.getBandIdFromName(bandname));
				stmt.setInt(3, this.getMusicianIdFromName(username));
				stmt.setBoolean(4, accepted);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	public boolean checkLogin(String bandname, String username, String password) {
		boolean correct = false;
		try {
			PreparedStatement stmt = conn.prepareStatement("select m.id from musicians m join bandmembers bm on "
					   + "m.id = bm.musician_id where m.username = ? and m.password = ? and bm.band_id = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setInt(3, this.getBandIdFromName(bandname));
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()){
				correct = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return correct;
	}

	public Vector<Appointment> getUnansweredAppearanceRequests(String bandname, String username, String password) {
		Vector<Appointment> appRequests = new Vector<Appointment>();
		Appointment appToRemove = null;
		
		try {
			if (checkLogin(bandname, username, password)) {
				int bandId = this.getBandIdFromName(bandname);
				PreparedStatement stmt = conn.prepareStatement("select apt.id, apt.name, apt.description, apt.grounded, apt.start_time, apt.end_time, "
						+ "l.id, l.name from locations l join appointments apt on l.id = apt.location_id join appearances apc on apt.id = apc.appointment_id " +
						"where apc.band_id = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt.setInt(1, bandId);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()){
					appRequests.add(new Appointment(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
							rs.getDate(5), rs.getDate(6), new Location(rs.getString(8), rs.getInt(7)), EnumAppointmentType.Appearance));
				}
			
				//select only unanswered ones
				stmt = conn.prepareStatement("select aa.appointment_id from appointment_attendances aa join appearances app on aa.appointment_id = " +
						"app.appointment_id where aa.band_id = ? and aa.musician_id = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt.setInt(1, bandId);
				stmt.setInt(2, this.getMusicianIdFromName(username));
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					for (Appointment app : appRequests) {
						if (app.getId() == rs.getInt(1)) {
							appToRemove = app;
						}
					}
					
					if (appToRemove != null) {
						appRequests.remove(appToRemove);
						appToRemove = null;
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return appRequests;
	}
}