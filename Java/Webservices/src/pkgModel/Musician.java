package pkgModel;

import java.util.Date;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Musician {
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Vector<Instrument> instruments = new Vector<Instrument>();
	private Location habitation;
	private Date birthdate;
	
	public Musician(String username, String password, String firstName, String lastName, Vector<Instrument> instruments,
			Location habitation, Date birthdate) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.instruments = instruments;
		this.habitation = habitation;
		this.birthdate = birthdate;
	}

	public Musician() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Vector<Instrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(Vector<Instrument> instruments) {
		this.instruments = instruments;
	}

	public Location getHabitation() {
		return habitation;
	}

	public void setHabitation(Location habitation) {
		this.habitation = habitation;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
}
