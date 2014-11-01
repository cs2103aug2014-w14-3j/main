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
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.Controller;
import controller.ControllerClass;

/**
 * @author Luo Shaohuai
 *
 */
public class Main extends Application{
	private Popup noti;
	private Popup suggest;
	private Stage stage;
	private UIControl mainControl;
	public Main() {
		controller = ControllerClass.getInstance();
		log = new Log();
		commandHistory = new ArrayList<String>();
		noti = new Popup();	
		suggest = new Popup();
		historyPos = 0;
	}
	
	@Override
	public void start(Stage stage) { 
		try {
			this.stage = stage;
			//stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(loadScene());
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
			noti.hide();
			
			recentChange = controller.execCmd(cmd);
			displayBuf = controller.getCurrentList();
			if (displayBuf == null) {
				return;
			}
		} catch (Exception e) {
			if (Config.onDevelopment) {
				e.printStackTrace();
			}
			Text text = new Text(e.getMessage());
			noti.getContent().clear();
			noti.getContent().add(text);
			noti.setX(stage.getX() + mainControl.getInputPosition().getX());
			noti.setY(stage.getY() + mainControl.getInputPosition().getY());
			noti.show(stage);
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
	
	private String onInputChange(String newValue) {
		PopupListControl list = new PopupListControl();
		suggest.getContent().clear();
		suggest.getContent().add(list.getPane());
		suggest.setX(stage.getX() + mainControl.getInputPosition().getX());
		suggest.setY(stage.getY() + stage.getHeight() - 5);
		list.setOnEnter((str) -> onPopupListEnter(str));
		list.setOnEsc((str)->onPopupListEsc(str));
		
		if (list.loadList(controller.suggest(newValue))) {
			suggest.show(stage);
		} else {
			suggest.hide();
		}
		
		return newValue;
	}
	
	private String onPopupListEnter(String str) {
		if (str == null || str.isEmpty()) {
			mainControl.setInputOnFocus();
		} else {
			mainControl.setInputText(str);
		}
		return str;
	}
	
	private String onPopupListEsc(String str) {
		suggest.hide();
		mainControl.setInputOnFocus();
		return str;
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
