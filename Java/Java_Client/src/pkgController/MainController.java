package pkgController;

import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import netscape.javascript.JSObject;

import pkgModel.OracleDatabaseManager;
import pkgModel.RehearsalRequest;
import pkgModel.Appearance;
import pkgModel.Appointment;
import pkgModel.Instrument;
import pkgModel.Location;
import pkgModel.Musician;

public class MainController {
	private OracleDatabaseManager databaseManager;
	private String username;
	private String bandname;
	
	@FXML
	private ResourceBundle resources;
	
    @FXML
    private Button btnAccept;
    @FXML
    private Button btnSave;
    @FXML
    private DatePicker dateBirthdate;
    @FXML
    private ComboBox<Location> cboxHabitation;
    @FXML
    private ListView<Appearance> lstAppearances;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private TextArea txtareaDetail;
    @FXML
    private ListView<RehearsalRequest> lstRehearsalRequests;
    @FXML
    private TextField txtLastName;
    @FXML
    private ListView<Instrument> lstInstruments;
    @FXML
    private TextField txtFirstName;
    @FXML
    private Button btnDecline;
    @FXML
    private WebView wviewCalendar;
    @FXML
    private Button btnAcceptAppointment;
    @FXML
    private Button btnDeclineAppointment;
    @FXML
    private ListView<Appointment> lstAppointments;
    @FXML
    private TextArea txtareaAppointmentDetail;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    
	@FXML
	private void initialize() {
		databaseManager = (OracleDatabaseManager) resources.getObject ("databaseManager");
		username = (String) resources.getObject("username");
		bandname = (String) resources.getObject("bandname");
		
		configureComponents();
		
		try {
			initializeComponents();
		} catch (SQLException e) {
			printAlertMessage(e.getMessage());
		}
	}
	
	private void initializeComponents() throws SQLException {
		initializeComponentsPersonalDataTab();
		initializeComponentsRehearsalRequestsTab();
		initializeComponentsAppearanceRequestsTab();
		initializeComponentsOtherAppointmentRequestsTab();
	}
	
	private void configureComponents() {
		configureComponentsPersonalDataTab();
		configureComponentsRehearsalRequestsTab();
		configureComponentsAppearanceRequestsTab();
		configureComponentsOtherAppointmentRequestsTab();
	}
	
