/**
 * 
 */
package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
		DateTimeFormatter format = DateTimeFormatter.ofPattern("LLL d ccc HH:mm:ss");
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event) -> {
			time.setText(format.format(LocalDateTime.now()));
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}
	
	public void loadList(ArrayList<String> strList) {
		ObservableList<String> observableList = FXCollections.observableArrayList(strList);
		list.setItems(observableList);
		list.setCellFactory((list) -> {
			return new ListViewCell();
		});
	}
	
	public void setInputOnEnter(OnEnterEvent value) {
		input.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				value.onEnter(input.getText());
				input.clear();
			}
		});
	}
	
}
