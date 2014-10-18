/**
 * 
 */
package ui;

import javafx.scene.control.ListCell;

/**
 * @author Luo Shaohuai
 *
 */
public class ListViewCell extends ListCell<String> {
	@Override
	public void updateItem(String str, boolean empty){
		ListItem item = new ListItem();
		item.setDesc(str);
		setGraphic(item.getHBox());
	}
}
