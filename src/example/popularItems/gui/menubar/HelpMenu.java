package example.popularItems.gui.menubar;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import example.popularItems.gui.ProFIDMainVisual;

public class HelpMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1001508188517883947L;

	private JMenuItem aboutMenuItem;
	private JMenuItem howToMenuItem;

	public HelpMenu() {
		setText("Help");
		add(getAboutMenuItem());
		add(getHowToMenuItem());
	}

	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = ProFIDMainVisual.getInstance().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {

		JDialog aboutDialog = new JDialog(ProFIDMainVisual.getInstance(), true);
		aboutDialog.setTitle("About");
		aboutDialog.setSize(new Dimension(200, 98));
		aboutDialog.setPreferredSize(new Dimension(200, 100));
		aboutDialog.setContentPane(getAboutContentPane());
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {

		JPanel aboutContentPane = new JPanel();
		aboutContentPane.setLayout(new FlowLayout());
		aboutContentPane.add(getTxtAbout(), null);
		return aboutContentPane;
	}

	/**
	 * This method initializes txtAbout
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getTxtAbout() {

		JTextArea txtAbout = new JTextArea();
		txtAbout.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		txtAbout.setEditable(false);
		txtAbout.setText("\u00a9 2011 ProFID\r\nby Emrah Cem");
		return txtAbout;
	}

	/**
	 * This method initializes howToMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getHowToMenuItem() {
		if (howToMenuItem == null) {
			howToMenuItem = new JMenuItem("How-to");
			howToMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog howToDialog = getHowToDialog();
					howToDialog.pack();
					Point loc = ProFIDMainVisual.getInstance().getLocation();
					loc.translate(20, 20);
					howToDialog.setLocation(loc);
					howToDialog.setVisible(true);
				}
			});
		}
		return howToMenuItem;
	}

	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getHowToDialog() {

		JDialog howToDialog = new JDialog(ProFIDMainVisual.getInstance(), "How-to");
		howToDialog.setSize(new Dimension(650, 700));
		howToDialog.setPreferredSize(new Dimension(650, 700));
		howToDialog.setContentPane(getHowToContentPane());
		howToDialog.setResizable(false);

		return howToDialog;
	}

	/**
	 * This method initializes howToContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getHowToContentPane() {

		JPanel howToContentPane = new JPanel();
		howToContentPane.setLayout(null);
		howToContentPane.add(getHowToScrollPane(), null);
		return howToContentPane;
	}

	/**
	 * This method initializes howToScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getHowToScrollPane() {

		JScrollPane howToScrollPane = new JScrollPane();
		howToScrollPane.setBounds(new Rectangle(7, 6, 621, 649));
		howToScrollPane.setViewportView(getHowToTextArea());

		return howToScrollPane;
	}

	/**
	 * This method initializes howToTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getHowToTextArea() {
		JTextArea howToTextArea = new JTextArea();
		howToTextArea.setLineWrap(true);
		howToTextArea
				.append("----------------------------------------------------------------------------------------------------------\n");
		howToTextArea.append("\n");
		howToTextArea.append("NETWORK PARAMS\n");

		howToTextArea.append(" - Number of experiments\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea
				.append("   Number of experiments to be performed. In each experiment, same overlay is used, but initial\n");
		howToTextArea.append("   states of peers change.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Network size\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea.append("   Number of peers in the network.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Topology Type\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea.append("         WireKOut\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         Each peer chooses \"k\" random peers to connect to among all peers.\n");
		howToTextArea.append("\n");
		howToTextArea.append("         Erdos-Renyi\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         An edge between each pair of nodes with equal probability \"p\" is set, independently\n");
		howToTextArea.append("         of the other edges.\n");
		howToTextArea.append("\n");
		howToTextArea.append("         Barabasi-Albert\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         Algorithm for generating random scale-free networks using a preferential attachment\n");
		howToTextArea
				.append("         mechanism. Each new node is connected to \"k\" existing nodes with a probability that\n");
		howToTextArea
				.append("         is proportional to the number of links that the existing nodes already have.\n");
		howToTextArea.append("\n");
		howToTextArea.append("         Watts&Strogatz\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         The Watts and Strogatz model is a random graph generation model that produces graphs\n");
		howToTextArea
				.append("         with small-world properties, including short average path lengths and high clustering.\n");
		howToTextArea
				.append("         It has two parameters: \"beta\" is the probability for a node to be rewired, \"k\" is the\n");
		howToTextArea.append("         mean degree.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Number of items\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea.append("   Number of distinct items in the network.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Item frequency distribution\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea.append("         Uniform\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         Item frequencies are uniformly distributed in the range [Min frequency,Max frequency].\n");
		howToTextArea.append("\n");
		howToTextArea.append("         Zipf\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         Item frequencies are distributed using zipf distribution with \"Skew\" parameter in the\n");
		howToTextArea
				.append("         range [Min frequency,Max frequency].Implementation details can be found in\n");
		howToTextArea
				.append("         http://diveintodata.org/2009/09/zipf-distribution-generator-in-java/.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Distribution of items to peers\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea.append("\n");
		howToTextArea.append("         Uniform\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         Items are distributed to peers uniformly.\n");
		howToTextArea.append("\n");
		howToTextArea.append("         Power-law\n");
		howToTextArea.append("         ------------------------\n");
		howToTextArea
				.append("         Items are distributed to peers using a power-law distribution with parameter\n");
		howToTextArea
				.append("         \"Distribution power (n)\". Details of how to produce power-law distribution can be\n");
		howToTextArea
				.append("         found in http://mathworld.wolfram.com/RandomNumber.html\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Link drop probability\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea
				.append("   Each message on a link is lost with this percentage.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Minimum delay (in ms)\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea
				.append("   Minimum delay to send a message between any pairs of neighbors.\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Maximum delay (in ms)\n");
		howToTextArea.append("   ------------------------------ \n");
		howToTextArea
				.append("   Maximum delay to send a message between any pairs of neighbors.\n");
		howToTextArea.append("\n");
		howToTextArea
				.append("-----------------------------------------------------------------------------------------------------------\n");
		howToTextArea.append("\n");
		howToTextArea.append("PROFID PARAMS\n");
		howToTextArea
				.append("Details can be found in https://sites.google.com/a/ku.edu.tr/emrah-cem/publications/EmrahCemMSThesis.pdf\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Write Trace File\n");
		howToTextArea.append("   ------------------------------ \n");
		howToTextArea
				.append("   Writes a file which keeps events during simulations \n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Write Initial States File\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea
				.append("   Writes a file which keeps initial states of peers such as local item frequencies, it also keeps the\n");
		howToTextArea.append("   global frequencies of items\n");
		howToTextArea.append("\n");
		howToTextArea.append(" - Write Neighbor List File\n");
		howToTextArea.append("   ------------------------------\n");
		howToTextArea
				.append("   Writes a file which keeps the neighborhood information.\n");
		howToTextArea.append("\n");
		howToTextArea
				.append("------------------------------------------------------------------------------------------------------------\n");
		howToTextArea.append("\n");
		howToTextArea.append("How to run a sample simulation\n");
		howToTextArea.append("\n");
		howToTextArea
				.append("After setting all parameters, click File ->Simulate. You can see the simulation progress in the\n");
		howToTextArea
				.append("application console. When the simulation ends, result files are written to a folder in the same\n");
		howToTextArea.append("directory with the runnable .jar file.\n");
		howToTextArea
				.append("------------------------------------------------------------------------------------------------------------\n");
		howToTextArea.append("\n");
		howToTextArea.append("Which file keeps what?\n");
		howToTextArea.append("\n");
		howToTextArea
				.append("First of all, at the end of  the simulation, a root folder is created named like\n");
		howToTextArea
				.append("\"p100i100min1max100c10e10d0min2max100f1t50delta0v1\", this folder name eases to figure out how the\n");
		howToTextArea
				.append("simulation paramters were set to get results. Inside that root folder, a seperate folder is created for\n");
		howToTextArea
				.append("each experiment (e.g experiment0, experiment1,...). Moreover, configFile.txt which is given as input to\n");
		howToTextArea
				.append("peerSim exists under the root directory. There is also neighborList.txt file which keeps neighborhood\n");
		howToTextArea
				.append("relationship. Each line starts with the id of a peer and the rest of the line is the ids of the neighbors\n");
		howToTextArea
				.append("of this peer.Finally, there is output.txt which keeps overall results such as convergence time, total\n");
		howToTextArea.append("number of messages used until convergence.\n");
		howToTextArea
				.append("------------------------------------------------------------------------------------------------------------");

		return howToTextArea;
	}
}
