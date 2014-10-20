/**
 * 
 */
package ui;

import java.time.LocalDateTime;

import javafx.scene.control.ListCell;

/**
 * @author Luo Shaohuai
 *
 */
public class ListViewCell extends ListCell<String> {
	@Override
	public void updateItem(String str, boolean empty){
		super.updateItem(str, empty);
		if (empty) {
			setGraphic(null);
			return;
        }
		ListItem item = new ListItem();
		String[] split = str.split("%");
		if (split.length < 5) {
			item.setDesc(str);
			setGraphic(item.getHBox());
			return;
		}
		Boolean priority = Boolean.parseBoolean(split[1].trim());
		String desc = split[2].trim();
		Long timeStart = null;
		if (!split[3].trim().isEmpty()) {
			timeStart = Long.parseLong(split[3].trim());
		}
		Long timeEnd = null;
		if (!split[4].trim().isEmpty()) {
			timeEnd = Long.parseLong(split[4].trim());
		}
		item.setDesc(desc);
		
		if (timeStart != null) {
			if (timeEnd != null) {
				item.setTimes(timeStart, timeEnd);
			} else {
				item.setTimes(timeStart);
			}
		}  else {
			item.setTimes();
		}
		
		setGraphic(item.getHBox());
	}
}
