package vos.core;
import java.awt.*;
import java.awt.event.*;
import vos.db.*;

/**
* To create a new Add Matter Dialog, use a null String as the oid
*/
public class MatterDialog extends Dialog implements ActionListener {
			TextComponent t1;	//type
			TextComponent t2;
			TextComponent t3;
			TextComponent t4;
			TextComponent t5;
			TextComponent t6;
			TextComponent t7;
			TextComponent t8;
			TextComponent t9;
			TextComponent t10;
			TextComponent t11;
			TextComponent t12;
			TextComponent t13;
			TextComponent t14;
			Checkbox cb1;
			DataStore ds;
			String oid;

			public MatterDialog(Frame f,DataStore ds,String oid) {
				super(f,"Matter Dialog");
				this.ds=ds;
				this.setSize(350,500);
				addWindowListener(new DialogListener(this));
				setLayout(new BorderLayout());

				Panel top=new Panel(new GridLayout(15,2));
				add(top,BorderLayout.NORTH);

				Panel center=new Panel(new FlowLayout());
				add(center,BorderLayout.CENTER);

				Panel south=new Panel(new FlowLayout());
				add(south,BorderLayout.SOUTH);

				this.oid=oid;

				//get data
				Matter m=null;
				if (oid==null) {
					m=new Matter();
				} else {
					try {
						m=(Matter)ds.get(oid);
					} catch (Exception x) {
						System.out.println("WARNING: object is not of type matter");
					}
				}

				top.add(new Label("Matter Name"));
				t1 = new TextField(m.mattername, 20);
				top.add(t1);
				top.add(new Label("Type (e.g. CR)"));
				t2 = new TextField(m.type, 5);
				top.add(t2);
				top.add(new Label("Date Opened (YYYY-MM-DD)"));
				if (m.date_opened==null) {
					m.date_opened=Vos.getDate();
				}
				t3 = new TextField(m.date_opened, 12);
				top.add(t3);
				top.add(new Label("First Name"));
				t4 = new TextField(m.firstname, 20);
				top.add(t4);
				top.add(new Label("Last Name"));
				t5 = new TextField(m.lastname, 20);
				top.add(t5);
				top.add(new Label("Phone"));
				t6 = new TextField(m.phone, 12);
				top.add(t6);
				top.add(new Label("Email"));
				t7 = new TextField(m.email, 20);
				top.add(t7);
				top.add(new Label("Address 1"));
				t8=new TextField(m.address1,20);
				top.add(t8);
				top.add(new Label("Address 2 (City,State)"));
				t9=new TextField(m.address2,20);
				top.add(t9);
				top.add(new Label("Court"));
				t10 = new TextField(m.court, 20);
				top.add(t10);
				top.add(new Label("Case No"));
				t11 = new TextField(m.casenum, 20);
				top.add(t11);
				top.add(new Label("Other Party"));
				t12 = new TextField(m.otr_party, 20);
				top.add(t12);
				top.add(new Label("Other Attorney"));
				t13 = new TextField(m.otr_atty, 20);
				top.add(t13);
				top.add(new Label("Open"));
				if (oid==null) {
					m.open=true;
				}
				cb1 = new Checkbox("open",m.open);
				top.add(cb1);

				center.add(new Label("Notes"));
				t14 = new TextArea(m.desc,3,40);
				center.add(t14);

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

				Matter m=new Matter();
				m.mattername=t1.getText();
				m.type=t2.getText();
				m.date_opened=t3.getText();
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
				m.open=cb1.getState();
				m.desc=t14.getText();
				//get rid of newline by replacing it with a space
				m.desc = m.desc.replaceAll("\\r?\\n"," ");
				//System.out.println(m.desc);

				if (cmd.equals("Add")) {
 					try {
						String k=ds.insert(m);
						System.out.println("insert successful, key="+k);
						//status.setText("inserted matter, key="+k);
					} catch (Exception x) {
						x.printStackTrace();
					}
				} else if (cmd.equals("Update")) {
					try {
						//update this in database
						ds.update(oid,m);
						System.out.println("update successful, key="+oid);
						//status.setText("inserted matter, key="+k);
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				this.dispose();
			}
}
