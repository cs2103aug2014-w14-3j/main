package ui;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;

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
		text.setFont(Font.font("Tahoma", FontWeight.BOLD,FontPosture.ITALIC, 14));		
		text.setFill(Color.RED);
		
	}
	
	public HBox getBox() {
		return hbox;
	}
}
