/**
 * 
 */
package ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import controller.Controller;
import controller.ControllerClass;

/**
 * @author Luo Shaohuai
 *
 */
public class Main extends Application{
	
	private UIControl mainControl;
	public Main() {
		controller = ControllerClass.getInstance();
		displayBuf = new ArrayList<String>();
		log = new Log();
	}
	
	@Override
	public void start(Stage stage) { 
		try {
			stage.setScene(loadScene());
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
			System.exit(0);
		}
		stage.show();
	}
	
	private Scene loadScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane mainPane = (Pane) loader.load(getClass().getResourceAsStream(Config.main));
		
		mainControl = loader.getController();
		
		Scene scene = new Scene(mainPane);
		scene.getStylesheets().setAll(
	            getClass().getResource("main.css").toExternalForm()
	        );
		
		mainControl.setInputOnEnter((command) -> {
			onEnter(command);
		});
		
		execCmd("list");
		mainControl.loadList(displayBuf);
		return scene;
	}
	
	
	private void execCmd(String cmd) {
		if(cmd.trim().compareToIgnoreCase(Config.cmdExit) == 0) {
			Platform.exit();
			System.exit(0);
		}
		
		try {
			log.log("Command: " + cmd);
			recentChange = controller.execCmd(cmd);
			displayBuf = controller.getCurrentList();
			if (displayBuf == null) {
				return;
			}
			assert displayBuf.size() <= Controller.taskPerPage;
		} catch (Exception e) {
			if (Config.onDevelopment) {
				e.printStackTrace();
			}
			log.log(e.getMessage());
		}

	}
	
	private void onEnter(String command) {
		execCmd(command);
		mainControl.loadList(displayBuf, recentChange);
	}
	
	/**
	 * Command line entry
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	

	private Controller controller;
	private ArrayList<String> displayBuf;
	private Integer recentChange;
	private Log log;
}
