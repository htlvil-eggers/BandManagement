package pkgModel;

import java.util.Date;

public class Appearance extends Appointment {
	public Appearance(int id, String name, String description, Location location, Date startTime, Date endTime) {
		super (id, name, description, location, startTime, endTime);
	}
}
