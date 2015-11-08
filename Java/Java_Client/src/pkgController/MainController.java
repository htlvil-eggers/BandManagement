package pkgController;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
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
import javafx.collections.FXCollections;
import pkgModel.OracleDatabaseManager;
import pkgModel.Instrument;
import pkgModel.Location;
import pkgModel.Musician;

public class MainController {
	private OracleDatabaseManager databaseManager;
	private String username;
	
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
    private ListView<?> lstAppearances;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private TextArea txtareaDetail;
    @FXML
    private ListView<?> lstRehearsalRequests;
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
	private void initialize() {
		databaseManager = (OracleDatabaseManager) resources.getObject ("databaseManager");
		username = (String) resources.getObject("username");
		
		configureComponents();
		
		try {
			initializeComponentsPersonalDataTab();
			initializeComponentsRehearsalRequestsTab();
		} catch (SQLException e) {
			Alert messageWindow = new Alert (Alert.AlertType.ERROR);
			
			messageWindow.setContentText(e.getMessage());
			
			messageWindow.show();
			e.printStackTrace();
		}
	}
	
	private void configureComponents() {
		lstInstruments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	private void initializeComponentsRehearsalRequestsTab() {
		this.wviewCalendar.getEngine().load(Thread.currentThread().getContextClassLoader().getResource("pkgResources/Site.html").toExternalForm());
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
			dateBirthdate.setValue(musician.getBirthdate().toLocalDate());
		}
		
		for (Instrument instrument : musician.getInstruments()) {
			lstInstruments.getSelectionModel().select(instrument);
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
    		birthdate = Date.valueOf (dateBirthdate.getValue());
    	}
    	
    	Musician musician = new Musician (username, password, firstName, lastName, instruments, habitation, birthdate);
    	
    	try {
			databaseManager.updateMusician (musician);
		} catch (SQLException e) {
			Alert messageWindow = new Alert (Alert.AlertType.ERROR);
			
			messageWindow.setContentText(e.getMessage());
			
			messageWindow.show();
		}
    }

    @FXML
    void btnAccept_onAction() {

    }

    @FXML
    void btnDecline_onAction() {

    }
}
