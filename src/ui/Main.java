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
 * Main class of the program
 * 
 * @author A0119381E
 */
public class Main extends Application{
	
	private UIControl mainControl;
	private Stage stage;

	private Log log;
	
	private ArrayList<String> commandHistory;
	private Integer historyPos;
	
	private Controller controller;
	private List<String> displayBuf;
	private Integer recentChange;
	
	
	public Main() {
		controller = ControllerClass.getInstance();
		log = new Log();
		commandHistory = new ArrayList<String>();
		historyPos = 0;
	}
	
	/**
	 * GUI entry of the program (javafx)
	 * To launch call static method launch(args)
	 */
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
	
	/**
	 * Initialize and return the main scene
	 * 
	 * @return Scene to be load into the stage
	 * @throws IOException	
	 * 				when error loading the FXML file
	 */
	private Scene loadScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane mainPane = (Pane) loader.load(getClass().getResourceAsStream(Config.main));
		
		mainControl = loader.getController();
		
		Scene scene = new Scene(mainPane);
		
		mainControl.setOnExecCmd((command) -> onEnter(command));
		mainControl.setOnRequestHistory((direction) -> onUpDown(direction));
		mainControl.setInputOnChange((str) -> onInputChange(str));
		
		execCmd(Config.cmdList);
		
		setHotKeys(scene);
		return scene;
	}
	
	
	/**
	 * Execute command by calling Controller.execCmd
	 * load the list from controller every time called
	 * 
	 * @param cmd
	 */
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
	
	/**
	 * Called when enter is pressed
	 * 
	 * @param command
	 * @return command pass to this function
	 */
	private String onEnter(String command) {
		if (command.isEmpty()) {
			return command;
		}
		
		execCmd(command);
		pushHistory(command);
		return command;
	}
	
	/**
	 * Called when up/down is pressed in input box
	 * 
	 * @param direction
	 * @return history command
	 */
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
	
	/**
	 * Called when input is changed
	 * 
	 * @param newValue
	 * @return suggest word returned by controller
	 */
	private String onInputChange(String newValue) {
		String suggest = controller.suggest(newValue);
		mainControl.showNoti(controller.getFeedback());
		return suggest;
	}
	
	/**
	 * Push a new history entry 
	 * 
	 * @param command
	 */
	private void pushHistory(String command) {
		commandHistory.add(command);
		while (commandHistory.size() > Config.maxNumHistory) {
			commandHistory.remove(0);
		}
		historyPos = commandHistory.size();
	}
	
	/**
	 * Set general hot keys include: page up/down
	 * & Escape to minimize 
	 * 
	 * @param scene
	 */
	private void setHotKeys(Scene scene) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
			if (event.getCode() == KeyCode.PAGE_DOWN || 
					(event.getCode() == KeyCode.RIGHT && event.isAltDown())) {
				execCmd(Config.cmdPageDown);
				event.consume();
			} else if (event.getCode() == KeyCode.PAGE_UP
					|| (event.getCode() == KeyCode.LEFT && event.isAltDown())) {
				execCmd(Config.cmdPageUp);
				event.consume();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				stage.setIconified(true);
			}
		});
	}
	
	/**
	 * Command line entry
	 * Execute command from args, or launch GUI
	 * Any error will cause the program launch the GUI
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				String command = "";
				for (String str : args) {
					command += str;
				}
				Main main = new Main();
				main.controller.execCmd(command);
				System.out.println(main.controller.getFeedback());
				return;
			} catch (Exception e) {
				launch(args);
				return;
			}
		}
		launch(args);
	}
}
