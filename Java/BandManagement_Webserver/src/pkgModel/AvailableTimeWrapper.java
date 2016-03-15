package pkgModel;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AvailableTimeWrapper {
	private String bandname;
	private String username;
	private Date from;
	private Date to;
	
	public AvailableTimeWrapper() {
	}

	public AvailableTimeWrapper(String bandname, String username, Date from, Date to) {
		this.bandname = bandname;
		this.username = username;
		this.from = from;
		this.to = to;
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

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}
	
	
}

