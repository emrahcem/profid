/*Copyright 2010 Emrah Cem

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package example.popularItems.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import example.popularItems.gui.content.ContentPanel;
import example.popularItems.gui.menubar.CustomMenuBar;
import example.popularItems.gui.toolbar.ToolBarPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;

public class ProFIDMainVisual extends JFrame {

	/**
	 * 
	 */
	private static ProFIDMainVisual proFIDMainVisual;
	private static final long serialVersionUID = -2175642866290964073L;

	public static ProFIDMainVisual getInstance(){
		if(proFIDMainVisual==null)
			return proFIDMainVisual= new ProFIDMainVisual();
		else
			return proFIDMainVisual;
	}
	
	private ProFIDMainVisual() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			initGUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setResizable(true);
		setSize(520, 720);
		setJMenuBar(getmenuBar());
		setTitle("ProFID v3.0");

	}

	private void initGUI() throws IOException {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 500, 0 };
		gridBagLayout.rowHeights = new int[] { 40, 40, 275, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		GridBagConstraints gbc_toolBarPanel = new GridBagConstraints();
		gbc_toolBarPanel.anchor = GridBagConstraints.WEST;
		gbc_toolBarPanel.insets = new Insets(0, 0, 0, 0);
		gbc_toolBarPanel.gridx = 0;
		gbc_toolBarPanel.gridy = 0;
		getContentPane().add(getToolBarPanel(), gbc_toolBarPanel);

		GridBagConstraints gbc_savePanel = new GridBagConstraints();
		gbc_savePanel.anchor = GridBagConstraints.WEST;
		gbc_savePanel.insets = new Insets(0, 0, 0, 0);
		gbc_savePanel.gridx = 0;
		gbc_savePanel.gridy = 1;
		getContentPane().add(getSaveResultsPanel(), gbc_savePanel);

		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.fill = GridBagConstraints.BOTH;
		gbc_contentPanel.gridx = 0;
		gbc_contentPanel.gridy = 2;
		gbc_contentPanel.weightx = gbc_contentPanel.weighty = 1.0;
		getContentPane().add(getContentPanel(), gbc_contentPanel);
	}

	/**
	 * This method initializes contentPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public ContentPanel getContentPanel() {
		return  ContentPanel.getInstance();
	}

	/**
	 * This method initializes saveToPanel
	 * 
	 * @return javax.swing.JPanel
	 * @throws IOException 
	 */
	public SaveResultsPanel getSaveResultsPanel() throws IOException {

		return SaveResultsPanel.getInstance();

	}

	/**
	 * This method initializes toolBarPanel
	 * 
	 * @return javax.swing.JPanel
	 * @throws IOException 
	 */
	public ToolBarPanel getToolBarPanel() throws IOException {
		return ToolBarPanel.getInstance();
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	public CustomMenuBar getmenuBar() {
		return CustomMenuBar.getInstance();
	}
	
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame application = ProFIDMainVisual.getInstance();
				application.setVisible(true);
			}
		});
	}
}
