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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Luo Shaohuai
 *
 */
public class ListItem{
	@FXML
	private HBox hbox;
	
	@FXML
	private Text desc; 
	
	@FXML
	private TextFlow time;
	
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
		return hbox;
	}
	
	public void setDesc(String text) {
		desc.setText(text);
	}
	
	public void setPriority(boolean priority) {
		if (priority) {
			hbox.setStyle("-fx-background-color: #F89406");
		} else {
			hbox.setStyle("-fx-background-color: #EEEEEE");
		}
	}
	
	public void clearTime() {
		time.getChildren().clear();
	}
	
	public void setTimes() {
		clearTime();
		time.getChildren().add(new Text("Floating"));
	}
	
	public void setTimes(Long timeInMilli) {
		clearTime();
		
		time.getChildren().add(timeToText(timeInMilli));
	}
	
	public void setTimes(Long timeStart, Long timeEnd) {
		clearTime();
		time.getChildren().add(timeToText(timeStart));
		
		Text to = new Text();
		to.setText("\n");
		time.getChildren().add(to);
		time.getChildren().add(timeToText(timeEnd));	
	}
	
	private Text timeToText(Long timeInMilli) {
		Date time = new Date(timeInMilli);
		LocalDateTime timeobj = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
		Text timeText = new Text();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a");
		timeText.setText(format.format(timeobj));
		return timeText;
	}
	
}
