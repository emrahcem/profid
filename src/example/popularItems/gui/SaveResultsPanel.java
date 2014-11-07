package example.popularItems.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import example.popularItems.gui.toolbar.ToolBarButton;

public class SaveResultsPanel extends JPanel {
	/**
	 * 
	 */
	private static SaveResultsPanel saveResultsPanel;
	private static final long serialVersionUID = 1L;
	private JTextField txtSaveTo;

	public void setTxtSaveTo(JTextField txtSaveTo) {
		this.txtSaveTo = txtSaveTo;
	}
	
	public static SaveResultsPanel getInstance() throws IOException{
		if(saveResultsPanel == null)
			return saveResultsPanel= new SaveResultsPanel();
		else
			return saveResultsPanel;
	}

	private SaveResultsPanel() throws IOException {
		super();
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(new JLabel("Save results to:"));
		add(getTxtSaveTo());
		JButton btnsaveTo = new ToolBarButton("/img/saveTo.png");
		btnsaveTo.setToolTipText("Choose directory");
		btnsaveTo.addActionListener(new SaveToActionListener(this));
		add(btnsaveTo);
	}

	public JTextField getTxtSaveTo() {
		if (txtSaveTo == null) {
			txtSaveTo = new JTextField();
			txtSaveTo.setFont(new Font("Arial", Font.PLAIN, 12));
			txtSaveTo.setHorizontalAlignment(SwingConstants.LEFT);
			txtSaveTo.setText(new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().length()-2));
			txtSaveTo.setColumns(30);
		}
		return txtSaveTo;
	}

	class SaveToActionListener implements ActionListener {

		JPanel panel;

		public SaveToActionListener(JPanel panel) {
			// TODO Auto-generated constructor stub
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			JFileChooser chooser = new JFileChooser(".");
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Choose folder to save experimental results");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			int returnVal = chooser.showOpenDialog(panel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				txtSaveTo.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}

	}
}
