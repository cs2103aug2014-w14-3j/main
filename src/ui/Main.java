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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	private Stage stage;
	public Main() {
		controller = ControllerClass.getInstance();
		log = new Log();
		commandHistory = new ArrayList<String>();
		historyPos = 0;
	}
	
	@Override
	public void start(Stage stage) { 
		try {
			this.stage = stage;
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(loadScene());
			mainControl.init();
		} catch (IOException e) {
			if (Config.onDevelopment) {
				e.printStackTrace();
			}
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
		
		mainControl.setOnExecCmd((command) -> onEnter(command));
		mainControl.setOnRequestHistory((direction) -> onUpDown(direction));
		mainControl.setInputOnChange((str) -> onInputChange(str));
		
		execCmd("list");
		
		setHotKeys(scene);
		return scene;
	}
	
	
	private void execCmd(String cmd) {
		try {
			log.log("Command: " + cmd);
			
			recentChange = controller.execCmd(cmd);
			
			if(controller.isExiting()) {
				log.log("on exit");
				Platform.exit();
				System.exit(0);
			}
			
			displayBuf = controller.getCurrentList();
			if (displayBuf == null) {
				return;
			}
			mainControl.loadList(displayBuf, recentChange);
			mainControl.showNoti(controller.getFeedback());
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
	
	private String onInputChange(String newValue) {
		String suggest = controller.suggest(newValue);
		mainControl.showNoti(controller.getFeedback());
		return suggest;
	}
	
	private void pushHistory(String command) {
		commandHistory.add(command);
		historyPos = commandHistory.size();
	}
	
	private void setHotKeys(Scene scene) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
			if (event.getCode() == KeyCode.PAGE_DOWN || 
					(event.getCode() == KeyCode.RIGHT && event.isAltDown())) {
				execCmd("page down");
				event.consume();
			} else if (event.getCode() == KeyCode.PAGE_UP
					|| (event.getCode() == KeyCode.LEFT && event.isAltDown())) {
				execCmd("page up");
				event.consume();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				stage.setIconified(true);
			}
		});
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
