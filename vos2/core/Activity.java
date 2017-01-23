package vos2.core;
import apollo.iface.*;
import apollo.util.DateYMD;
import java.awt.Choice;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* An Activity is anything that you do on behalf of the matter.
*
* It is a very generic table and handles appointments, to-do items, documents, notes,
* records of emails, phone calls, items, and time events.
*
* The type field show the type, which could be a sub-class.  There are 2 important types:
*	EVENT - used for Calendar events.  (The word "event" is overused, but I can't find a better name for it).
*	TASK - used for to-do items
* They are not that much different, but Events are scheduled appointments, and Tasks don't but have a deadline.
*
* 	time_entered is used by the time and billing system.
*
* The elapsed time is important because it will be used for time and billing
*/
public class Activity implements DataObject {
	//rounds to the nearest 10th of an hour
	public static DecimalFormat hours=new DecimalFormat("#0.0");

	public long rowid;
	public String _key;
	public String mid;			//the matter id.  This is a foreign key, but not enforced
								//corresponds to Matter._key
	public String type;			//select type from list
	public DateYMD date_opened;	//don't edit this, it is just the date entered
	public String name;   		//short description.  aka title
	public DateYMD date;		//format YYYY-MM-DD.  This either the date entered or scheduled
								//for non-events, it is the date completed
	public String time;			//format HH:MM, no AM/PM
	public String location;
	public DateYMD deadline; 	//if a task
	public double elapsed;
	public String phone;		//if there is a phone call involved
	public int priority;
	public String desc;  		//textarea
	public String status;
	public boolean time_entered=false;


	public String getTableName() {return "Activity";}

	public String[] fields() {return new String[]{"mid","type","name","date","time","location","deadline","elapsed","phone","priority","desc","status","time_entered"};}

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

	public static Choice getStatusList() {
		Choice list=new Choice();
		list.add("OPEN"); 			//for events, it is scheduled
    	list.add("COMPLETED");		//done.  It won't show up as a scheduled event, or on the to-do list
    	list.add("MEMO");			//nothing to do, this is just a note
    	list.add("CANCELLED");
		return list;
	}
}