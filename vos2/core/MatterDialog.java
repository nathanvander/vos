package vos2.core;
import java.awt.*;
import java.awt.event.*;
import apollo.iface.*;
import apollo.util.DateYMD;
/**
* To create a new Add Matter Dialog, use a zero as the oid
*/
public class MatterDialog extends Dialog implements ActionListener {
			//20 rows
			//we don't let you edit date_closed
			Label l_key;	//_key;
			TextComponent t1;	//mattername
			TextComponent t2;
			TextComponent t_date_opened;
			TextComponent t4;
			TextComponent t5;
			TextComponent t6;
			TextComponent t7;
			TextComponent t8;
			TextComponent t9;
			TextComponent t10;
			TextComponent t11;
			TextComponent t12;
			TextComponent t13;	//otr_attry
			TextArea t_desc;
			TextComponent t_parent;
			TextComponent t_budget;
			TextComponent t_rate;
			TextComponent t_priority;
			Choice c_status;

			DataStore ds;
			long oid;

			//this will be used only if object is changed
			Matter oldMatter;

			public MatterDialog(Frame f,DataStore ds,long oid) {
				super(f,"Matter Dialog");
				this.ds=ds;
				this.setSize(350,700);
				addWindowListener(new DialogListener(this));
				setLayout(new BorderLayout());

				Panel top=new Panel(new GridLayout(21,2));
				add(top,BorderLayout.NORTH);

				Panel center=new Panel(new FlowLayout());
				add(center,BorderLayout.CENTER);

				Panel south=new Panel(new FlowLayout());
				add(south,BorderLayout.SOUTH);

				this.oid=oid;

				//get data
				Matter m=null;
				if (oid<1) {
					m=new Matter();
				} else {
					try {
						Key k=new Key("Matter",oid);
						m=(Matter)ds.get(k);
						if (m!=null) {
							oldMatter=m.clone();
						}
					} catch (Exception x) {
						System.out.println("WARNING: unable to get Matter data");
						//need more detail. What is the problem?
						x.printStackTrace();
					}
				}

				//row 0
				if (oid<0) {
					top.add(new Label("Key"));
					l_key=new Label("#"+oid);
					top.add(l_key);
				}

				//row 1
				top.add(new Label("Matter Name"));
				t1 = new TextField(m.mattername, 20);
				top.add(t1);

				//row 2
				top.add(new Label("Type (e.g. CR)"));
				t2 = new TextField(m.type, 5);
				top.add(t2);

				//row 3
				top.add(new Label("Date Opened (YYYY-MM-DD)"));
				if (m.date_opened==null) {
					m.date_opened=new DateYMD();
				}
				t_date_opened = new TextField(m.date_opened.toString(), 12);
				top.add(t_date_opened);

				//row 4
				top.add(new Label("First Name"));
				t4 = new TextField(m.firstname, 20);
				top.add(t4);

				//row 5
				top.add(new Label("Last Name"));
				t5 = new TextField(m.lastname, 20);
				top.add(t5);

				//row 6
				top.add(new Label("Phone"));
				t6 = new TextField(m.phone, 12);
				top.add(t6);

				//row 7
				top.add(new Label("Email"));
				t7 = new TextField(m.email, 20);
				top.add(t7);

				//row 8
				top.add(new Label("Address 1"));
				t8=new TextField(m.address1,20);
				top.add(t8);

				//row 9
				top.add(new Label("Address 2 (City,State)"));
				t9=new TextField(m.address2,20);
				top.add(t9);

				//row 10
				top.add(new Label("Court"));
				t10 = new TextField(m.court, 20);
				top.add(t10);

				//row 11
				top.add(new Label("Case No"));
				t11 = new TextField(m.casenum, 20);
				top.add(t11);

				//row 12
				top.add(new Label("Other Party"));
				t12 = new TextField(m.otr_party, 20);
				top.add(t12);

				//row 13
				top.add(new Label("Other Attorney"));
				t13 = new TextField(m.otr_atty, 20);
				top.add(t13);

				//row 14 - notes, moved to the bottom
				//row 15.  This needs a long
				top.add(new Label("Parent"));
				t_parent=new TextField(String.valueOf(m.parent),20);
				top.add(t_parent);

				//row 16
				top.add(new Label("Budget (amount)"));
				if (m.budget!=null) {
					t_budget=new TextField(m.budget.toPlainString(),20);
				} else {
					t_budget=new TextField("0.00",20);
				}
				top.add(t_budget);

				//row 17
				top.add(new Label("Rate (amount)"));
				if (m.rate!=null) {
					t_rate=new TextField(m.rate.toPlainString(),20);
				} else {
					t_rate=new TextField("0.00",20);
				}
				top.add(t_rate);

				//row 18
				top.add(new Label("Priority (0..100)"));
				t_priority=new TextField(String.valueOf(m.priority),20);
				top.add(t_priority);

				//row 19
				top.add(new Label("Status"));
				c_status=Matter.getStatusList();
				if (m.status!=null) {
					c_status.select(m.status);
				}
				top.add(c_status);

				//there is also date_closed, but that can't be edited

				//notes
				center.add(new Label("Notes"));
				if (m.desc!=null) {
					t_desc=m.desc;
				} else {
					t_desc = new TextArea("",3,40);
				}
				center.add(t_desc);

				Button bp=null;
				if (oid<1) {
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

				Matter m=new Matter();
				m.mattername=t1.getText();
				m.type=t2.getText();
				try {
					m.date_opened=DateYMD.fromString(t_date_opened.getText());
				} catch (Exception x) {
					System.out.println("warning: unable to parse "+t_date_opened.getText());
				}
				m.firstname=t4.getText();
				m.lastname=t5.getText();
				m.phone=t6.getText();
				m.email=t7.getText();
				m.address1=t8.getText();
				m.address2=t9.getText();
				m.court=t10.getText();
				m.casenum=t11.getText();
				m.otr_party=t12.getText();
				m.otr_atty=t13.getText();

				m.desc=t_desc;
				String sdesc=t_desc.getText();
				if (sdesc!=null) {
					//get rid of newline by replacing it with a space
					m.desc.setText(sdesc.replaceAll("\\r?\\n"," "));
				}

				//parent is a long
				try {
					m.parent=Long.parseLong(t_parent.getText());
				} catch (NumberFormatException x) {
					System.out.println("warning: unable to parse "+t_parent.getText()+" as a long");
				}

				//budget
				m.setBudget(t_budget.getText());

				//rate
				m.setRate(t_rate.getText());

				//priority
				try {
					m.priority=Integer.parseInt(t_priority.getText());
				} catch (NumberFormatException x) {
					System.out.println("warning: unable to parse "+t_priority.getText()+" as an int");
				}


				//status
				m.status=c_status.getSelectedItem();

				if (cmd.equals("Add")) {
 					try {
						Transaction tx=ds.createTransaction();
						tx.begin();
						Key k=tx.insert(m);
						tx.commit();
						System.out.println("insert successful, key="+k.rowid);
					} catch (Exception x) {
						x.printStackTrace();
					}
				} else if (cmd.equals("Update")) {
					try {
						//update this in database
						//requires old version of object
						m.rowid=oid;
						Transaction tx=ds.createTransaction();
						tx.begin();
						tx.update(oldMatter,m);
						tx.commit();
						System.out.println("update successful, key="+oid);
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				this.dispose();
			}
}
