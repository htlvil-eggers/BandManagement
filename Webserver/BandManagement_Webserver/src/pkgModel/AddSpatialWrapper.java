package pkgModel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddSpatialWrapper {
	private Location location;
	private Street street;
	private double x;
	private double x2;
	private double y;
	private double y2;
	
	public AddSpatialWrapper() {
		
	}
	
	public AddSpatialWrapper(Location location, double x, double y) {
		super();
		this.location = location;
		this.x = x;
		this.y = y;
	}
	

	public AddSpatialWrapper(Street street, double x, double x2, double y, double y2) {
		super();
		this.street = street;
		this.x = x;
		this.x2 = x2;
		this.y = y;
		this.y2 = y2;
	}
	
	public AddSpatialWrapper(Street street, double x, double y) {
		super();
		this.street = street;
		this.x = x;
		this.y = y;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Street getStreet() {
		return street;
	}

	public void setStreet(Street street) {
		this.street = street;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}
	
	
	
}
