package com.example.stefan.android_client.pkgModel;

import java.util.Date;
import java.util.Vector;

public class Musician {
	private int id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Vector<Instrument> skills = new Vector<Instrument>();
	private Location habitation;
	private Date birthdate;
	private Vector<AvailableTime> availableTimes = new Vector<AvailableTime>();
	
	public Musician(int id, String username, String password, String firstName, String lastName,
			Vector<Instrument> skills, Location habitation, Date birthdate, Vector<AvailableTime> availableTimes) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.skills = skills;
		this.habitation = habitation;
		this.birthdate = birthdate;
		this.availableTimes = availableTimes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Vector<Instrument> getSkills() {
		return skills;
	}

	public void setSkills(Vector<Instrument> skills) {
		this.skills = skills;
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

	public Vector<AvailableTime> getAvailableTimes() {
		return availableTimes;
	}

	public void setAvailableTimes(Vector<AvailableTime> availableTimes) {
		this.availableTimes = availableTimes;
	}
}