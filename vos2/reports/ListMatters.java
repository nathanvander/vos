package vos2.reports;
import vos2.core.Vos;
import vos2.core.Matter;
import weasel.WebObject;
import apollo.iface.DataStore;
import apollo.iface.Cursor;
import java.util.Hashtable;


/**
* This is the List Matters reports.  It lists all matters
*/
public class ListMatters implements WebObject {

	public String display(DataStore ds, String resource,Hashtable params) {
		//create page
		StringBuffer sb=new StringBuffer();
		sb.append(Vos.getHeader("View Matters")+"\r\n");

		//get a list of all matters
		try {
			Cursor it=ds.selectAll(new Matter());
			it.open();

			sb.append("<table border=1><tr><th>Key</th><th>Matter Name</th><th>Priority</th><th>Status</th></tr>\r\n");
			while (it.hasNext()) {
				Matter m=(Matter)it.next();
				sb.append("<tr><td>"+m.getKey()+"</td><td>"+m.mattername+"</td><td>"+m.priority+"</td><td>"+m.status+"</td></tr>\r\n");
			}
			it.close();
			sb.append("</table>");
		} catch (Exception x) {
			System.out.println(x.getMessage());
			sb.append(x.getMessage());
		}

		return sb.toString();
	}
}