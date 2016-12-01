package vos.core;

//A Matter is anything about a client or case
public class Matter implements Comparable {
		public String mattername;
		public String type;		//e.g. CR,DR
		public String date_opened;
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

	public String sortKey() {
		return mattername.toUpperCase();
	}

	public int compareTo(Object o) {
		if (o==null) {return -1;}  	   //put that at the end
		if (o instanceof Matter) {
			Matter m=(Matter)o;
			return sortKey().compareTo(m.sortKey());
		} else {
			return -1;
		}
	}

}