package pkgModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		int userId = getIdOfCredentials (username, password);
		int bandId = getIdOfBand (bandname);
		
		if (userId != NO_ID && bandId != NO_ID) {
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

	private int getIdOfCredentials(String username, String password) throws SQLException {
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
			int musicianId = this.getIdOfCredentials(musician.getUsername(), musician.getPassword());
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
			statement.setDate(4, musician.getBirthdate());
			statement.setInt(5, locationId);
			statement.setString(6, musician.getUsername());
			
			statement.executeUpdate();
		}
		
		Vector<Integer> instrumentIds = getIdsOfInstruments(musician.getInstruments());
		int musicianId = getIdOfCredentials (musician.getUsername(), musician.getPassword());
		
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
}