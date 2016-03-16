package pkgModel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AppearanceRequestWrapper {
	private Appointment selectedAppointment;
	private String bandname;
	private String username;
	private String password;
	private Boolean accepted;
	
	public AppearanceRequestWrapper() {
	}

	public AppearanceRequestWrapper(Appointment selectedAppointment, String bandname, String username,
			String password, Boolean accepted) {
		super();
		this.selectedAppointment = selectedAppointment;
		this.bandname = bandname;
		this.username = username;
		this.password = password;
		this.accepted = accepted;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Appointment getSelectedAppointment() {
		return selectedAppointment;
	}

	public void setSelectedAppointment(Appointment selectedAppointment) {
		this.selectedAppointment = selectedAppointment;
	}

	public String getBandname() {
		return bandname;
	}

	public void setBandname(String bandname) {
		this.bandname = bandname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
}
