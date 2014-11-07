package example.popularItems.gui.content;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class ContentPanel extends JPanel {

	/**
	 * 
	 */
	private static ContentPanel contentPanel;
	private static final long serialVersionUID = 4380114214068794301L;

	private JSplitPane splitPane;
	private JTabbedPane paramsTabbedPane;
	private JScrollPane resultScrollPane;

	public static JTextArea console;

	public static ContentPanel getInstance() {
		if (contentPanel == null)
			return contentPanel = new ContentPanel();
		else
			return contentPanel;
	}

	private ContentPanel() {
		setLayout(new BorderLayout());
		add(getJSplitPane(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			splitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
			splitPane.setDividerLocation(500);
			splitPane.setOneTouchExpandable(true);
			splitPane.setLeftComponent(getContentJTabbedPane());
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setBottomComponent(getResultScrollPane());
			splitPane.setEnabled(false);
		}
		return splitPane;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getContentJTabbedPane() {
		if (paramsTabbedPane == null) {
			paramsTabbedPane = new JTabbedPane();
			paramsTabbedPane.addTab("Network Params", null,
					getNetworkParamsPanel(), null);
			paramsTabbedPane.addTab("ProFID Params", null,
					getAlgoParamsPanel(), null);
		}
		return paramsTabbedPane;
	}

	/**
	 * This method initializes NetworkParamsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public NetworkParamsPanel getNetworkParamsPanel() {
		return NetworkParamsPanel.getInstance();
	}

	/**
	 * This method initializes AlgoParamsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public AlgoParamsPanel getAlgoParamsPanel() {

		return AlgoParamsPanel.getInstance();

	}

	/**
	 * This method initializes resultScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getResultScrollPane() {
		if (resultScrollPane == null) {
			resultScrollPane = new JScrollPane();
			resultScrollPane.setAutoscrolls(true);
			resultScrollPane.setViewportView(getConsole());
			resultScrollPane.getVerticalScrollBar().addAdjustmentListener(
					new AdjustmentListener() {

						@Override
						public void adjustmentValueChanged(AdjustmentEvent arg0) {
							console.setCaretPosition(console.getDocument()
									.getLength());
						}
					});
		}
		return resultScrollPane;
	}

	/**
	 * This method initializes resultTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getConsole() {
		if (console == null) {
			console = new JTextArea();
			console.setEditable(false);
			console.setBorder(BorderFactory.createTitledBorder(null, "Console",
					TitledBorder.LEFT, TitledBorder.TOP, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return console;
	}
}
