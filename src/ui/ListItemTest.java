/**
 * 
 */
package ui;

import java.time.Instant;


import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Luo Shaohuai
 *
 */
public class ListItemTest{
	private ListItem list;
	
	public ListItemTest() {
		list = new ListItem();
	}
	
	@Test
	public void testDesc() {
		list.setDesc("abc");
		HBox hbox = list.getHBox();
		
		Object firstChild = hbox.getChildren().get(0);
		Assert.assertTrue("Wrong description position", firstChild instanceof Text);
		Assert.assertTrue("Wrong description", ((Text) firstChild).getText().equals("abc"));
	}
	
	@Test
	public void testPriority() {
		list.setPriority(true);
		HBox hbox = list.getHBox();
		
		Assert.assertFalse("Priority not setted", hbox.getStyle().trim().isEmpty());
	}

	@Test
	public void testFloatingTime() {
		list.clearTime();
		list.setTimes();
		HBox hbox = list.getHBox();
		
		Object secondChild = hbox.getChildren().get(1);
		Assert.assertTrue("Wrong time position", secondChild instanceof TextFlow);
		
		TextFlow timeText = (TextFlow) secondChild;
		Assert.assertTrue("Wrong floating text", ((Text) timeText.getChildren().get(0)).getText().equals("Floating"));
	}
	
	@Test
	public void testDeadlineTime() {
		list.clearTime();
		list.setTimes(Instant.EPOCH.toEpochMilli());
		HBox hbox = list.getHBox();
		
		Object secondChild = hbox.getChildren().get(1);
		Assert.assertTrue("Wrong time position", secondChild instanceof TextFlow);
		
		TextFlow timeText = (TextFlow) secondChild;
		Assert.assertEquals("Wrong number of time text", 
				timeText.getChildren().size(), 1
				);
		
		String text = ((Text) timeText.getChildren().get(0)).getText();
		Assert.assertFalse("Deadline text empty", 
				text.trim().isEmpty()
				);
	}
	
	@Test
	public void testTimedTime() {
		list.clearTime();
		list.setTimes(Instant.EPOCH.toEpochMilli(), Instant.EPOCH.toEpochMilli());
		HBox hbox = list.getHBox();
		
		Object secondChild = hbox.getChildren().get(1);
		Assert.assertTrue("Wrong time position", secondChild instanceof TextFlow);
		
		TextFlow timeText = (TextFlow) secondChild;
		Assert.assertEquals("Wrong number of time text", 
				timeText.getChildren().size(), 3
				);
		
		String text1 = ((Text) timeText.getChildren().get(0)).getText();
		String text2 = ((Text) timeText.getChildren().get(2)).getText();
		Assert.assertFalse("Timed text empty", 
				text1.trim().isEmpty() || text2.trim().isEmpty()
				);
	}
}
