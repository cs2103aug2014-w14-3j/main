/**
 * 
 */
package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;

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
		
	public UIControl() {
		displayCurTime();
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
	}
	
	public Point2D getInputPosition() {
		return input.localToScene(0.0, 0.0);
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
			}
			if (event.getCode() == KeyCode.DOWN) {
				input.setText(value.onEventExec("DOWN"));
			}
		});
	}
	
	private void displayCurTime() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss a");
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event) -> {
			time.setText(format.format(LocalDateTime.now()));
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
}
