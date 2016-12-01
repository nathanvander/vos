package vos.core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Iterator;
import java.io.*;
import vos.db.*;

public class Vos extends Frame implements ActionListener {
	public static String version="2.0-beta1";
	DataStore ds;
	Label status;
	JEditorPane desktop;
	String mid=null;  //matterid

	//---------------------------
	//subroutines
	public static String getDate() {
		return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
	}

	public static String getTime() {
		return new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
	}

	public static String getHeader(String title) {
		return "<head><title>"+title+"</title><meta name=\"app\" content=\"VOS ver. "+version+"\"></head>";
	}

	//--------------------------

	public Vos() {
		super("Vos");

		try {
			ds=new SimpleDB("vos.db");
		} catch (DSX x) {
			System.out.println(x);
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
		//get a list of all matters
		Iterator it=ds.selectAll("vos.core.Matter");
		if (it==null) {
			System.out.println("Warning: no matters to display");
			return;
		}

		//create page
		StringBuffer sb=new StringBuffer();
		sb.append(getHeader("View Matters"));
		sb.append("<table border=1><tr><th>Key</th><th>MatterName</th></tr>");
		while (it.hasNext()) {
			Pair e=(Pair)it.next();
			Matter m=(Matter)e.value;
			sb.append("<tr><td>"+e.key+"</td><td>"+m.mattername+"</td></tr>");
		}
		sb.append("</table>");
		//display it
		desktop.setText(sb.toString());
	}

	public void viewActivities() {
		//first, get header
		Matter m=(Matter)ds.get(mid);

		StringBuffer sb=new StringBuffer();
		sb.append("<html>Matter name: "+m.mattername+"<br>");
		sb.append("Client name: "+m.firstname+" "+m.lastname+"<br>");

		//get a list of all activities
		Iterator it=ds.selectAll("vos.core.Activity");

		sb.append("<table border=1><tr><th>Key</th><th>Activity</th><th>Type</th><th>Date</th><th>Description</th><th>Time</th><th>Complete</th></tr>");
		//System.out.println("Debug: rs.size()"+rs.size());
		while (it.hasNext()) {
			Pair e=(Pair)it.next();
			Activity a=(Activity)e.value;
			//System.out.println("Debug: mid"+a.mid);
			if (a.mid.equals(mid)) {
				sb.append("<tr><td>"+e.key+"</td><td>"+a.name+"</td><td>"+a.type+"</td><td>"+a.date+"</td><td>"+a.desc+"</td><td>"+a.elapsed+"</td><td>"+a.complete+"</td></tr>");
			}

		}

		sb.append("</table></html>");
		//display it
		desktop.setText(sb.toString());
	}

	public void eventsReport() {
		StringBuffer sb=new StringBuffer();
		sb.append("<html><b>Events</b><br>");

		//get a list of all activities
		Iterator it=ds.selectAll("vos.core.Activity");

		sb.append("<table border=1><tr><th>Key</th><th>Matter</th><th>Date</th><th>Time</th><th>Name</th><th>Details</th></tr>");
		//System.out.println("Debug: rs.size()"+rs.size());
		while (it.hasNext()) {
			Pair e=(Pair)it.next();
			Activity a=(Activity)e.value;
			Matter m=(Matter)ds.get(a.mid);

			//System.out.println("Debug: mid"+a.mid);
			if (a.type.equals("Event") && a.complete==false) {
				sb.append("<tr><td>"+e.key+"</td><td>"+m.mattername+"</td><td>"+a.date+"</td><td>"+a.time+"</td><td>"+a.name+"</td><td>"+a.desc+"</td></tr>");
			}

		}

		sb.append("</table></html>");
		//display it
		desktop.setText(sb.toString());
	}

	public void tasksReport() {
		StringBuffer sb=new StringBuffer();
		sb.append("<html><b>Tasks</b><br>");

		//get a list of all activities
		Iterator it=ds.selectAll("vos.core.Activity");

		sb.append("<table border=1><tr><th>Key</th><th>Matter</th><th>Date</th><th>Name</th><th>Details</th><th>Deadline</th></tr>");
		//System.out.println("Debug: rs.size()"+rs.size());
		while (it.hasNext()) {
			Pair e=(Pair)it.next();
			Activity a=(Activity)e.value;
			Matter m=(Matter)ds.get(a.mid);
			//System.out.println("Debug: mid"+a.mid);
			if (a.type.equals("Task") && a.complete==false) {
				sb.append("<tr><td>"+e.key+"</td><td>"+m.mattername+"</td><td>"+a.date+"</td><td>"+a.name+"</td><td>"+a.desc+"</td><td>"+a.deadline+"</td></tr>");
			}

		}

		sb.append("</table></html>");
		//display it
		desktop.setText(sb.toString());
	}

	public void about() {
		StringBuffer sb=new StringBuffer();
		sb.append("<html><table border=1><tr><th>VOS ver. "+version+"<br>Copyright 2013 Nathan Vanderhoofven</th></tr></table></html>");
		desktop.setText(sb.toString());
	}

	public void saveFile(String text) {
		FileDialog fd=new FileDialog(this,"Save Desktop",FileDialog.SAVE);
		fd.setFile("*.html");
		fd.show();
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
			//get list of matters
			Iterator it=ds.selectAll("vos.core.Matter");
			if (it==null) {
				System.out.println("Warning: no matters to display");
				return;
			}

			//System.out.println("populating list");
			while (it.hasNext()) {
				Pair e=(Pair)it.next();
				Matter m=(Matter)e.value;
				if (m.open) {
					String s=e.key+": "+m.mattername;
					list.add(s);
				}
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
					ds.delete(oid);
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
			//get list of activities
			Iterator it=ds.selectAll("vos.core.Activity");

			while (it.hasNext()) {
				Pair e=(Pair)it.next();
				Activity a=(Activity)e.value;
				//System.out.println("Debug: mid"+a.mid);
				if (a.mid.equals(matterId)) {
					String s=e.key+": "+a.name;
					list.add(s);
				}
			}
		}
	}

	//------------------------------------------------
	public static void main(String[] args) {
		new Vos();
	}

}