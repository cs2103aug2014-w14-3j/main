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
 * This class is the controller of and will generate a list item
 * 
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
	
	/**
	 * Get the Node representation of the list item
	 * 
	 * @return HBox
	 */
	public HBox getHBox() {
		return box;
	}
	
	
	/**
	 * Set text to position of description
	 * 
	 * @param text
	 */
	public void setDesc(String text) {
		desc.setText(text);
	}
	
	/**
	 * Set if the item is shown as prioritized
	 * 
	 * @param priority
	 */
	public void setPriority(boolean priority) {
		this.prior.setVisible(priority);
	}
	
	/**
	 * Clear all text in time field
	 */
	public void clearTime() {
		time.getChildren().clear();
		this.overdue.setVisible(false);
	}
	
	/**
	 * Set as no time
	 */
	public void setTimes() {
		clearTime();
		Text floating = new Text(Config.floating);
		floating.setStyle(Config.minorTextStyle);
		time.getChildren().add(floating);
		this.overdue.setVisible(false);
	}
	
	/**
	 * Set with one time
	 * 
	 * @param timeInMilli
	 */
	public void setTimes(Long timeInMilli) {
		clearTime();
		time.getChildren().add(timeToText(timeInMilli));
		this.overdue.setVisible(isOverdue(timeInMilli));
		
	}
	
	/**
	 * Set with two times
	 * 
	 * @param timeStart
	 * @param timeEnd
	 */
	public void setTimes(Long timeStart, Long timeEnd) {
		clearTime();
		time.getChildren().add(timeToText(timeStart));
		
		Text to = new Text();
		to.setText(Config.timeSeparater);
		to.setStyle(Config.minorTextStyle);
		time.getChildren().add(to);
		time.getChildren().add(timeToText(timeEnd));
		this.overdue.setVisible(isOverdue(timeStart));
	}
	
	/**
	 * Convert time(epoch) to Text
	 * 
	 * @param timeInMilli
	 * @return
	 */
	private Text timeToText(Long timeInMilli) {
		Date time = new Date(timeInMilli);
		LocalDateTime timeobj = LocalDateTime.ofInstant(time.toInstant(), 
														ZoneId.systemDefault()
														);
		Text timeText = new Text();
		DateTimeFormatter format = DateTimeFormatter.ofPattern(Config.taskDateFormat);
		timeText.setText(format.format(timeobj));
		timeText.setStyle(Config.timeStyle);
		return timeText;
	}
	
	/**
	 * Check if overdue
	 * 
	 * @param timeInMilli
	 * @return if the task is overdue
	 */
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
