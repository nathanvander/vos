package vos2.core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Iterator;
import java.io.*;
import apollo.iface.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Vos extends Frame implements ActionListener {
	public static String version="2.0";
	DataStore ds;
	Label status;
	JEditorPane desktop;
	String mid=null;  //matterid

	//---------------------------
	//subroutines
	public static String getHeader(String title) {
		return "<head><title>"+title+"</title><meta name=\"app\" content=\"VOS ver. "+version+"\"></head>";
	}

	//--------------------------

	public Vos(DataStore ds) {
		super("Vos");
		this.ds=ds;

		//create new tables for activity and matter
		try {
		Transaction tx=ds.createTransaction();
		System.out.println("creating Matter and Activity tables");
		tx.begin();
		tx.createTable(new Matter());
		tx.createTable(new Activity());
		tx.commit();
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}

		setSize(400, 300);
		addWindowListener(new Listener());
		setLayout(new BorderLayout());

		MenuBar menuBar=new MenuBar();
		setMenuBar(menuBar);

		Menu fileMenu=new Menu("File");
		menuBar.add(fileMenu);
		MenuItem saveFile=new MenuItem("Save File");
		fileMenu.add(saveFile);
		saveFile.addActionListener(this);

		//Matters menu
		Menu mattersMenu=new Menu("Matters");
		menuBar.add(mattersMenu);
		MenuItem viewMattersItem=new MenuItem("View Matters");
		MenuItem selectMatterItem=new MenuItem("Select Matter");
		MenuItem editMatterItem=new MenuItem("Edit Matter");
		MenuItem addMatterItem=new MenuItem("Add Matter");
		MenuItem deleteMatterItem=new MenuItem("Delete Matter");
		viewMattersItem.addActionListener(this);
		selectMatterItem.addActionListener(this);
		editMatterItem.addActionListener(this);
		addMatterItem.addActionListener(this);
		deleteMatterItem.addActionListener(this);
		mattersMenu.add(viewMattersItem);
		mattersMenu.add(selectMatterItem);
		mattersMenu.add(editMatterItem);
		mattersMenu.add(addMatterItem);
		mattersMenu.add(deleteMatterItem);

		//--------------------------------------------------------
		//Activities menu
		Menu actsMenu=new Menu("Activities");
		menuBar.add(actsMenu);

		//these aren't all the activies, just those of the selected matter
		MenuItem viewActsItem=new MenuItem("View Activities");
		MenuItem addActivityItem=new MenuItem("Add Activity");
		MenuItem editActivityItem=new MenuItem("Edit Activity");
		viewActsItem.addActionListener(this);
		addActivityItem.addActionListener(this);
		editActivityItem.addActionListener(this);
		actsMenu.add(viewActsItem);
		actsMenu.add(addActivityItem);
		actsMenu.add(editActivityItem);

		//Reports menu
		Menu reportsMenu=new Menu("Reports");
		menuBar.add(reportsMenu);
		MenuItem eventsItem=new MenuItem("Events");
		eventsItem.addActionListener(this);
		reportsMenu.add(eventsItem);
		MenuItem tasksItem=new MenuItem("Tasks");
		tasksItem.addActionListener(this);
		reportsMenu.add(tasksItem);
		MenuItem aboutItem=new MenuItem("About");
		aboutItem.addActionListener(this);
		reportsMenu.add(aboutItem);

		ScrollPane pane=new ScrollPane();
		add(pane, BorderLayout.CENTER);

		//add desktop
		desktop=new JEditorPane();
		desktop.setContentType("text/html");
		desktop.setEditable(false);
		desktop.setText(getHeader("Welcome")+"Welcome");
		pane.add(desktop);

		//add status
		status=new Label();
		status.setPreferredSize(new Dimension(100, 16));
		status.setText("Ready");
		add(status, BorderLayout.SOUTH);

		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		System.out.println(cmd);
		if (cmd.equals("Add Matter")) {
			new MatterDialog(this,ds,null);
		} else if (cmd.equals("View Matters")) {
			viewMatters();
		} else if (cmd.equals("Select Matter")) {
			new SelectMatterDialog(this);
		} else if (cmd.equals("Edit Matter")) {
			if (mid!=null) {
				new MatterDialog(this,ds,mid);
			}
		} else if (cmd.equals("Delete Matter")) {
			if (mid!=null) {
				new DeleteMatterDialog(this,ds,mid);
			}
		} else if (cmd.equals("Add Activity")) {
			if (mid!=null) {
				new ActivityDialog(this,ds,mid,null);
			}
		} else if (cmd.equals("View Activities")) {
			if (mid!=null) {
				viewActivities();
			}
		} else if (cmd.equals("Edit Activity")) {
			if (mid!=null) {
				new SelectActivityDialog(this,ds,mid);
			}
		} else if (cmd.equals("Events")) {
			eventsReport();
		} else if (cmd.equals("Tasks")) {
			tasksReport();
		} else if (cmd.equals("About")) {
			about();
		} else if (cmd.equals("Save File")) {
			saveFile(desktop.getText());
		}
	}

	//get matter id, might be null
	public String getMid() {
		return mid;
	}

	//===================================================
	public void viewMatters() {
		//create page
		StringBuffer sb=new StringBuffer();
		sb.append(getHeader("View Matters"));

		//get a list of all matters
		try {
		apollo.iface.Cursor it=ds.selectAll(new Matter());
		it.open();

		sb.append("<table border=1><tr><th>Key</th><th>MatterName</th></tr>");
		while (it.hasNext()) {
			Matter m=(Matter)it.next();
			sb.append("<tr><td>"+m.getKey()+"</td><td>"+m.mattername+"</td></tr>");
		}
		it.close();
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}
		sb.append("</table>");
		//display it
		desktop.setText(sb.toString());
	}

	public void viewActivities() {
		//first, get header
		StringBuffer sb=new StringBuffer();

		try {
		Matter m=(Matter)ds.get(new Key("Matter",mid));

		sb.append("<html>Matter name: "+m.mattername+"<br>");
		sb.append("Client name: "+m.firstname+" "+m.lastname+"<br>");

		//get a list of all activities
		apollo.iface.Cursor it=ds.selectAll(new Activity());
		it.open();

		sb.append("<table border=1><tr><th>Key</th><th>Activity</th><th>Type</th><th>Date</th><th>Description</th><th>Time</th><th>Complete</th></tr>");
		//System.out.println("Debug: rs.size()"+rs.size());
		while (it.hasNext()) {
			Activity a=(Activity)it.next();
			if (a.mid.equals(mid)) {
				sb.append("<tr><td>"+a.getKey()+"</td><td>"+a.name+"</td><td>"+a.type+"</td><td>"+a.date+"</td><td>"+a.desc+"</td><td>"+a.elapsed+"</td><td>"+a.complete+"</td></tr>");
			}
		}
		it.close();
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}
		sb.append("</table></html>");
		//display it
		desktop.setText(sb.toString());
	}

	public void eventsReport() {
		StringBuffer sb=new StringBuffer();
		sb.append("<html><b>Events</b><br>");

		//get a list of all activities
		try {
		apollo.iface.Cursor it=ds.selectAll(new Activity());
		it.open();

		sb.append("<table border=1><tr><th>Key</th><th>Matter</th><th>Date</th><th>Time</th><th>Name</th><th>Details</th></tr>");
		//System.out.println("Debug: rs.size()"+rs.size());
		while (it.hasNext()) {
			Activity a=(Activity)it.next();
			Matter m=(Matter)ds.get(new Key("Matter",a.mid));

			//System.out.println("Debug: mid"+a.mid);
			if (a.type.equalsIgnoreCase("EVENT") && a.complete==false) {
				sb.append("<tr><td>"+a.getKey()+"</td><td>"+m.mattername+"</td><td>"+a.date+"</td><td>"+a.time+"</td><td>"+a.name+"</td><td>"+a.desc+"</td></tr>");
			}
		}
		it.close();
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}
		sb.append("</table></html>");
		//display it
		desktop.setText(sb.toString());
	}

	public void tasksReport() {
		StringBuffer sb=new StringBuffer();
		sb.append("<html><b>Tasks</b><br>");

		try {
		//get a list of all activities
		apollo.iface.Cursor it=ds.selectAll(new Activity());
		it.open();

		sb.append("<table border=1><tr><th>Key</th><th>Matter</th><th>Date</th><th>Name</th><th>Details</th><th>Deadline</th></tr>");
		//System.out.println("Debug: rs.size()"+rs.size());
		while (it.hasNext()) {
			Activity a=(Activity)it.next();
			Matter m=(Matter)ds.get(new Key("Matter",a.mid));
			//System.out.println("Debug: mid"+a.mid);
			if (a.type.equalsIgnoreCase("TASK") && a.complete==false) {
				sb.append("<tr><td>"+a.getKey()+"</td><td>"+m.mattername+"</td><td>"+a.date+"</td><td>"+a.name+"</td><td>"+a.desc+"</td><td>"+a.deadline+"</td></tr>");
			}
		}
		it.close();
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}

		sb.append("</table></html>");
		//display it
		desktop.setText(sb.toString());
	}

	public void about() {
		StringBuffer sb=new StringBuffer();
		sb.append("<html><table border=1><tr><th>VOS ver. "+version+"<br>Copyright 2016 Nathan Vanderhoofven</th></tr></table></html>");
		desktop.setText(sb.toString());
	}

	//saves the desktop.  this is because we don't have an ability to print
	public void saveFile(String text) {
		FileDialog fd=new FileDialog(this,"Save Desktop",FileDialog.SAVE);
		fd.setFile("*.html");
		fd.setVisible(true);
		String fn= fd.getFile();
		String dir=fd.getDirectory();
		String fp=dir+fn;

			System.out.println("filename="+fp);
			if (fp!=null && fp.endsWith(".html")) {
				try {
					File f=new File(fp);
					if (!f.exists()) {
						f.createNewFile();
					}
					FileWriter fw=new FileWriter(f);  //open in append mode
					BufferedWriter bw=new BufferedWriter(fw);
					bw.write(text);
					bw.flush();
					fw.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
	}

	//====================================================
	class Listener extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			System.out.println("window closing");
			System.exit(0);
		}
	}

	//------------------------------------------------
	class SelectMatterDialog extends Dialog implements ActionListener {
		List list;

		public SelectMatterDialog(Frame f) {
			super(f,"Select Matter Dialog");
			this.setSize(350,300);
			addWindowListener(new DialogListener(this));
			setLayout(new BorderLayout());

			Panel top=new Panel(new FlowLayout());
			add(top,BorderLayout.NORTH);
			list=new List(10);
			populateList();
			top.add(list);

			Panel south=new Panel(new FlowLayout());
			add(south,BorderLayout.SOUTH);

			Button bp=new Button("Select");
			bp.addActionListener(this);
			south.add(bp);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			System.out.println(cmd);
			String selected=list.getSelectedItem();
			System.out.println("selected="+selected);

			if (cmd.equals("Select")) {
				if (selected!=null) {
					String[] sa=selected.split(":");
					//int i=Integer.parseInt(sa[0]);
					status.setText(selected);
					mid=sa[0];
				}
			}
			this.dispose();
		}

		private void populateList() {
			try {
			//get list of matters
			apollo.iface.Cursor it=ds.selectAll(new Matter());
			it.open();

			//System.out.println("populating list");
			while (it.hasNext()) {
				Matter m=(Matter)it.next();
				if (m.open) {
					String s=m.getKey()+": "+m.mattername;
					list.add(s);
				}
			}
			it.close();

			} catch (Exception x) {
				System.out.println(x.getMessage());
			}
		}
	}

	//------------------------------------------------
	class DeleteMatterDialog extends Dialog implements ActionListener {
		DataStore ds;
		String oid;

		public DeleteMatterDialog(Frame f,DataStore ds,String oid) {
			super(f,"Delete Matter Dialog");
			this.oid=oid;
			this.ds=ds;

			this.setSize(250,150);
			addWindowListener(new DialogListener(this));
			setLayout(new BorderLayout());

			Panel top=new Panel(new GridLayout(3,1));
			add(top,BorderLayout.NORTH);

			top.add(new Label("Are you sure you want to delete this?"));
			top.add(new Label("Record# "+oid));

			Panel south=new Panel(new FlowLayout());
			add(south,BorderLayout.SOUTH);

			Button bp=new Button("Yes");
			bp.addActionListener(this);
			south.add(bp);
			Button bpNo=new Button("No");
			bpNo.addActionListener(this);
			south.add(bpNo);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			System.out.println(cmd);
			if (cmd.equals("Yes")) {
				//delete from database
				try {
					Transaction tx=ds.createTransaction();
					tx.begin();
					tx.delete(new Key("Matter",oid));
					tx.commit();
				} catch (Exception x) {
					x.printStackTrace();
				}

				//reset status
				status.setText("Ready");
				this.dispose();

			} else {
				this.dispose();
			}
		}
	}

	//------------------------------------------------
	class SelectActivityDialog extends Dialog implements ActionListener {
		List list;
		DataStore ds;
		String matterId;

		public SelectActivityDialog(Frame f,DataStore ds,String mid) {
			super(f,"Select Activity Dialog");
			this.setSize(350,300);
			this.ds=ds;
			this.matterId=mid;

			addWindowListener(new DialogListener(this));
			setLayout(new BorderLayout());

			Panel top=new Panel(new FlowLayout());
			add(top,BorderLayout.NORTH);
			list=new List(10);
			populateList();
			top.add(list);

			Panel south=new Panel(new FlowLayout());
			add(south,BorderLayout.SOUTH);

			Button bp=new Button("Select");
			bp.addActionListener(this);
			south.add(bp);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			System.out.println(cmd);
			String selected=list.getSelectedItem();
			System.out.println("selected="+selected);

			if (cmd.equals("Select")) {
				if (selected!=null) {
					String[] sa=selected.split(":");
					String oid=sa[0];
					//int oid=Integer.parseInt(sa[0]);
					new ActivityDialog(getOwner(),ds,matterId,oid);
				}
			}
			this.dispose();
		}

		private void populateList() {
		try {
			//get list of activities
			apollo.iface.Cursor it=ds.selectAll(new Activity());
			it.open();

			while (it.hasNext()) {
				Activity a=(Activity)it.next();
				//System.out.println("Debug: mid"+a.mid);
				if (a.mid.equals(matterId)) {
					String s=a.getKey()+": "+a.name;
					list.add(s);
				}
			}
			it.close();
		} catch (Exception x) {
			System.out.println(x.getMessage());
		}
		}
	}

	//------------------------------------------------
	public static void main(String[] args) {
		String host=args[0];
		if (host==null) {host="localhost";}
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            DataStore ds = (DataStore) registry.lookup("DataStore");

			new Vos(ds);
        } catch (Exception e) {
            System.err.println("exception: " + e.toString());
        }
	}

}