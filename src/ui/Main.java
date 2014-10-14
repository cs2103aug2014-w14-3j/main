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
import controller.Controller;
import controller.ControllerClass;

/**
 * @author Luo Shaohuai
 *
 */
public class Main extends Application{
	public static final String initMsg = "         Forget-Me-Not\n"
				+ "-------------------------------";
	public static final String exitMsg = "===============================";
	
	
	public Main() {
		controller = new ControllerClass();
		displayBuf = new ArrayList<String>();
		root = new BorderPane();
	}
	
	/**
	 * GUI entry
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.initStyle(StageStyle.UNDECORATED);
			
			Scene scene = new Scene(root, 600, 400);
			root.setStyle("-fx-background-color: #CCBBAA;");
			
			HBox top = createTop();
			top.setStyle("-fx-background-color: #AABBCC;");
			root.setTop(top);
			
			HBox bottom = createBottom();
			bottom.setStyle("-fx-background-color: #AABBCC;");
			root.setBottom(bottom);
			
			ListView<String> list = creatCenter(new ArrayList<String>());
			root.setCenter(list);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
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
		
		Text title = new Text("Forget-Me-Not");
		title.setStyle("-fx-fill: #FFEEDD;");
		top.getChildren().add(title);
		
		return top;
	}
	
	private HBox createBottom() {
		HBox bottom = new HBox();
		bottom.setPadding(new Insets(5, 5, 5, 5));
		
		TextField commandInput = new TextField();
		commandInput.setPrefWidth(590);
		commandInput.setStyle("-fx-font-size: 16pt;");
		commandInput.setPromptText("Enter command here");
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
		if(cmd.trim().compareTo("exit") == 0) {
			Platform.exit();
			System.exit(0);
		}
		
		try {
			displayBuf = controller.execCmd(cmd);
			updateDisplay();
		} catch (Exception e) {
			e.printStackTrace();
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
		//onInitialize();
		/*
		Controller controller = new ControllerClass();
		while(true) {
			System.out.println("Command: ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String cmd;
			try {
				cmd = in.readLine();
			} catch (IOException e1) {
				continue;
			}
			if(cmd.trim().isEmpty()) {
				break;
			}
			if(cmd.trim().compareToIgnoreCase("launch") == 0) {
				launch(args);
				break;
			}
			try {
				ArrayList<String> output = controller.execCmd(cmd);
				for (String item : output) {
					System.out.println(item.substring(4));
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}*/
		launch(args);
		//onExit();
	}
	
	public static void onInitialize() {
		System.out.println(initMsg);
	}
	
	public static void onExit() {
		System.out.println(exitMsg);
	}

	private Controller controller;
	private ArrayList<String> displayBuf;
	private BorderPane root;
}
