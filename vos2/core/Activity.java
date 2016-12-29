package vos2.core;
import apollo.iface.*;
import apollo.util.DateYMD;
import java.awt.Choice;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* An Activity is anything that you do on behalf of the matter.
* The type is very important.  Types:
*	Event
*	Task
*	Email
*	Phone
*	Meeting
*	Document - drafting and filing documents
*	Memo
*	Other
*
*	To create a Task, just select type Task.  When complete, set status to complete.
*	To create an event, select type Event.
*
*   Sorted based on date/time
*/
public class Activity implements DataObject {
	//rounds to the nearest 10th of an hour
	public static DecimalFormat hours=new DecimalFormat("#0.0");

	public long rowid;
	public String _key;
	public String mid;			//the matter id.  This is a foreign key, but not enforced
								//corresponds to Matter._key
	public String type;			//select type from list
	public String name;   		//short description.  aka title
	public DateYMD date;		//format YYYY-MM-DD
	public String time;			//format HH:MM, no AM/PM
	public DateYMD deadline; 	//if a task
	public boolean complete=false;
	public double elapsed;
	public String phone;
	public int priority;
	public String desc;  		//textarea


	public String getTableName() {return "Activity";}

	public String[] fields() {return new String[]{"mid","type","name","date","time","deadline","complete","elapsed","phone","priority","desc"};}

	public String index() {return "date,time";}

	public String getKey() {return _key;}


	//even though this is a double, we want to display it to the nearest 10th of an hour
	public String getElapsed() {
		return hours.format(elapsed);
	}

	//set the elapsed time
	public void setElapsed(String elapse) {
		try {
			elapsed=Double.parseDouble(elapse);
		} catch (NumberFormatException x) {
			System.out.println("Warning: unable to parse "+elapse);
		}
	}

	//this is just a static list.  you have to select the active one
	public static Choice getTypeList() {
		Choice list=new Choice();
		list.add("EVENT");
    	list.add("TASK");
    	list.add("MEETING");
    	list.add("DOCUMENT");
    	list.add("EMAIL");
    	list.add("PHONE");
    	list.add("VOICEMAIL");
    	list.add("MEMO");
    	list.add("OTHER");
		return list;
	}

	//get the time now in HH:mm format
	public static String getTime() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		return timeFormat.format(new Date());
	}

}