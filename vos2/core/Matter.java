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
* A Matter is conceptually a context or container for a set of Activities.
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
*
* Updated 1/22/2017 to remove key, which is no longer needed.
*/
public class Matter implements DataObject {
		public final static MathContext FORMAT=new MathContext(2);	//uses HALF_UP rounding mode

		//identity fields
		public long rowid;
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
		public java.awt.TextArea desc;		//textarea

		//new fields
		public long parent;		//the key of the parent project, usually null
		public BigDecimal budget;	//the budget for the project, can be null
		public BigDecimal rate;		//the hourly rate for the project
		public int priority;
		public String status;
		public DateYMD date_closed;

		public Matter() {}

	public String getTableName() {return "Matter";}

	public String[] fields() {return new String[]{"mattername","type","date_opened","firstname","lastname","phone","email","address1","address2","court","casenum","otr_party","otr_atty","desc","parent","budget","rate","priority","status","date_closed"};}

	public String[] displayNames() {return new String[]{"Matter Name","Type","Date Opened","First Name","Last Name","Phone","Email","Address","Address (City,State)","Court","Case Number","Other Party","Other Attorney","Description","Parent Matter","Budget","Rate","Priority","Status","Date_Closed"};}

	public String index() {return "mattername";}

	public long getID() {return rowid;}

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

	//do a deep copy of this
	//the primitives and Strings are just copied.  Objects must be cloned
	public Matter clone() {
		Matter minime = new Matter();

		minime.rowid = rowid;
		minime.mattername = mattername;
		minime.type = type;
		if (date_opened!=null) {
			minime.date_opened = date_opened.clone();
		}

		//contact info
		minime.firstname = firstname;
		minime.lastname = lastname;
		minime.phone = phone;
		minime.email = email;
		minime.address1 = address1;
		minime.address2 = address2;

		//more matter data
		minime.court = court;
		minime.casenum = casenum;
		minime.otr_party = otr_party;
		minime.otr_atty = otr_atty;
		if (desc!=null) {
			minime.desc = new java.awt.TextArea(desc.getText(),3,40,java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			//also change rows and cols if changed
			minime.desc.setRows(desc.getRows());
			minime.desc.setColumns(desc.getColumns());
		}
		//new fields
		minime.parent = parent;
		if (budget!=null) {
			String sb = budget.toPlainString();
			minime.budget= new BigDecimal(sb);
		}
		if (rate!=null) {
			String sr= rate.toPlainString();
			minime.rate = new BigDecimal(sr);
		}
		minime.priority=priority;
		minime.status=status;
		if (date_closed!=null) {
			minime.date_closed = date_closed.clone();
		}

		return minime;
	}
}