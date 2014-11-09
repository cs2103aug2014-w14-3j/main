/**
 * 
 */
package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

/**
 * @author Luo Shaohuai
 *
 */
public class UIControl extends BorderPane {	
	@FXML
	private Text time;
	
	@FXML
	private ListView<String> list;
	
	@FXML
	private TextField input;
	
	@FXML
	private HBox title;
	
	@FXML
	private Text noti;
	
	private double mouseX;
	private double mouseY;
	
	private String appendOnComplete;
	
	public UIControl() {
		displayCurTime();
	}
	
	public void init() {
		list.setFocusTraversable(false);
		setDraggable(title);
	}
	
	public void loadList(List<String> displayBuf) {
		loadList(displayBuf, 0);
	}
	
	public void loadList(List<String> strList, Integer recentChange) { 
		ObservableList<String> observableList = FXCollections.observableArrayList(strList);
		list.setItems(observableList);
		list.setCellFactory((list) -> {
			return new ListViewCell();
		});
		
		if (strList.isEmpty()) {
			return;
		}
		
		if (recentChange < 0) {
			recentChange = 0;
		}
		if (recentChange >= strList.size()) {
			recentChange = strList.size() - 1;
		}
		
		list.scrollTo(recentChange);
		list.getSelectionModel().select(recentChange);
		input.requestFocus();
	}
	
	public void showNoti(String message) {
		noti.setText(message);
	}
	
	public void setOnExecCmd(OnEvent value) {
		input.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				value.onEventExec(input.getText());
				input.clear();
			}
		});
	}
	
	public void setOnRequestHistory(OnEvent value) {
		input.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.UP) {
				input.setText(value.onEventExec("UP"));
				setInputCaretToEnd();
				event.consume();
			}
			if (event.getCode() == KeyCode.DOWN) {
				input.setText(value.onEventExec("DOWN"));
				setInputCaretToEnd();
				event.consume();
			}
			if (event.getCode() == KeyCode.TAB) {
				input.setText(input.getText() + appendOnComplete);
				setInputCaretToEnd();
				event.consume();
			}
		});
	}
	
	public void setInputOnChange(OnEvent value) {
		input.textProperty().addListener((observable, oldString, newString)->{
			//ensure caret is at the end
			if (input.caretPositionProperty().get() < newString.length() - 1) {
				return;
			}
			
			String suggest = value.onEventExec(newString).trim();
			
			if (suggest.isEmpty()) {
				return;
			}
			
			String[] words = newString.split(" ");
			String originWord = words[words.length - 1];
			appendOnComplete = suggest.substring(suggest.indexOf(originWord) 
									   + originWord.length());
		});
	}
	
	public void setInputOnFocus() {
		input.requestFocus();
	}
	
	private void setInputCaretToEnd() {
		try {
			input.positionCaret(input.getText().length());
		} catch (Exception e) {
			//do nothing
			//if input has no text, exception will be thrown
		}
	}
	
	private void displayCurTime() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(Config.curTimeDateFormat);
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event) -> {
			time.setText(format.format(LocalDateTime.now()));
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	private void setDraggable(Node node) {
	    node.setOnMousePressed((event) -> {
	    	mouseX = event.getSceneX();
	    	mouseY = event.getSceneY();
	    });

	    node.setOnMouseDragged((event) -> {
	    	node.getScene().getWindow().setX(event.getScreenX() - mouseX);
	    	node.getScene().getWindow().setY(event.getScreenY() - mouseY);
	    });
	}
}
