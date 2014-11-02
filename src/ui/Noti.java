package ui;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * @author Luo Shaohuai
 *
 */
public class Noti {
	private HBox hbox;
	private Text text;
	
	public Noti() {
		hbox = new HBox();
		text = new Text();
		hbox.getChildren().add(text);
	}
	
	public void setText(String str) {
		text.setText(str);
	}
	
	public HBox getBox() {
		return hbox;
	}
}
