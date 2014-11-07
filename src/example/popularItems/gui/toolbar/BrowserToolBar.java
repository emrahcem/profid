package example.popularItems.gui.toolbar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import example.popularItems.gui.menubar.CustomMenuBar;

/**
 * Part of a small example showing basic use of JToolBar. Creates a small
 * dockable toolbar that is supposed to look vaguely like one that might come
 * with a Web browser. Makes use of ToolBarButton, a small extension of JButton
 * that shrinks the margins around the icon and puts text label, if any, below
 * the icon. 1999 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 */

public class BrowserToolBar extends JToolBar {
	/**
	 * 
	 */
	private static BrowserToolBar browserToolBar;
	private static final long serialVersionUID = -5001616108413421738L;
	private static ToolBarButton runButton;

	public static BrowserToolBar getInstance() throws IOException {
		if (browserToolBar == null)
			return browserToolBar = new BrowserToolBar();
		else
			return browserToolBar;
	}

	private BrowserToolBar() throws IOException {

		Insets margins = new Insets(0, 0, 0, 0);
		ToolBarButton button = new ToolBarButton("/img/save.png");
		button.setToolTipText("Save");
		button.setMargin(margins);
		add(button);

		button = new ToolBarButton("/img/saveas.png");
		button.setToolTipText("Save as");
		button.setMargin(margins);
		add(button);

		button = new ToolBarButton("/img/import.png");
		button.setToolTipText("Import");
		button.setMargin(margins);
		add(button);

		button = new ToolBarButton("/img/export.png");
		button.setToolTipText("Export");
		button.setMargin(margins);
		add(button);

		runButton = new ToolBarButton("/img/run.png");
		runButton.setToolTipText("Run");
		runButton.setMargin(margins);
		add(runButton);

		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CustomMenuBar.getInstance().runSimulation();
			}
		});
	}

	public static ToolBarButton getRunButton() {
		return runButton;
	}
}
