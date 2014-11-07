package example.popularItems.gui.menubar;

import java.awt.Event;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import example.popularItems.UserInputs;
import example.popularItems.exceptions.InvalidInputException;
import example.popularItems.exceptions.MinMaxException;
import example.popularItems.gui.ProFIDMainVisual;
import example.popularItems.gui.toolbar.BrowserToolBar;
import example.popularItems.gui.toolbar.ToolBarButton;

import peersim.Simulator;
import peersim.config.Configuration;
import peersim.config.ParsedProperties;

public class CustomMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static CustomMenuBar menuBar;
	private static final long serialVersionUID = -1368734783423185957L;
	
	private JMenu fileMenu;
	private JMenuItem simulateFromConfigMenuItem;
	private JMenuItem simulateMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;
	
	private JMenu helpMenu;

	private JMenu editMenu;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;

	public static CustomMenuBar getInstance() {
		if (menuBar == null)
			return menuBar = new CustomMenuBar();
		else
			return menuBar;
	}

	private CustomMenuBar() {
		add(getFileMenu());
		add(getEditMenu());
		add(getHelpMenu());
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getSimulateMenuItem());
			fileMenu.add(getSimulateFromConfigMenuItem());
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	private JMenuItem getSimulateFromConfigMenuItem() {

		if (simulateFromConfigMenuItem == null) {
			simulateFromConfigMenuItem = new JMenuItem();
			simulateFromConfigMenuItem.setText("Simulate From File");
			simulateFromConfigMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser(".");
					int returnVal = chooser
							.showOpenDialog(simulateFromConfigMenuItem);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						try {
							UserInputs.configFile = chooser.getSelectedFile();
							Configuration
									.setConfig(new ParsedProperties(UserInputs.configFile.getAbsolutePath()));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(
									simulateFromConfigMenuItem,
									"Config file can be set only once.");
						}
						UserInputs.runFromManualConfig = true;

						runSimulation();
					}
				}
			});
		}
		return simulateFromConfigMenuItem;
	}

	/**
	 * This method initializes Simulate
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSimulateMenuItem() {
		if (simulateMenuItem == null) {
			simulateMenuItem = new JMenuItem();
			simulateMenuItem.setText("Simulate");
			simulateMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runSimulation();
				}

			});
		}
		return simulateMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new HelpMenu();
		}
		return helpMenu;
	}

	@SuppressWarnings("finally")
	public boolean runSimulation() {
		boolean succesful = false;
		try {
			Simulator.runSimulation();
			succesful = true;
			updateGUI();
		} catch (RuntimeException ex) {
			JOptionPane
					.showMessageDialog(
							this,
							"You can not run the simulation twice in this version. \r\nPlease restart the program.",
							null, JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(
							this,
							"Please make sure that save directory exists \r\n or you have write permission on that directory",
							null, JOptionPane.ERROR_MESSAGE);
		} catch (InvalidInputException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), null,
					JOptionPane.ERROR_MESSAGE);
		} catch (MinMaxException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), null,
					JOptionPane.ERROR_MESSAGE);
		} finally {
			return succesful;
		}
	}

	private void updateGUI() throws IOException {
		fileMenu.remove(simulateMenuItem);
		fileMenu.remove(simulateFromConfigMenuItem);
		ToolBarButton button = new ToolBarButton("/img/exit.png");
		button.setToolTipText("Exit");
		button.setMargin(new Insets(0, 0, 0, 0));
		BrowserToolBar.getInstance().remove(BrowserToolBar.getRunButton());
		BrowserToolBar.getInstance().add(button);
		
		ProFIDMainVisual.getInstance().validate();
		ProFIDMainVisual.getInstance().repaint();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}

}
