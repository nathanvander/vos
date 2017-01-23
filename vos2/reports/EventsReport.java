package vos2.reports;
import vos2.core.*;
import weasel.WebObject;
import apollo.iface.*;
import java.util.Hashtable;

/**
* The Events report is used to report all Activities where the type is EVENT
* and the status is OPEN
*/
public class EventsReport implements WebObject {

	public String display(DataStore ds, String resource,Hashtable params) {

		StringBuffer sb=new StringBuffer();
		sb.append("<html><b>Events</b>"+"\r\n");

		//get a list of all activities
		try {
			Cursor it=ds.selectAll(new Event());
			it.open();

			sb.append("<table border=1><tr><th>Key</th><th>Matter</th><th>Date</th><th>Time</th><th>Location</th><th>Name</th><th>Details</th></tr>\r\n");

			while (it.hasNext()) {
				Event ev=(Event)it.next();
				sb.append("<tr><td>"+ev.eventid+"</td><td>"+ev.mattername+"</td><td>"+ev.date+"</td><td>"+ev.time+"</td><td>"+ev.location+"</td><td>"+ev.name+"</td><td>"+ev.desc+"</td></tr>\r\n");
			}
			it.close();
			sb.append("</table></html>");
		} catch (Exception x) {
			System.out.println(x.getMessage());
			sb.append(x.getMessage());
		}

		return sb.toString();
	}
}