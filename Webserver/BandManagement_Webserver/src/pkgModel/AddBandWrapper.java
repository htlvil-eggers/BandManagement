package pkgModel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddBandWrapper {
	private Band band;
	private String username;
	private Appointment appointment;
	private RehearsalRequest rehearsalRequest;
	private AppearanceRequest appearanceRequest;


	public AddBandWrapper() {
	}

	public AddBandWrapper(Band band, String username) {
		super();
		this.band = band;
		this.username = username;
	}
	
	public AddBandWrapper(Band band, Appointment app) {
		super();
		this.band = band;
		this.appointment = app;
	}
	
    public AddBandWrapper(Band band, RehearsalRequest rehRequest)
    {
    	super();
        this.band = band;
        this.rehearsalRequest = rehRequest;
    }
    
    public AddBandWrapper(Band band, AppearanceRequest appearanceRequest)
    {
    	super();
        this.band = band;
        this.appearanceRequest = appearanceRequest;
    }

	public AppearanceRequest getAppearanceRequest() {
		return appearanceRequest;
	}

	public void setAppearanceRequest(AppearanceRequest appearanceRequest) {
		this.appearanceRequest = appearanceRequest;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Band getBand() {
		return band;
	}

	public void setBand(Band band) {
		this.band = band;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public RehearsalRequest getRehearsalRequest() {
		return rehearsalRequest;
	}

	public void setRehearsalRequest(RehearsalRequest rehearsalRequest) {
		this.rehearsalRequest = rehearsalRequest;
	}
}
