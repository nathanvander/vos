package vos2.core;
import java.awt.*;
import java.awt.event.*;
import apollo.iface.*;
import apollo.util.DateYMD;

//if this is an Add dialog, then mid should have a value and oid should be null
//if this is an edit dialog, then mid should be null, and oid should have a value
public class ActivityDialog extends Dialog implements ActionListener {
		String mid;  //matter id

		//14 components
		//you can't edit time_entered
		Label l_key;	//_key;
		Label l_mid;		//mid
		Choice c_type;		//types
		TextComponent t_date_opened;
		TextComponent t_name;	//name
		TextComponent t_date;
		TextComponent t_time;
		TextComponent t_location;
		TextComponent t_deadline;
		TextComponent t_elapsed;
		TextComponent t_phone;
		TextComponent t_priority;	//priority
		Choice c_status;		//complete
		TextComponent t_desc;	//notes

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

			Panel top=new Panel(new GridLayout(14,2));
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
				Key k=new Key("Activity",oid);
				try {
				a=(Activity)ds.get(k);
				this.mid=a.mid;  //this.mid might be null before this
				} catch (Exception x) {
					System.out.println("WARNING: unable to get Activity data");
				}
			}

			//row 0
			if (oid!=null) {
				top.add(new Label("Key"));
				l_key=new Label(oid);
				top.add(l_key);
			}

			//row 1
			//matterid
			top.add(new Label("Matter #"));
			l_mid=new Label(a.mid);
			top.add(l_mid);

			//type
			top.add(new Label("Type"));
			c_type = Activity.getTypeList();
			c_type.select(a.type);
			top.add(c_type);

			//only allow editing date_opened on initial dialog
			//date_opened
			if (oid==null) {
				top.add(new Label("Date Opened (YYYY-MM-DD))"));
				if (a.date_opened==null) {
					a.date_opened=new DateYMD();
				}
				t_date_opened=new TextField(a.date_opened.toString(), 20);
				top.add(t_date_opened);
			} else {
				//just set the field but don't display it
				if (a.date_opened!=null) {
					t_date_opened=new TextField(a.date_opened.toString(), 20);
				}
			}

			//name
			top.add(new Label("Name"));
			t_name = new TextField(a.name, 20);
			top.add(t_name);

			//date
			top.add(new Label("Date (YYYY-MM-DD)"));
			if (a.date==null) {
				a.date=new DateYMD();
			}
			t_date = new TextField(a.date.toString(), 20);
			top.add(t_date);

			//time
			top.add(new Label("Time (hh:mm)"));
			if (a.time==null) {
				a.time=Activity.getTime();
			}
			t_time = new TextField(a.time, 10);
			top.add(t_time);

			//location
			top.add(new Label("Location"));
			t_location = new TextField(a.location, 20);
			top.add(t_location);

			//deadline
			top.add(new Label("Deadline (YYYY-MM-DD)"));
			if (a.deadline==null) {
				a.deadline=new DateYMD();
			}
			t_deadline = new TextField(a.deadline.toString(), 20);
			top.add(t_deadline);

			//elapsed
			top.add(new Label("Time Elapsed (e.g. 1.5)"));
			t_elapsed = new TextField(String.valueOf(a.getElapsed()), 20);
			top.add(t_elapsed);

			//phone
			top.add(new Label("Phone"));
			t_phone = new TextField(a.phone, 12);
			top.add(t_phone);

			//priority
			top.add(new Label("Priority (0..100)"));
			t_priority=new TextField(String.valueOf(a.priority),20);
			top.add(t_priority);

			//status
			top.add(new Label("Status"));
			c_status=Activity.getStatusList();
			if (a.status!=null) {
				c_status.select(a.status);
			}
			top.add(c_status);

			//notes
			center.add(new Label("Notes"));
			t_desc = new TextArea(a.desc,3,40);
			center.add(t_desc);

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
			try {
				a.mid=mid;
				a.type=c_type.getSelectedItem();
				a.date_opened=DateYMD.fromString(t_date_opened.getText());
				a.name=t_name.getText();
				a.date=DateYMD.fromString(t_date.getText());
				a.time=t_time.getText();
				a.location=t_location.getText();
				a.deadline=DateYMD.fromString(t_deadline.getText());
				a.setElapsed(t_elapsed.getText());
				a.phone=t_phone.getText();

				a.priority=Integer.parseInt(t_priority.getText());

				//status
				a.status=c_status.getSelectedItem();

				//description
				a.desc= t_desc.getText();
				//get rid of newline by replacing it with a space
				a.desc = a.desc.replaceAll("\\r?\\n"," ");
			} catch (Exception x) {
				System.out.println("unable to update activity: "+x.getMessage());
				return;
			}

			if (cmd.equals("Add")) {
				try {
					Transaction tx=ds.createTransaction();
					tx.begin();
					Key k=tx.insert(a);
					tx.commit();
					System.out.println("insert successful, key="+k.id);
				} catch (Exception x) {
					x.printStackTrace();
				}
			} else if (cmd.equals("Update")) {
				try {
					a._key=oid;
					//a.mid is already set
					Transaction tx=ds.createTransaction();
					tx.begin();
					tx.update(a);
					tx.commit();
					System.out.println("update successful, key="+oid);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			this.dispose();
		}
}
