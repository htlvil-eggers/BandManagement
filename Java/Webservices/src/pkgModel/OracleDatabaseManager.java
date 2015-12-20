package pkgModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

public class OracleDatabaseManager implements AutoCloseable {
	private static final int NO_ID = -1;
	
	private final String connectionString;
	private final String username;
	private final String password;
	
	private Connection connection;
	
	public OracleDatabaseManager (String _connectionString, String _username, String _password) throws SQLException {
		connectionString = _connectionString;
		username = _username;
		password = _password;
		
		createConnection();
	}
	
	private void createConnection() throws SQLException {
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		
		connection = DriverManager.getConnection (connectionString, username, password);
	}
	
	public boolean checkCredentials (String username, String password, String bandname) throws SQLException {
		boolean credentialsCorrect = false;
		int userId = getIdOfUsername (username);
		int bandId = getIdOfBand (bandname);
		
		if (userId != NO_ID && bandId != NO_ID && checkUsernameAndPassword (username, password)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet resultSet = statement.executeQuery ("SELECT COUNT(*) AS is_member FROM bandmembers WHERE band_id = " + bandId + " AND musician_id = " + userId)) {
					resultSet.next();
					
					if (resultSet.getInt ("is_member") == 1) {
						credentialsCorrect = true;
					}
				}
			}
		}
		
		return credentialsCorrect;
	}
	
	private boolean checkUsernameAndPassword (String username, String password) throws SQLException {
		int id = NO_ID;
		
		try (PreparedStatement statement = connection.prepareStatement ("SELECT id FROM musicians WHERE username = ? AND password = ?")) {
			statement.setString(1, username);
			statement.setString(2, password);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					id = resultSet.getInt("id");
				}
			}
		}
		
		return id == NO_ID ? false : true;
	}

	private int getIdOfBand(String bandname) throws SQLException {
		int id = NO_ID;
		
		try (PreparedStatement statement = connection.prepareStatement ("SELECT id FROM bands WHERE name = ?")) {
			statement.setString(1, bandname);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					id = resultSet.getInt("id");
				}
			}
		}
		
		return id;
	}

	private int getIdOfUsername(String username) throws SQLException {
		int id = NO_ID;
		
		try (PreparedStatement statement = connection.prepareStatement ("SELECT id FROM musicians WHERE username = ?")) {
			statement.setString(1, username);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					id = resultSet.getInt("id");
				}
			}
		}
		
		return id;
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	public Vector<Location> getLocations() throws SQLException {
		Vector<Location> locations = new Vector<Location>();
		
		try (Statement statement = connection.createStatement()) {
			try (ResultSet resultSet = statement.executeQuery("SELECT name FROM locations")) {
				while (resultSet.next()) {
					locations.add(new Location (resultSet.getString("name")));
				}
			}
		}
		
		return locations;
	}

	public Vector<Instrument> getInstruments() throws SQLException {
		Vector<Instrument> instruments = new Vector<Instrument>();
		
		try (Statement statement = connection.createStatement()) {
			try (ResultSet resultSet = statement.executeQuery("SELECT name FROM instruments")) {
				while (resultSet.next()) {
					instruments.add(new Instrument (resultSet.getString("name")));
				}
			}
		}
		
		return instruments;
	}
	
	public Musician getMusician (String username) throws SQLException {
		Musician musician = null;
	
		try (PreparedStatement statement = connection.prepareStatement("SELECT id, password, first_name, last_name, birthdate, habitation_id FROM musicians WHERE username = ?")) {
			statement.setString(1, username);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					musician = new Musician();
					
					musician.setUsername(username);
					musician.setPassword (resultSet.getString("password"));
					musician.setFirstName(resultSet.getString("first_name"));
					musician.setLastName(resultSet.getString("last_name"));
					musician.setBirthdate(resultSet.getDate("birthdate"));
					
					musician.setHabitation (getLocationById (resultSet.getInt("habitation_id")));
				}
			}
		}
		
		if (musician != null) {
			int musicianId = getIdOfUsername(musician.getUsername());
			Vector<Integer> instrumentIds = new Vector<Integer>();
					
			try (Statement statement = connection.createStatement()) {
				try (ResultSet resultSet = statement.executeQuery("SELECT instrument_id FROM instrument_skills WHERE musician_id = " + musicianId)) {
					while (resultSet.next()) {
						instrumentIds.add (resultSet.getInt("instrument_id"));
					}
				}
			}
			
			musician.setInstruments (getInstrumentsByIds (instrumentIds));
		}
		
		return musician;
	}

	public void updateMusician(Musician musician) throws SQLException {
		int locationId = getIdOfLocation (musician.getHabitation());
		
		try (PreparedStatement statement = connection.prepareStatement("UPDATE musicians SET password = ?, first_name = ?, last_name = ?, birthdate = ?, habitation_id = ?  WHERE username = ?")) {
			statement.setString(1, musician.getPassword());
			statement.setString(2, musician.getFirstName());
			statement.setString(3, musician.getLastName());
			statement.setDate(4, new Date(musician.getBirthdate().getTime()));
			statement.setInt(5, locationId);
			statement.setString(6, musician.getUsername());
			
			statement.executeUpdate();
		}
		
		Vector<Integer> instrumentIds = getIdsOfInstruments(musician.getInstruments());
		int musicianId = getIdOfUsername (musician.getUsername());
		
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("DELETE FROM instrument_skills WHERE musician_id = " + musicianId);
		}
		
		try (PreparedStatement statement = connection.prepareStatement ("INSERT INTO instrument_skills VALUES (?, ?)")) {
			for (Integer instrumentId : instrumentIds) {
				statement.setInt(1, musicianId);
				statement.setInt(2, instrumentId);
				
				statement.executeUpdate();
			}
		}
	}
	
	private Location getLocationById (int locationId) throws SQLException {
		Location location = null;
		
		try (Statement statement = connection.createStatement()) {
			try (ResultSet resultSet = statement.executeQuery(("SELECT name FROM locations WHERE id = " + locationId))) {
				if (resultSet.next()) {
					location = new Location (resultSet.getString("name"));
				}
			}
		}
		
		return location;
	}
	
	private Vector<Instrument> getInstrumentsByIds (Vector<Integer> instrumentIds) throws SQLException {
		Vector<Instrument> instruments = new Vector<Instrument>();
		
		try (PreparedStatement statement = connection.prepareStatement ("SELECT name FROM instruments WHERE id = ?")) {
			for (Integer instrumentId : instrumentIds) {
				statement.setInt(1, instrumentId);
				
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {	
						instruments.add (new Instrument (resultSet.getString("name")));
					}
				}
			}
			
		}
		
		return instruments;
	}

	private Vector<Integer> getIdsOfInstruments(Vector<Instrument> instruments) throws SQLException {
		Vector<Integer> ids = new Vector<Integer>();
		
		try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM instruments WHERE name = ?")) {
			for (Instrument instrument : instruments) {
				statement.setString(1, instrument.getName());
				
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						ids.add (resultSet.getInt("id"));
					}
				}
			}
		}
		
		return ids;
	}

	private int getIdOfLocation(Location habitation) throws SQLException {
		int id = NO_ID;
		
		try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM locations WHERE name = ?")) {
			statement.setString(1, habitation.getName());
			
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					id = resultSet.getInt("id");
				}
			}
		}
		
		return id;
	}
	
	public void answerAppointmentRequest (Appointment appointment, String bandname, String username, boolean accepted) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO appointment_attendances VALUES (?, ?, ?, ?)")) {
			statement.setInt (1, appointment.getId());
			statement.setInt(2, getIdOfBand(bandname));
			statement.setInt(3, getIdOfUsername(username));
			statement.setInt(4, accepted ? 1 : 0);
			
			statement.executeUpdate();
		}
	}
	
	public Vector<Appointment> getUnansweredAppointmentRequests (String username, String bandname) throws SQLException {
		Vector<Appointment> appointments = new Vector<Appointment>();
		
		try (PreparedStatement statement = connection.prepareStatement("select id, location_id, start_time, end_time, grounded, name, description from appointments ao where not exists (select * from appointment_attendances aa where aa.appointment_id = ao.id and aa.MUSICIAN_ID = ? and aa.BAND_ID = ao.BAND_ID) and ao.BAND_ID = ?")) {
			int musicianId = getIdOfUsername (username);
			int bandId = getIdOfBand(bandname);
			
			statement.setInt(1, musicianId);
			statement.setInt(2, bandId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
			
				while (resultSet.next()) {
					appointments.add (new Appointment (resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("description"), getLocationById (resultSet.getInt("locaiont_id")), resultSet.getTimestamp("start_time"), resultSet.getTimestamp("end_time")));
				}
			}
		}
		
		return appointments;
	}

	public void answerAppearanceRequest(Appearance appearance, String bandname, String musicianName, boolean accepted) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO appointment_attendances VALUES (?, ?, ?, ?)")) {
			statement.setInt (1, appearance.getId());
			statement.setInt(2, getIdOfBand(bandname));
			statement.setInt(3, getIdOfUsername(musicianName));
			statement.setInt(4, accepted ? 1 : 0);
			
			statement.executeUpdate();
		}
	}

	public Vector<Appearance> getUnansweredAppearanceRequests(String username, String bandname) throws SQLException {
		Vector<Appearance> appearances = new Vector<Appearance>();
		
		try (PreparedStatement statement = connection.prepareStatement("select id, location_id, start_time, end_time, grounded, name, description from appointments ao inner join appearances ae on ao.id = ae.appointment_id where not exists (select * from appointment_attendances aa where aa.appointment_id = ae.APPOINTMENT_ID and aa.MUSICIAN_ID = ? and aa.BAND_ID = ae.BAND_ID) and ae.BAND_ID = ?")) {
			int musicianId = getIdOfUsername (username);
			int bandId = getIdOfBand(bandname);
			
			statement.setInt(1, musicianId);
			statement.setInt(2, bandId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
			
				while (resultSet.next()) {
					appearances.add (new Appearance (resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("description"), getLocationById (resultSet.getInt("locaiont_id")), resultSet.getTimestamp("start_time"), resultSet.getTimestamp("end_time")));
				}
			}
		}
		
		return appearances;
	}

	public Vector<RehearsalRequest> getRehearsalRequests(String bandname) throws SQLException {
		Vector<RehearsalRequest> rehearsalRequests = new Vector<RehearsalRequest>();
		
		try (PreparedStatement statement = connection.prepareStatement("SELECT start_time, end_time, duration FROM rehearsal_requests WHERE band_id = ?")) {
			statement.setInt(1, getIdOfBand(bandname));
			
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					rehearsalRequests.add(new RehearsalRequest (resultSet.getTimestamp("start_time"), resultSet.getTimestamp("end_time"), resultSet.getDouble("duration")));
				}
			}
		}
		
		return rehearsalRequests;
	}

	public void addAvailableTimes(String bandname, String username, java.util.Date dateFrom, java.util.Date dateTo) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO available_times VALUES (0,?,?,?,?)")) {
			statement.setInt(1, getIdOfBand(bandname));
			statement.setInt(2, getIdOfUsername(username));
			statement.setTimestamp(3, new Timestamp(dateFrom.getTime()));
			statement.setTimestamp(4, new Timestamp(dateTo.getTime()));
			
			statement.executeUpdate();
		}
	}

	public Vector<Musician> getMusiciansOfBand(String bandname) throws SQLException {
		Vector<Musician> musicians = new Vector<Musician>();
		
		try (PreparedStatement statement = connection.prepareStatement("SELECT username FROM bandmembers b INNER JOIN musicians m ON b.musician_id = m.id  WHERE band_id = ?")) {
			statement.setInt(1, getIdOfBand(bandname));
			
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					musicians.add(getMusician(rs.getString("username")));
				}
			}
		}
		
		return musicians;
	}

	public void addBandmember(String bandname, String username) throws SQLException {
		int bandId = getIdOfBand(bandname);
		int musicianId = getIdOfUsername(username);
		
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO bandmembers VALUES (?,?,?)")) {
			statement.setInt(1, bandId);
			statement.setInt(2, musicianId);
			statement.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
			
			statement.executeUpdate();
		}
	}

	public void removeBandmember(String bandname, String musicianName) throws SQLException {
		int bandId = getIdOfBand(bandname);
		int musicianId = getIdOfUsername(username);
		
		try (PreparedStatement statement = connection.prepareStatement("DELETE FROM bandmembers WHERE band_id = ? AND musician_id = ?")) {
			statement.setInt(1, bandId);
			statement.setInt(2, musicianId);
			
			statement.executeUpdate();
		}
	}
}