package pkgModel;

import java.util.Date;

public class Appointment {
	private int id;
	private String name;
	private String description;
	private Location location;
	private Date startTime;
	private Date endTime;
	
	public Appointment(int id, String name, String description, Location location, Date startTime, Date endTime) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public String toString() {
		return name;
	}
}