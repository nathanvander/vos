package vos2.reports;
import vos2.core.*;
import weasel.WebObject;
import apollo.iface.*;
import java.util.Hashtable;

/**
* The Tasks reports lists all open tasks
*/
public class TasksReport implements WebObject {

	public String display(DataStore ds, String resource,Hashtable params) {

		StringBuffer sb=new StringBuffer();
		sb.append("<html><b>Tasks</b>\r\n");

		try {
			//get a list of all activities
			apollo.iface.Cursor it=ds.selectAll(new Task());
			it.open();

			sb.append("<table border=1><tr><th>Key</th><th>Matter</th><th>Priority</th><th>Date</th><th>Name</th><th>Deadline</th><th>Details</th></tr>\r\n");

			while (it.hasNext()) {
				Task t=(Task)it.next();
				sb.append("<tr><td>"+t.taskid+"</td><td>"+t.mattername+"</td><td>"+t.priority+"</td><td>"+t.date+"</td><td>"+t.name+"</td><td>"+t.deadline+"</td><td>"+t.deadline+"</td></tr>\r\n");
			}

			it.close();
			sb.append("</table></html>");
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}

		return sb.toString();
	}
}