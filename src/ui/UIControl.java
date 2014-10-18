/**
 * 
 */
package ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

/**
 * @author Luo Shaohuai
 *
 */
public class UIControl extends BorderPane {
	@FXML
	private Text time;
	
	@FXML
	private ListView list;
	
	public UIControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
	}
	
	public void loadList(ArrayList<ListItemControl> list) {
		
	}
	
}
