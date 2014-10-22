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
		super.updateItem(str, empty);
		if (empty) {
			setGraphic(null);
			return;
        }
		ListItem item = new ListItem();
		String[] split = str.split("%");
		if (split.length < 4) {
			item.setDesc(str);
			setGraphic(item.getHBox());
			return;
		}
		Boolean priority = Boolean.parseBoolean(split[0].trim());
		String desc = split[1].trim();
		Long timeStart = null;
		if (!split[2].trim().isEmpty()) {
			timeStart = Long.parseLong(split[2].trim());
		}
		Long timeEnd = null;
		if (!split[3].trim().isEmpty()) {
			timeEnd = Long.parseLong(split[3].trim());
		}
		item.setDesc(desc);
		item.setPriority(priority);
		
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
