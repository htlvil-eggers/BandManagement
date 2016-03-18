package com.example.stefan.android_client.pkgModel;

import android.util.Log;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class WebserviceManager {
	private static WebResource service;

	public WebserviceManager () {
		
	}
	
	private static void initializeService(String url) {
		ClientConfig clientConfig = new DefaultClientConfig();

		clientConfig.getClasses().add(GsonMessageBodyHandler.class);

		Client client = Client.create (clientConfig);
		
		service = client.resource(url);
	}

	
	public static boolean checkCredentials(String username, String password, String bandname) {
		initializeService(getBaseURI().toString());
        String correct = service.path("rest").path("login").path(bandname).path(username).path(password).accept(MediaType.APPLICATION_JSON).get(String.class);
		
		return Boolean.parseBoolean(correct);
	}
	
	public static Musician getMusician(String username, String password) {
		initializeService(getBaseURI().toString());

		return service.path("rest").path("musicians").path(username).path(password).accept(MediaType.APPLICATION_JSON).get(Musician.class);
	}
	
    private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://10.0.0.24:8080/BandManagement_Webserver").build();
    }

	public static Location[] getLocations() {
		return service.path("rest").path("locations").accept(MediaType.APPLICATION_JSON).get(Location[].class);
	}

	public static Instrument[] getInstruments() {
		return service.path("rest").path("instruments").accept(MediaType.APPLICATION_JSON).get(Instrument[].class);
	}

	public static RehearsalRequest[] getRehearsalRequests(String bandname) {
		return service.path("rest").path("rehearsalRequests").path(bandname).accept(MediaType.APPLICATION_JSON).get(RehearsalRequest[].class);
	}

	public static Appointment[] getUnansweredAppearanceRequests(String username, String bandname, String password) {
		return service.path("rest").path("appearanceRequests").path(bandname).path(username).path(password).accept(MediaType.APPLICATION_JSON).get(Appointment[].class);
	}

	public static void updateMusician(Musician musician) {
		service.path("rest").path("musicians").accept(MediaType.APPLICATION_JSON ).put(musician);
		
	}
	
	public static void addAvailableTime(String bandname, String username, Date from, Date to) {
		service.path("rest").path("availableTimes").accept(MediaType.APPLICATION_JSON).post(new AvailableTimeWrapper(bandname, username, from, to));
	}

	public static void answerAppearanceRequest(Appointment selectedAppearance, String bandname, String username,
			String password, boolean accepted) {
		service.path("rest").path("appearanceRequests").accept(MediaType.APPLICATION_JSON).put(new AppearanceRequestWrapper(selectedAppearance, bandname, username, password, accepted));
	}
}
