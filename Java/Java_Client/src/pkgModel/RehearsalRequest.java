package pkgModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RehearsalRequest {
	private Date startTime;
	private Date endTime;
	private double duration;
	
	public RehearsalRequest(Date startTime, Date endTime, double duration) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
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

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		
		return simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime);
	}
}
