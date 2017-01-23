package vos2.core;
import apollo.iface.*;
import apollo.util.DateYMD;
import java.math.BigDecimal;
import java.math.MathContext;
import java.awt.Choice;

/**
* A Matter is anything about a client or case.  This could be renamed Project or Process
* but I don't want to change it now.  It has fields that could belong in a Contact table,
* which are combined to save space, because usually there is a one-to-one mapping.
*
* A Matter is conceptually a context or container for a set of Activities.  It should be flexible
* to represent the concept of a Room in a MUD system.  It is also similar to the concept of a Thread.
*
* A Matter could also represent a creditor, and the events are the payments made and the communication.
*
* Deleted fields:
*		open (it duplicates the function of status)
*
* New Fields:
*		parent - so this can be a sub-project
*		budget - the overall budget for the project
*		rate - the hourly rate associated with the matter
*		priority - an int from 0 to 100.  Used for sorting
*		status - the only status really used right now is COMPLETED.  If it is completed, it won't
*			show up on lists of projects.
*		date_closed
*
* Possible other fields:
*		owner
*/
public class Matter implements DataObject {
		public final static MathContext FORMAT=new MathContext(2);	//uses HALF_UP rounding mode

		//identity fields
		public long rowid;
		public String _key;
		public String mattername;

		//matter data
		public String type;		//e.g. CR,DR
		public DateYMD date_opened;

		//contact info
		public String firstname;
		public String lastname;
		public String phone;
		public String email;
		public String address1;
		public String address2;

		//more matter data
		public String court;	//e.g. Adams
								//could be renamed org to make it more generic
		public String casenum;
		public String otr_party;
		public String otr_atty;
		public String desc;		//textarea

		//new fields
		public String parent;		//the key of the parent project, usually null
		public BigDecimal budget;	//the budget for the project, can be null
		public BigDecimal rate;		//the hourly rate for the project
		public int priority;
		public String status;
		public DateYMD date_closed;

		public Matter() {}

	public String getTableName() {return "Matter";}

	public String[] fields() {return new String[]{"mattername","type","date_opened","firstname","lastname","phone","email","address1","address2","court","casenum","otr_party","otr_atty","desc","parent","budget","rate","priority","status","date_closed"};}

	public String index() {return "mattername";}

	public String getKey() {return _key;}

	//this is more useful than an enumeration
	public static Choice getStatusList() {
		Choice list=new Choice();
		list.add("NONE");		//default, ready to use
    	list.add("RUNNING");	//this is the active process
    	list.add("READY");		//there is something to do here
    	list.add("WAITING");	//blocked, waiting on something.  The description should say what it is
    							//waiting on
    	list.add("SLEEPING");	//nothing to do now
    	list.add("PAUSED");		//on hold
    	list.add("CLOSED");		//closed.  We don't want to actually delete a matter, but if it is done, mark it as
    							//closed and you won't be able to select it
		return list;
	}

	public void setBudget(String s) {budget=new BigDecimal(s,FORMAT);}
	public void setBudget(double d) {budget=new BigDecimal(d,FORMAT);}

	public void setRate(String s) {rate=new BigDecimal(s,FORMAT);}
	public void setRate(double d) {rate=new BigDecimal(d,FORMAT);}


}