package vos.core;
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
*	To create an event, select type Event.  When complete, set status to complete
*
*   Sorted based on date/time
*/
public class Activity implements Comparable {
	public String mid;			//the matter id
	public String type;		//select type from list
	public String name;   	//short description.  aka title
	public String date;		//format YYYY-MM-DD
	public String time;		//format HH:MM, no AM/PM
	public String deadline; //if a task
	public boolean complete=false;
	public float elapsed;
	public String phone;
	public String desc;  //textarea

	public String sortKey() {
		return date+" "+time;
	}

	public int compareTo(Object o) {
		if (o==null) {return -1;}  	   //put that at the end
		if (o instanceof Activity) {
			Activity a=(Activity)o;
			return sortKey().compareTo(a.sortKey());
		} else {
			return -1;
		}
	}


}