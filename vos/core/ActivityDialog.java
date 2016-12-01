package vos.core;
import java.awt.*;
import java.awt.event.*;
import vos.db.*;

//if this is an Add dialog, then mid should have a value and oid should be null
//if this is an edit dialog, then mid should be null, and oid should have a value
public class ActivityDialog extends Dialog implements ActionListener {
		String mid;  //matter id
		//List list1;
		Choice list1;
		TextComponent t1;
		TextComponent t2;
		TextComponent t3;
		TextComponent t4;
		TextComponent t5;
		TextComponent t6;
		TextComponent t7;
		Checkbox cb1;
		DataStore ds;
		String oid;  //activity id

		public ActivityDialog(Window f,DataStore ds,String mid,String oid) {
			super(f,"Activity Dialog");
			this.ds=ds;
			this.setSize(350,500);
			this.mid=mid;
			this.oid=oid;
			addWindowListener(new DialogListener(this));
			setLayout(new BorderLayout());

			Panel top=new Panel(new GridLayout(9,2));
			add(top,BorderLayout.NORTH);

			Panel center=new Panel(new FlowLayout());
			add(center,BorderLayout.CENTER);

			Panel south=new Panel(new FlowLayout());
			add(south,BorderLayout.SOUTH);

			//retrieve Activity
			Activity a=null;
			if (oid==null) {
				a=new Activity();
				a.mid=mid;
			} else {
				a=(Activity)ds.get(oid);
				this.mid=a.mid;  //this.mid might be null before this
			}

			//matterid
			top.add(new Label("Matter #"));
			top.add(new Label(a.mid));

			//type
			top.add(new Label("Type"));
			//list1 = new List(2);
			list1 = new Choice();
			list1.add("Event");
			list1.add("Task");
			list1.add("document");
			list1.add("email");
			list1.add("meeting");
			list1.add("memo");
			list1.add("phone");
			list1.add("voicemail");
			list1.add("other");
			top.add(list1);
			list1.select(a.type);

			top.add(new Label("Name"));
			t1 = new TextField(a.name, 20);
			top.add(t1);

			top.add(new Label("Date (YYYY-MM-DD)"));
			if (a.date==null) {
				a.date=Vos.getDate();
			}
			t2 = new TextField(a.date, 20);
			top.add(t2);

			top.add(new Label("Time (hh:mm)"));
			if (a.time==null) {
				a.time=Vos.getTime();
			}
			t3 = new TextField(a.time, 10);
			top.add(t3);

			top.add(new Label("Deadline (YYYY-MM-DD)"));
			if (a.deadline==null) {
					a.deadline=Vos.getDate();
			}
			t4 = new TextField(a.deadline, 20);
			top.add(t4);

			top.add(new Label("Phone"));
			t5 = new TextField(a.phone, 12);
			top.add(t5);

			top.add(new Label("Time Elapsed (e.g. 1.5)"));
			t6 = new TextField(String.valueOf(a.elapsed), 20);
			top.add(t6);

			top.add(new Label("Complete"));
			cb1 = new Checkbox("complete",a.complete);
			top.add(cb1);

			center.add(new Label("Notes"));
			t7 = new TextArea(a.desc,3,40);
			center.add(t7);

			Button bp=null;
			if (oid==null) {
				bp=new Button("Add");
			} else {
				bp=new Button("Update");
			}
			bp.addActionListener(this);
			south.add(bp);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
				Activity a=new Activity();
				a.mid=mid;
				a.type=list1.getSelectedItem();
				a.name=t1.getText();
				a.date=t2.getText();
				a.time=t3.getText();
				a.deadline=t4.getText();
				a.phone=t5.getText();

				String elapsed=t6.getText();
				try {
					a.elapsed=Float.parseFloat(elapsed);
				} catch (Exception x) {
					System.out.println("WARNING: "+x.getMessage());
				}

				a.complete=cb1.getState();
				a.desc= t7.getText();
				//get rid of newline by replacing it with a space
				a.desc = a.desc.replaceAll("\\r?\\n"," ");

			if (cmd.equals("Add")) {
				try {
					String k=ds.insert(a);
					System.out.println("insert successful, key="+k);
					//status.setText("inserted matter, key="+k);
				} catch (Exception x) {
					x.printStackTrace();
				}
			} else if (cmd.equals("Update")) {
				try {
					ds.update(oid,a);
					System.out.println("update successful, key="+oid);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			this.dispose();
		}
}
