package pkgMain;

import java.sql.SQLException;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import pkgModel.OracleDatabaseManager;

//"jdbc:oracle:thin:@192.168.128.151:1521:ora11g"
//"jdbc:oracle:thin:@212.152.179.117:1521:ora11g"

public class Main extends Application {
	private OracleDatabaseManager databaseManager;
	private ResourceBundle resources;
	private final String CONNECTION_STRING = "jdbc:oracle:thin:@192.168.128.151:1521:ora11g";
	private final String USERNAME = "d5bhifs25";
	private final String PASSWORD = "edelBlech";
	
	public static void main(String[] args) {
		launch (args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			databaseManager = new OracleDatabaseManager (CONNECTION_STRING, USERNAME, PASSWORD);
		
			resources = new ListResourceBundle() { 
				protected Object[][] getContents() {
					return new Object[][] {
						{"databaseManager", databaseManager}
					};
				}
			};
			
			Scene scene = new Scene (FXMLLoader.load (Thread.currentThread().getContextClassLoader().getResource("pkgGui/Login.fxml"), resources));
			
			primaryStage.setScene (scene);
			
			primaryStage.show();
		} catch (SQLException e) {
			Alert messageWindow = new Alert (Alert.AlertType.ERROR);
			
			messageWindow.setContentText(e.getMessage());
			
			messageWindow.show();
		}
	}
	
	@Override
	public void stop() throws Exception {
		if (databaseManager != null) {
			databaseManager.close();
		}
	}
}
