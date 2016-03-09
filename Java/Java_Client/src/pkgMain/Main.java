package pkgMain;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

//"jdbc:oracle:thin:@192.168.128.151:1521:ora11g"
//"jdbc:oracle:thin:@212.152.179.117:1521:ora11g"

public class Main extends Application {
	private ResourceBundle resources;
	
	public static void main(String[] args) {
		launch (args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene (FXMLLoader.load (Thread.currentThread().getContextClassLoader().getResource("pkgGui/Login.fxml"), resources));
		
		primaryStage.setScene (scene);
		
		primaryStage.show();
	}
}
