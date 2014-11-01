/**
 * 
 */
package ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

/**
 * @author Luo Shaohuai
 *
 */
public class PopupListControl {
	private ListView<String> list;
	private Pane pane;
	
	public PopupListControl() {
		list = new ListView<String>();
		pane = new Pane();
		pane.getChildren().add(list);
		
		list.setPrefHeight(100.0);
		list.setPrefWidth(578.0);
	}
	
	public Pane getPane() {
		return pane;
	}
	
	public boolean loadList(List<String> strList) {
		if (strList.isEmpty()) {
			return false;
		}
		
		List<String> descList = new ArrayList<String>();
		int len = strList.size();
		if (len > 4) {
			len = 4;
		}
		for (String str : strList.subList(0, len)) {
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
		list.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				value.onEventExec(list.getSelectionModel().getSelectedItem());
				event.consume();
			}
		});
	}
}
