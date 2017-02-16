package vos2.reports;
import apollo.iface.ViewObject;
import apollo.util.DateYMD;
import apollo.iface.DataObject;

/**
* An Event is like a sub-class of Activity.
*
* An Event is a scheduled time.  The name "Event" is overused, but it is the common name used for scheduling
* things on a calendar.
*
* This list all activities that have a type of EVENT and a status of OPEN.
* It is sorted by date and time
*/
public class Event implements ViewObject {

	public long eventid;		//from Activity
	public long mid;
	public String mattername;	//from Matter
	public DateYMD date;
	public String time;
	public String location;
	public String name;
	public String desc;
	//note that type and status are not included in the view because they never change

	//this is used only to create the view
	//after it is created, you can just do SELECT * FROM Event
	//the CREATE language is not used here
	//see TransactionObject.CreateView
	public String getSQL() {
		StringBuffer sb=new StringBuffer("SELECT a.rowid AS eventid, a.mid AS mid, m.mattername AS mattername, a.date AS date, a.time AS time, ");
		sb.append("a.location AS location,a.name AS name, a.desc AS desc ");
		sb.append("FROM Activity a,Matter m ");
		sb.append("WHERE a.mid=m.rowid ");
		sb.append("AND a.type='EVENT' ");
		sb.append("AND a.status='OPEN' ");
		sb.append("ORDER BY date, time");
		return sb.toString();
	}

	/**
	* This is the name of the view.  Don't use the same name as a table.
	*/
	public String getViewName() {return "Event";}

	public String[] fields() {return new String[]{"eventid","mid","mattername","date","time","location","name","desc"};}

	public String[] displayNames() {return null;}

	//these fields are not used.  this is to simplify Cursor.next()
	public String getTableName() {return "Event";}
	public String index() {return null;}
	public long getID() {return eventid;}

	//not supported, never used
	public DataObject clone() {return null;}
}