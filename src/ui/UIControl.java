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
import javafx.stage.Popup;
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
	
	private Popup noti;
	private Popup suggest;
	PopupList popupList;
	Noti notiBox;
	
	private double mouseX;
	private double mouseY;
	
	public UIControl() {
		displayCurTime();
		noti = new Popup();	
		notiBox = new Noti();
		suggest = new Popup();
		popupList = new PopupList();
		suggest.getContent().add(popupList.getPane());
		noti.getContent().add(notiBox.getBox());
	}
	
	public void init() {
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
	
	public void hideNoti() {
		noti.hide();
	}
	
	public void showNoti(String message) {
		notiBox.setText(message);
		noti.setHeight(20.0);
		
		double posX = input.localToScene(0.0, 0.0).getX() 
				+ input.getScene().getWindow().getX();
		double posY = input.localToScene(0.0, 0.0).getY() 
				+ input.getScene().getWindow().getY()
				- noti.getHeight();
		
		noti.show(input, posX, posY);
	}
	
	public void setInputOnEnter(OnEvent value) {
		input.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				value.onEventExec(input.getText());
				input.clear();
			}
		});
	}
	
	public void setInputOnKeyUPDown(OnEvent value) {
		input.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.UP) {
				input.setText(value.onEventExec("UP"));
				try {
					input.positionCaret(input.getText().length());
				} catch (Exception e) {
					//do nothing
					//if input has no text, exception will be thrown
				}
				event.consume();
			}
			if (event.getCode() == KeyCode.DOWN) {
				input.setText(value.onEventExec("DOWN"));
				try {
					input.positionCaret(input.getText().length());
				} catch (Exception e) {
					//do nothing
					//if input has no text, exception will be thrown
				}
				event.consume();
			}
		});
	}
	
	public void setInputOnChange(ListGetter value) {
		input.textProperty().addListener((observable, oldString, newString)->{
			double posX = input.localToScene(0.0, 0.0).getX() 
					+ input.getScene().getWindow().getX();
			double posY = input.getScene().getWindow().getY()
					+ input.getScene().getWindow().getHeight();
			
			if (popupList.loadList(value.getList(newString))) {
				suggest.show(input, posX, posY);
			} else {
				suggest.hide();
			}
			
		});
	}
	
	public void setInputText(String str) {
		input.setText(str);
		input.requestFocus();
		try {
			input.positionCaret(input.getText().length());
		} catch (Exception e) {
			//do nothing
		}
	}
	
	public void setInputOnFocus() {
		input.requestFocus();
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
