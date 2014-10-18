/**
 * 
 */
package ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private ListView<String> list;
	/*
	public UIControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
		fxmlLoader.setController(this);
		try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
	}*/
	
	public void loadList(ArrayList<String> strList) {
		ObservableList<String> observableList = FXCollections.observableArrayList(strList);
		list.setItems(observableList);
		list.setCellFactory((list) -> {
			return new ListViewCell();
		});
	}
	
	
	
}