	private void configureComponentsOtherAppointmentRequestsTab() {
		lstAppointments.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Appointment>() {
			public void changed (ObservableValue <? extends Appointment> observable, Appointment oldValue, Appointment newValue) {
				updateTextareaFromAppointment (newValue, txtareaAppointmentDetail);
			}
		});
	}
	
	private void configureComponentsAppearanceRequestsTab() {
		lstAppearances.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Appearance>() {
			public void changed (ObservableValue <? extends Appearance> observable, Appearance oldValue, Appearance newValue) {
				updateTextareaFromAppointment (newValue, txtareaDetail);
			}
		});
	}

	private void configureComponentsRehearsalRequestsTab() {
		lstRehearsalRequests.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RehearsalRequest>() {
			public void changed (ObservableValue <? extends RehearsalRequest> observable, RehearsalRequest oldValue, RehearsalRequest newValue) {
				updateCalendarFromRehearsalRequest (newValue);
			}

			private void updateCalendarFromRehearsalRequest(RehearsalRequest rehearsalRequest) {
				String dateFrom, dateTo;
				Date startTime = rehearsalRequest.getStartTime(), endTime = rehearsalRequest.getEndTime();
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(startTime);
				dateFrom = "new Date (" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH) + "," + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.SECOND) + "," + calendar.get(Calendar.MILLISECOND) + ")";
				calendar.setTime(endTime);
				dateTo = "new Date (" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH) + "," + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.SECOND) + "," + calendar.get(Calendar.MILLISECOND) + ")";
			
				wviewCalendar.getEngine().executeScript("generateCalendar(" + dateFrom + "," + dateTo + ");");
			}
		});
	}

	private void configureComponentsPersonalDataTab() {
		lstInstruments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void initializeComponentsAppearanceRequestsTab() {
		try {
			lstAppearances.setItems(FXCollections.observableArrayList(databaseManager.getUnansweredAppearanceRequests(username, bandname)));
		} catch (SQLException e) {
			printAlertMessage(e.getMessage());
		}
	}
	
	private void updateTextareaFromAppointment (Appointment appointment, TextArea textArea) {
		String newLine = System.getProperty("line.separator");
		String text = "";
		
		text += "Titel: " + appointment.getName() + newLine + newLine;
		text += "Start time: " + appointment.getStartTime() + newLine;
		text += "End time: " + appointment.getEndTime() + newLine + newLine;
		text += "Location: " + appointment.getLocation().getName() + newLine + newLine;
		text += "Description: " + appointment.getDescription();
	
		textArea.setText(text);
	}
	
	private void initializeComponentsRehearsalRequestsTab() throws SQLException {
		this.wviewCalendar.getEngine().load(Thread.currentThread().getContextClassLoader().getResource("pkgResources/Site.html").toExternalForm());
	
		lstRehearsalRequests.setItems(FXCollections.observableArrayList(databaseManager.getRehearsalRequests (bandname)));
	}
    
    private void initializeComponentsPersonalDataTab() throws SQLException {
    	Musician musician = databaseManager.getMusician (username);
    	
		cboxHabitation.setItems (FXCollections.observableArrayList (databaseManager.getLocations()));
		lstInstruments.setItems (FXCollections.observableArrayList(databaseManager.getInstruments()));
		
		pwdPassword.setText(musician.getPassword());
		txtFirstName.setText(musician.getFirstName());
		txtLastName.setText(musician.getLastName());
		cboxHabitation.setValue (musician.getHabitation());
		
		if (musician.getBirthdate() != null) {
			dateBirthdate.setValue(musician.getBirthdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		}
		
		for (Instrument instrument : musician.getInstruments()) {
			lstInstruments.getSelectionModel().select(instrument);
		}
    }
    
	private void initializeComponentsOtherAppointmentRequestsTab() {
		try {
			lstAppointments.setItems(FXCollections.observableArrayList(databaseManager.getUnansweredAppointmentRequests(username, bandname)));
		} catch (SQLException e) {
			printAlertMessage(e.getMessage());
		}
	}

    @FXML
    void btnSave_onAction() {
    	String password = pwdPassword.getText();
    	String firstName = txtFirstName.getText();
    	String lastName = txtLastName.getText();
    	Date birthdate = null;
    	Location habitation = cboxHabitation.getValue();
    	Vector<Instrument> instruments = new Vector<Instrument> (Arrays.asList (lstInstruments.getSelectionModel().getSelectedItems().toArray(new Instrument[0])));
    	
    	if (dateBirthdate.getValue() != null) {
    		birthdate = Date.from(dateBirthdate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    	}
    	
    	Musician musician = new Musician (username, password, firstName, lastName, instruments, habitation, birthdate);
    	
    	try {
			databaseManager.updateMusician (musician);
		} catch (SQLException e) {
			printAlertMessage(e.getMessage());
		}
    }

    @FXML
    void btnAccept_onAction() {
    	Appearance selectedAppearance = lstAppearances.getSelectionModel().getSelectedItem();
    	
    	if (selectedAppearance != null) {
    		try {
				databaseManager.answerAppearanceRequest (selectedAppearance, bandname, username, true);
			} catch (SQLException e) {
				printAlertMessage(e.getMessage());
			}
    	}
    	else {
			printAlertMessage("Keinen Auftritt ausgewählt!");
    	}
    }

    @FXML
    void btnDecline_onAction() {
    	Appearance selectedAppearance = lstAppearances.getSelectionModel().getSelectedItem();
    	
    	if (selectedAppearance != null) {
    		try {
				databaseManager.answerAppearanceRequest (selectedAppearance, bandname, username, false);
			} catch (SQLException e) {
				printAlertMessage(e.getMessage());
			}
    	}
    	else {
			printAlertMessage("Keinen Auftritt ausgewählt!");
    	}
    }
    
    @FXML
    private void btnAcceptAppointment_onAction() {
    	Appointment selectedAppointment = lstAppointments.getSelectionModel().getSelectedItem();
    	
    	if (selectedAppointment != null) {
    		try {
				databaseManager.answerAppointmentRequest (selectedAppointment, bandname, username, true);
			} catch (SQLException e) {
				printAlertMessage(e.getMessage());
			}
    	}
    	else {
			printAlertMessage("No appointment selected!");
    	}
    }
    
    @FXML
    private void btnDeclineAppointment_onAction() {
    	Appointment selectedAppointment = lstAppointments.getSelectionModel().getSelectedItem();
    	
    	if (selectedAppointment != null) {
    		try {
				databaseManager.answerAppointmentRequest (selectedAppointment, bandname, username, false);
			} catch (SQLException e) {
				printAlertMessage(e.getMessage());
			}
    	}
    	else {
			printAlertMessage("No appointment selected!");
    	}
    }
    
    @FXML
    private void btnSavePossibleTimes_onAction() {
    	JSObject jsObject = (JSObject)wviewCalendar.getEngine().executeScript("getSelectedDates();");
    	Vector<Date> selectedDates = new Vector<Date>();
    	int i = 0;
    	
    	while (!jsObject.getSlot(i).equals("undefined")) {
    		selectedDates.add(new Date(((Double)((JSObject)jsObject.getSlot(i)).call("getTime")).longValue()));
    		i++;
    	}
    	
    	Date []dates;
    	for (Date date : selectedDates) {
    		try {
    			dates = generateAvailableTimesFromDateAndTimeFields(date);
    			
        		try {
    				databaseManager.addAvailableTimes (bandname, username, dates[0], dates[1]);
    			} catch (SQLException e) {
    				printAlertMessage(e.getMessage());
    				
    				e.printStackTrace();
    			}
    		}
    		catch (Exception e) {
    			printAlertMessage("Please input both times in correct format: (hh:mm)");
    		}
    	}
    }
    
    private Date[] generateAvailableTimesFromDateAndTimeFields (Date date) {
    	Date []dates = new Date[2];
    	Calendar calendar = Calendar.getInstance();
    	int hours, minutes;
    	String timeFrom = txtFrom.getText();
    	String timeTo = txtTo.getText();
    	String []timeData;
    	
    	calendar.setTime(date);
    	timeData = timeFrom.split(":");
    	hours = Integer.parseInt(timeData[0]);
    	minutes = Integer.parseInt(timeData[1]);
    	calendar.add(Calendar.HOUR_OF_DAY, hours);
    	calendar.add(Calendar.MINUTE, minutes);
    	dates[0] = calendar.getTime();
    	
    	calendar.setTime(date);
    	timeData = timeTo.split(":");
    	hours = Integer.parseInt(timeData[0]);
    	minutes = Integer.parseInt(timeData[1]);
    	calendar.add(Calendar.HOUR_OF_DAY, hours);
    	calendar.add(Calendar.MINUTE, minutes);
    	dates[1] = calendar.getTime();
    	
    	return dates;
    }
    
    private void printAlertMessage (String message) {
		Alert messageWindow = new Alert (Alert.AlertType.ERROR);
		
		messageWindow.setContentText(message);
		
		messageWindow.show();
    }
}
