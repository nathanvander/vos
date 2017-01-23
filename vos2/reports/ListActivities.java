package vos2.reports;
import vos2.core.*;
import weasel.WebObject;
import apollo.iface.*;
import java.util.Hashtable;


/**
* This is the List Activities reports.  It lists all activities associated with a given matter
* It expects a resource string with this format
*	/vos2.reports.ListActivities/mid	where mid is the key of the matter
*/
public class ListActivities implements WebObject {

	public String display(DataStore ds, String resource,Hashtable params) {

		//first get the matter id from the resource
		if (resource==null) {
			return "expecting resource with the format /vos2.reports.ListActivities/mid";
		}
		String[] part=resource.split("/");
		//part 0 is empty, since this because with a string
		//part 1 is the class name
		String mid=null;
		if (part.length>2) {
			mid=part[2];
			if (mid==null || mid.length()==0) {
				return "expecting resource with the format /vos2.reports.ListActivities/mid";
			}
		} else {
			return "expecting resource with the format /vos2.reports.ListActivities/mid";
		}

		StringBuffer sb=new StringBuffer();
		sb.append(Vos.getHeader("View Activities")+"\r\n");

		try {
			Matter m=(Matter)ds.get(new Key("Matter",mid));

			sb.append("<html>Matter name: "+m.mattername+"<br>\r\n");
			sb.append("Client name: "+m.firstname+" "+m.lastname+"<br>");

			//get a list of all activities matching the where clause
			String where="WHERE mid='"+mid+"' ORDER BY date,time";
			Cursor it=ds.selectWhere(new Activity(),where);
			it.open();

			sb.append("<table border=1><tr><th>Key</th><th>Activity</th><th>Type</th><th>Date</th><th>Description</th><th>Elapsed Time</th><th>Priority</th><th>Status</th></tr>\r\n");

			while (it.hasNext()) {
				Activity a=(Activity)it.next();
				sb.append("<tr><td>"+a.getKey()+"</td><td>"+a.name+"</td><td>"+a.type+"</td><td>"+a.date+"</td><td>"+a.desc+"</td><td>"+a.elapsed+"</td><td>"+a.priority+"</td><td>"+a.status+"</td></tr>\r\n");
			}

			it.close();
			sb.append("</table></html>\r\n");

		} catch (Exception x) {
			System.out.println(x.getMessage());
			sb.append(x.getMessage());
		}
		return sb.toString();
	}
}