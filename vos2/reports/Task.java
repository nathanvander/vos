package vos2.reports;
import apollo.iface.ViewObject;
import apollo.util.DateYMD;

/**
* A Task is like a sub-class of Activity.
*
* A Task is a todo item.  It will be sorted by priority.
*
* This list all events that have a type of TASK and a status of OPEN
*/
public class Task implements ViewObject {

	public String _key;			//from Activity
	public String mid;
	public String mattername;	//from Matter
	public int priority;
	public DateYMD date;		//the date is when we expect to work on it.  Not the date it was opened
	public String name;			//the name of the task
	public DateYMD deadline;
	public String desc;
	//note that type and status are not included in the view because they never change

	//this is used only to create the view
	public String getSQL() {
		StringBuffer sb=new StringBuffer("SELECT a._key AS _key, a.mid AS mid, m.mattername AS mattername, a.priority AS priority, a.date AS date, ");
		sb.append("a.name AS name, a.deadline AS deadline, a.desc AS desc ");
		sb.append("FROM Activity a,Matter m ");
		sb.append("WHERE a.mid=m._key ");
		sb.append("AND a.type='TASK' ");
		sb.append("AND a.status='OPEN' ");
		return sb.toString();
	}

	/**
	* This is the name of the view.  Don't use the same name as a table.
	*/
	public String getViewName() {return "Task";}

	public String[] fields() {return new String[]{"_key","mid","mattername","priority","date","name","deadline","desc"};}

	//these fields are not used.  this is to simplify Cursor.next()
	public String getTableName() {return "Task";}
	public String index() {return null;}
	public String getKey() {return null;}
}