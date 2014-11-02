/**
 * 
 */
package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
		log = new Log();
		commandHistory = new ArrayList<String>();
		historyPos = 0;
	}
	
	@Override
	public void start(Stage stage) { 
		try {
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(loadScene());
			mainControl.init();
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
			System.exit(0);
		}
		
		stage.show();
		log.log("Initialized");
	}
	
	private Scene loadScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane mainPane = (Pane) loader.load(getClass().getResourceAsStream(Config.main));
		
		mainControl = loader.getController();
		
		Scene scene = new Scene(mainPane);
		
		mainControl.setInputOnEnter((command) -> onEnter(command));
		mainControl.setInputOnKeyUPDown((direction) -> onUpDown(direction));
		mainControl.setInputOnChange((str) -> onInputChange(str));
		
		execCmd("list");
		mainControl.loadList(displayBuf);
		
		return scene;
	}
	
	
	private void execCmd(String cmd) {
		if(cmd.trim().compareToIgnoreCase(Config.cmdExit) == 0) {
			log.log("on exit");
			Platform.exit();
			System.exit(0);
		}
		
		try {
			log.log("Command: " + cmd);
			mainControl.hideNoti();
			
			recentChange = controller.execCmd(cmd);
			displayBuf = controller.getCurrentList();
			if (displayBuf == null) {
				return;
			}
		} catch (Exception e) {
			if (Config.onDevelopment) {
				e.printStackTrace();
			}
			mainControl.showNoti(e.getMessage());
			
			log.log(e.getMessage());
		}

	}
	
	private String onEnter(String command) {
		if (command.isEmpty()) {
			return command;
		}
		
		execCmd(command);
		pushHistory(command);
		mainControl.loadList(displayBuf, recentChange);
		return command;
	}
	
	private String onUpDown(String direction) {
		if (historyPos > 0 && direction.trim().equalsIgnoreCase("UP")) {
			historyPos--;
		} else if (historyPos < commandHistory.size() && direction.trim().equalsIgnoreCase("DOWN")) {
			historyPos++;
		}
		
		if (historyPos >= 0 && historyPos < commandHistory.size()) {
			return commandHistory.get(historyPos);
		}
		return null;
	}
	
	private List<String> onInputChange(String newValue) {
		return controller.suggest(newValue);
	}
	
	private void pushHistory(String command) {
		commandHistory.add(command);
		historyPos = commandHistory.size();
	}
	
	/**
	 * Command line entry
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	private ArrayList<String> commandHistory;
	private Integer historyPos;
	
	private Controller controller;
	private List<String> displayBuf;
	private Integer recentChange;
	private Log log;
}
