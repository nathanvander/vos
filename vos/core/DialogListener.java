package vos.core;
import java.awt.*;
import java.awt.event.*;
public class DialogListener extends WindowAdapter {
		Window w;
		public DialogListener(Window w) {
			this.w=w;
		}
		public void windowClosing(WindowEvent evt) {
			System.out.println("dialog window closing");
			w.dispose();
		}
}