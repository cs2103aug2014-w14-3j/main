package controller;
import java.util.Date;

 class TaskToString {

	 
	 
	// TaskType: 1 for floating, 2 for Deadline and 3 for TimedTask
	// isPrioritized: 0 is false, 1 is true
	//Format for the different types:
	//   *FloatingTask: <taskType>$<isPrioritized>$<content>
	//   *DeadlineTask: <taskType>$<isPrioritized>$<content>$<date>
	//   *TimedTask   : <taskType>$<isPrioritized>$<content>$<startTime>$<endTime>
	public static String taskToString(Task task){
		
		String isPrioritized;
		String content;
		
		
		if (task.isPrioritized()){
			isPrioritized="1";
		}
		else {
		isPrioritized="0";
		}
		
		content=task.getDesc();
		
		switch(task.getType()){
		
		case FLOATING:
			return "1$"+isPrioritized+"$"+content;
			break;
		case DEADLINE:
			String date=task.getDate.toString();
			
			return "2$"+isPrioritized+"$"+content+"$"+date;
			break;
		case TIMED:
			String startTime=task.getStartTime().toString();
			String endTime=task.getEndTime().toString();
			
			return "3$"+isPrioritized+"$"+content+"$"+startTime+"$"+endTime;
			break;
		}
		
	}
	
	public static Task stringToTask(String string){
		
		String[] para=string.trim().split("$");
		
		Boolean isPrioritized;
		
		if (para[1].equals("0")){
			isPrioritized=false;
		}else {
			isPrioritized=true;
		}
		
		String content=para[2];
		
		switch(Integer.parserInt(para[0])){
		
		case 1:
			return new FloatingTask(isPrioritized,content);
			break;
		case 2:
			Date date=new Date(para[3]);
			return new DeadlineTask(isPrioritized,content,date);
			break;
		case 3:
			Date startTime=new Date(para[3]);
			Date endTime=new Date(para[4]);
			return new TimedTask(isPrioritized,content,startTime,endTime);
			break;
		}
		
}
