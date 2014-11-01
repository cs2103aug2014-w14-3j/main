/**
 * 
 */
package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

/**
 * @author Luo Shaohuai
 *
 */
public class PopupListControl {
	@FXML
	private ListView<String> list;
	
	@FXML
	private Pane pane;
	
	public PopupListControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PopupList.fxml"));
		fxmlLoader.setController(this);
		
		try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
	}
	
	public Pane getPane() {
		return pane;
	}
	
	public boolean loadList(List<String> strList) {
		if (strList.isEmpty()) {
			return false;
		}
		
		List<String> descList = new ArrayList<String>();
		for (String str : strList) {
			String[] split = str.split("%");
			if (split.length < 4) {
				descList.add(str.trim());
				continue;
			}
			
			descList.add(split[0].trim());
		}
		
		ObservableList<String> observableList = FXCollections.observableArrayList(descList);
		list.setItems(observableList);
		return true;
	}
	
	public void setOnEnter(OnEvent value) {
		list.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				value.onEventExec(list.getSelectionModel().getSelectedItem());
				event.consume();
			}
		});
	}
	
	public void setOnEsc(OnEvent value) {
		list.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				value.onEventExec(list.getSelectionModel().getSelectedItem());
				event.consume();
			}
		});
	}
}
