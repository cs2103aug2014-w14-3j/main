/**
 * 
 */
package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import junit.framework.Assert;
import controller.Controller;
import controller.ControllerClass;

/**
 * @author Luo Shaohuai
 *
 */
public class Main extends Application{
	
	public Main() {
		//controller = ControllerClass.getInstance();
		displayBuf = new ArrayList<String>();
		root = new BorderPane();
		log = new Log();
	}
	
	/**
	 * GUI entry
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			log.log("Initializing...");
			primaryStage.initStyle(StageStyle.UNDECORATED);
			
			Scene scene = new Scene(root, Config.width, Config.height);
			root.setStyle(Config.rootStyle);
			
			HBox top = createTop();
			top.setStyle(Config.topStyle);
			root.setTop(top);
			
			HBox bottom = createBottom();
			bottom.setStyle(Config.bottomStyle);
			root.setBottom(bottom);
			
			ListView<String> list = creatCenter(new ArrayList<String>());
			root.setCenter(list);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			log.log("Initialized");
			
			resetStagePosition(primaryStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void resetStagePosition(Stage stage) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2); 
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 6 * 5);
	}
	
	private HBox createTop() {
		HBox top = new HBox();
		top.setPadding(new Insets(15, 15, 15, 15));
		
		Text title = new Text(Config.title);
		title.setStyle(Config.titleStyle);
		top.getChildren().add(title);
		
		return top;
	}
	
	private HBox createBottom() {
		HBox bottom = new HBox();
		bottom.setPadding(new Insets(5, 5, 5, 5));
		
		TextField commandInput = new TextField();
		commandInput.setPrefWidth(590);
		commandInput.setStyle(Config.inputFieldStyle);
		commandInput.setPromptText(Config.inputFieldPlaceholder);
		commandInput.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				onEnter(commandInput.getText());
				commandInput.clear();
			}
		});
		bottom.getChildren().add(commandInput);
		
		return bottom;
	}
	
	private ListView<String> creatCenter(ArrayList<String> data) {
		ListView<String> center = new ListView<String>();
		center.setItems(loadList(data));
		return center;
	}
	
	private ObservableList<String> loadList(ArrayList<String> data) {
		for (int i = 0; i < data.size(); i++) {
			data.set(i, data.get(i).substring(4));
		}
		return FXCollections.observableList(data);
	}
	
	private void onEnter(String command) {
		execCmd(command);
	}
	
	private void execCmd(String cmd) {
		if(cmd.trim().compareToIgnoreCase(Config.cmdExit) == 0) {
			Platform.exit();
			System.exit(0);
		}
		
		try {
			log.log("Command: " + cmd);
			//displayBuf = controller.execCmd(cmd);
			if (displayBuf == null) {
				return;
			}
			assert displayBuf.size() <= Controller.taskPerPage;
			updateDisplay();
		} catch (Exception e) {
			if (Config.onDevelopment) {
				e.printStackTrace();
			}
			log.log(e.getMessage());
		}

	}
	
	private void updateDisplay() {
		ListView<String> list = creatCenter(displayBuf);
		root.setCenter(list);
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
	private BorderPane root;
	private Log log;
}
