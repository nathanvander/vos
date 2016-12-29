package vos2.core;
import apollo.iface.*;
import apollo.util.DateYMD;

//A Matter is anything about a client or case
public class Matter implements DataObject {
		public long rowid;
		public String _key;
		public String mattername;
		public String type;		//e.g. CR,DR
		public DateYMD date_opened;
		public String firstname;
		public String lastname;
		public String phone;
		public String email;
		public String address1;
		public String address2;
		public String court;	//e.g. Adams
		public String casenum;
		public String otr_party;
		public String otr_atty;
		public boolean open;
		public String desc;		//textarea

		public Matter() {}

	public String getTableName() {return "Matter";}

	public String[] fields() {return new String[]{"mattername","type","date_opened","firstname","lastname","phone","email","address1","address2","court","casenum","otr_party","otr_atty","open","desc"};}

	public String index() {return "mattername";}

	public String getKey() {return _key;}
}