/**
 * 
 */
package ui;

import javafx.scene.control.ListCell;

/**
 * @author Luo Shaohuai
 *
 */
class ListViewCell extends ListCell<String> {
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
			item.setPriority(false);
			item.clearTime();
			setGraphic(item.getHBox());
			return;
		}
		
		String desc = split[0].trim();
		Boolean priority = Boolean.parseBoolean(split[3].trim());
		Long timeStart = null;
		if (!split[1].trim().isEmpty()) {
			timeStart = Long.parseLong(split[1].trim());
		}
		Long timeEnd = null;
		if (!split[2].trim().isEmpty()) {
			timeEnd = Long.parseLong(split[2].trim());
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
