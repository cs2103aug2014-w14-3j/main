/**
 * 
 */
package ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
	
	public void clearTime() {
		time.getChildren().clear();
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
		time.getChildren().add(to);
		
		time.getChildren().add(timeToText(timeEnd));	
	}
	
	private Text timeToText(Long timeInMilli) {
		LocalDateTime timeobj = LocalDateTime.ofEpochSecond(timeInMilli, 0, ZoneOffset.UTC);
		Text timeText = new Text();
		timeText.setText(timeobj.toString());
		return timeText;
	}
	
}