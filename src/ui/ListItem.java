/**
 * 
 */
package ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Luo Shaohuai
 *
 */
class ListItem{
	@FXML
	private HBox box;
	
	@FXML
	private Text desc; 
	
	@FXML
	private TextFlow time;
	
	@FXML
	private Circle priority;
	
	ListItem() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListItem.fxml"));
		fxmlLoader.setController(this);
		
		try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
		
		time.getChildren().clear();
		desc.setText("");
	}
	
	public HBox getHBox() {
		return box;
	}
	
	public void setDesc(String text) {
		desc.setText(text);
	}
	
	public void setPriority(boolean priority) {
		this.priority.setVisible(priority);
	}
	
	public void clearTime() {
		time.getChildren().clear();
	}
	
	public void setTimes() {
		clearTime();
		Text floating = new Text("Floating");
		floating.setStyle("-fx-fill: #777777;");
		time.getChildren().add(floating);
	}
	
	public void setTimes(Long timeInMilli) {
		clearTime();
		time.getChildren().add(timeToText(timeInMilli));
	}
	
	public void setTimes(Long timeStart, Long timeEnd) {
		clearTime();
		time.getChildren().add(timeToText(timeStart));
		
		Text to = new Text();
		to.setText(" to ");
		to.setStyle("-fx-fill: #777777");
		time.getChildren().add(to);
		time.getChildren().add(timeToText(timeEnd));	
	}
	
	private Text timeToText(Long timeInMilli) {
		Date time = new Date(timeInMilli);
		LocalDateTime timeobj = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
		Text timeText = new Text();
		DateTimeFormatter format = DateTimeFormatter.ofPattern(Config.taskDateFormat);
		timeText.setText(format.format(timeobj));
		timeText.setStyle("-fx-fill: #222222;");
		return timeText;
	}
	
}
