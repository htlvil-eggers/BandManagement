package pkgModel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Location {
	private String name;
	private int id;
	
	public Location() {
		
	}
	
	public Location(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		return name;
	}
}
