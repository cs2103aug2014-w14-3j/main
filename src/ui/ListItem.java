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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Luo Shaohuai
 *
 */
public class ListItem{
	@FXML
	private HBox box;
	
	@FXML
	private Text desc; 
	
	@FXML
	private TextFlow time;
	
	@FXML
	private Label prior;
	
	@FXML
	private Label overdue;
	
	public ListItem() {
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
		this.prior.setVisible(priority);
	}
	
	public void clearTime() {
		time.getChildren().clear();
		this.overdue.setVisible(false);
	}
	
	public void setTimes() {
		clearTime();
		Text floating = new Text("Floating");
		floating.setStyle("-fx-fill: #777777;");
		time.getChildren().add(floating);
		this.overdue.setVisible(false);
	}
	
	public void setTimes(Long timeInMilli) {
		clearTime();
		time.getChildren().add(timeToText(timeInMilli));
		this.overdue.setVisible(isOverdue(timeInMilli));
		
	}
	
	public void setTimes(Long timeStart, Long timeEnd) {
		clearTime();
		time.getChildren().add(timeToText(timeStart));
		
		Text to = new Text();
		to.setText(" to ");
		to.setStyle("-fx-fill: #777777");
		time.getChildren().add(to);
		time.getChildren().add(timeToText(timeEnd));
		this.overdue.setVisible(isOverdue(timeStart));
	}
	
	private Text timeToText(Long timeInMilli) {
		Date time = new Date(timeInMilli);
		LocalDateTime timeobj = LocalDateTime.ofInstant(time.toInstant(), 
														ZoneId.systemDefault()
														);
		Text timeText = new Text();
		DateTimeFormatter format = DateTimeFormatter.ofPattern(Config.taskDateFormat);
		timeText.setText(format.format(timeobj));
		timeText.setStyle("-fx-fill: #222222;");
		return timeText;
	}
	
	private boolean isOverdue(Long timeInMilli) {
		LocalDateTime time = LocalDateTime.ofInstant(
								new Date(timeInMilli).toInstant(), 
								ZoneId.systemDefault()
								);
		
		if (LocalDateTime.now().isAfter(time)) {
			return true;
		}

		return false;
	}
	
}
