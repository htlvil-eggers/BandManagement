package com.example.stefan.android_client.pkgModel;

public class Location {
	private String name;
	private int id;
	
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

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Location location = (Location) o;

		return id == location.id;

	}

	@Override
	public int hashCode() {
		return id;
	}
}
