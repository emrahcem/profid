package example.popularItems.gui.content;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import affluenza.gui.Affluenza;
import affluenza.gui.ModelSelector;
import affluenza.simulation.Simulation;
import affluenza.simulation.models.helper.Models;
import example.popularItems.ComponentFactory;
import example.popularItems.InputValidation.ValidationController;
import example.popularItems.gui.ProFIDMainVisual;

public class NetworkParamsPanel extends JPanel {

	/**
	 * 
	 */
	private static NetworkParamsPanel networkParamsPanel;
	private static final long serialVersionUID = 888007964699333589L;

	private JPanel jContentPane;
	private JPanel jContentPane1;
	private JPanel jContentPane2;
	private JPanel jContentPane3;
	private JPanel alphaPanel;
	private JPanel contPaneItemFreqDist;
	private JPanel contPaneItemFreqDist1;

	private JLabel lblChurn;
	private JLabel lblTopologyType;
	private JLabel lblDrop;
	private JLabel lblMaxDelay;
	private JLabel lblMinDelay;
	private JLabel lblItemDistToPeers;
	private JLabel lblItemFreqDist;
	private JLabel lblItemTypes;
	private JLabel lblNumOfExp;
	private JLabel lblNetSize;
	private JLabel lblWireKOut;
	private JLabel lblErdos;
	private JLabel lblBarabasi;
	private JLabel lblWattsK;
	private JLabel lblWattsBeta;
	private JLabel lblVariate;
	private JLabel lblMaxFreq;
	private JLabel lblMinFreq;
	private JLabel lblSkew;
	private JLabel lblMaxFreq1;
	private JLabel lblMinFreq1;

	private JComboBox cmbItemFreqDist;
	private JComboBox cmbDistToPeers;
	private JComboBox cmbTopology;
	private JComboBox cmbChurn;

	private JTextField txtMinDelay;
	private JTextField txtMaxDelay;
	private JTextField txtWireKOut;
	private JTextField txtErdos;
	private JTextField txtBarabasi;
	private JTextField txtWattsK;
	private JTextField txtWattsBeta;
	private JTextField txtNumOfItems;
	private JTextField txtSize;
	private JTextField txtNumOfExp;
	private JTextField txtDrop;
	private JTextField txtVariate;
	private JTextField txtMinFreq;
	private JTextField txtMaxFreq;
	private JTextField txtMinFreq1;
	private JTextField txtMaxFreq1;
	private JTextField txtSkew;

	private JDialog jDialogWireKout;
	private JDialog jDialogErdosRenyi;
	private JDialog jDialogBarabasi;
	private JDialog jDialogWatts;
	private JDialog alphaDialog;
	private JDialog jDialogItemFreqDist;
	private JDialog jDialogItemFreqDist1;

	private JButton btnWireKOut;
	private JButton btnOkErdos;
	private JButton btnOkBarabasi;
	private JButton btnOKWatts;
	private JButton btnRndOK;
	private JButton btnVariate;
	private JButton btnRndOK1;

	private JSlider sldNumOfExp;
	private JSlider sldSize;
	private JSlider sldNumOfItems;
	private JSlider sldDrop;
	private JSlider sldMinDelay;
	private JSlider sldMaxDelay;
	private JSlider sldThreshold;

	private JCheckBox chkChurn;

	public static NetworkParamsPanel getInstance() {
		if (networkParamsPanel == null)
			return networkParamsPanel = new NetworkParamsPanel();
		else
			return networkParamsPanel;
	}

	private NetworkParamsPanel() {

		AlgoParamsPanel.getInstance().getTxtThreshold();
		sldThreshold = AlgoParamsPanel.getInstance().getSldThreshold();

		lblNumOfExp = ComponentFactory.createLabel(new Rectangle(20, 20, 170,
				20), "Number of experiments:",
				"Number of experiments to be performed");
		lblNetSize = ComponentFactory.createLabel(
				new Rectangle(20, 60, 170, 20), "Network size:",
				"Number of peers in the network");
		lblTopologyType = ComponentFactory.createLabel(new Rectangle(21, 102,
				170, 20), "Topology Type:", "Overlay Topology Model");
		lblChurn = ComponentFactory.createLabel(
				new Rectangle(21, 143, 170, 20), "Churn Model:", "Churn Model");
		lblItemTypes = ComponentFactory.createLabel(new Rectangle(21, 185, 170,
				20), "Number of items:",
				"Number of distinct item types in the network");
		lblItemFreqDist = ComponentFactory.createLabel(new Rectangle(21, 229,
				170, 20), "Item frequency distribution:",
				"Distribution of item frequencies");
		lblItemDistToPeers = ComponentFactory.createLabel(new Rectangle(21,
				274, 170, 20), "Distribution of items to peers:",
				"How items will be distributed to peers");
		lblDrop = ComponentFactory
				.createLabel(
						new Rectangle(21, 319, 170, 20),
						"Link drop probability ( % )",
						"When a peer sends a message, it will be lost with that probability (each link's drop probability  is independent)");
		lblMinDelay = ComponentFactory.createLabel(new Rectangle(21, 369, 170,
				20), "Minimum delay (ms):",
				"Minimum delay between any 2 neighbors");
		lblMaxDelay = ComponentFactory.createLabel(new Rectangle(21, 419, 170,
				20), "Maximum delay (ms):",
				"Maximum delay between any 2 neighbors");

		setLayout(null);

		add(lblNetSize, null);
		add(lblNumOfExp, null);
		add(lblItemTypes, null);
		add(lblItemFreqDist, null);
		add(lblItemDistToPeers, null);
		add(lblMinDelay, null);
		add(lblMaxDelay, null);
		add(lblDrop, null);
		add(lblTopologyType, null);
		add(lblChurn, null);

		add(getSldNumOfExp(), null);
		add(getSldSize(), null);
		add(getSldNumOfItems(), null);
		add(getSldDrop(), null);
		add(getSldMinDelay(), null);
		add(getSldMaxDelay(), null);
		add(getTxtNumOfItems(), null);
		add(getTxtSize(), null);
		add(getTxtNumOfExp(), null);
		add(getTxtDrop(), null);
		add(getTxtMinDelay(), null);
		add(getTxtMaxDelay(), null);
		add(getCmbDistToPeers(), null);
		add(getCmbItemFreqDist(), null);
		add(getCmbTopology(), null);
		add(getCmbChurn(), null);

	}

	/**
	 * This method initializes cmbItemFreqDist
	 * 
	 * @return javax.swing.JComboBox
	 */
	public JComboBox getCmbItemFreqDist() {
		if (cmbItemFreqDist == null) {
			cmbItemFreqDist = new JComboBox();
			cmbItemFreqDist.setBounds(new Rectangle(200, 232, 170, 20));
			cmbItemFreqDist.setName("");
			cmbItemFreqDist.setEditable(false);
			cmbItemFreqDist.setFont(new Font("Arial", Font.BOLD, 12));

			cmbItemFreqDist.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (cmbItemFreqDist.getSelectedItem().toString()
							.equalsIgnoreCase("Uniform")) {
						JDialog itemFreqDialog = getJDialogItemFreqDist();
						itemFreqDialog.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						itemFreqDialog.setLocation(loc);
						itemFreqDialog.setVisible(true);
					} else if (cmbItemFreqDist.getSelectedItem().toString()
							.equalsIgnoreCase("Zipf")) {
						JDialog itemFreqDialog1 = getJDialogItemFreqDist1();
						itemFreqDialog1.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						itemFreqDialog1.setLocation(loc);
						itemFreqDialog1.setVisible(true);
					} else {
						if (sldThreshold != null) {
							sldThreshold.setMinimum(0);
							sldThreshold.setMaximum(500);
							sldThreshold.setValue(0);
							Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
							table.put(
									new Integer(
											Integer.parseInt(String
													.valueOf(sldThreshold
															.getMinimum()))),
									new JLabel(String.valueOf(sldThreshold
											.getMinimum())));
							table.put(
									new Integer(
											Integer.parseInt(String
													.valueOf(sldThreshold
															.getMaximum()))),
									new JLabel(String.valueOf(sldThreshold
											.getMaximum())));
							sldThreshold.setLabelTable(table);
							sldThreshold.setPaintLabels(true);
						}
					}

				}
			});

			cmbItemFreqDist.addItem("- SELECT -");
			cmbItemFreqDist.addItem("Uniform");
			cmbItemFreqDist.addItem("Zipf");
		}
		return cmbItemFreqDist;
	}

	/**
	 * This method initializes cmbDistToPeers
	 * 
	 * @return javax.swing.JComboBox
	 */
	public JComboBox getCmbDistToPeers() {
		if (cmbDistToPeers == null) {
			cmbDistToPeers = new JComboBox();
			cmbDistToPeers.setFont(new Font("Arial", Font.BOLD, 12));
			cmbDistToPeers.setBounds(new Rectangle(200, 277, 170, 20));
			cmbDistToPeers.addItem("- SELECT -");
			cmbDistToPeers.addItem("Uniform");
			cmbDistToPeers.addItem("PowerLaw");

			cmbDistToPeers.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmbDistToPeers.getSelectedItem().toString()
							.equalsIgnoreCase("PowerLaw")) {
						JDialog alphaDialog = getAlphaDialog();
						alphaDialog.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						alphaDialog.setLocation(loc);
						alphaDialog.setVisible(true);
					}
				}
			});

		}
		return cmbDistToPeers;
	}

	/**
	 * This method initializes txtMinDelay
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMinDelay() {
		if (txtMinDelay == null) {
			txtMinDelay = new JTextField();
			txtMinDelay.setFont(new Font("Arial", Font.PLAIN, 12));
			txtMinDelay.setBounds(new Rectangle(420, 372, 45, 25));
			txtMinDelay.setText("2");
			txtMinDelay.setEditable(false);
			txtMinDelay.setEnabled(true);
		}
		return txtMinDelay;
	}

	/**
	 * This method initializes txtMaxDelay
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMaxDelay() {
		if (txtMaxDelay == null) {
			txtMaxDelay = new JTextField();
			txtMaxDelay.setFont(new Font("Arial", Font.PLAIN, 12));
			txtMaxDelay.setBounds(new Rectangle(420, 422, 45, 25));
			txtMaxDelay.setText("100");
			txtMaxDelay.setEditable(false);
			txtMaxDelay.setEnabled(true);
		}
		return txtMaxDelay;
	}

	public JComboBox getCmbChurn() {
		if (cmbChurn == null) {
			cmbChurn = new JComboBox();
			cmbChurn.setBounds(new Rectangle(200, 148, 170, 20));
			cmbChurn.addItem("- SELECT -");
			cmbChurn.addItem("No churn (Static topology)");
			for (Models model : Models.values()) {
				cmbChurn.addItem(model);
			}
			cmbChurn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox combo = (JComboBox) e.getSource();
					if (combo.getSelectedIndex() != 0
							&& combo.getSelectedIndex() != 1) {
						Affluenza.getInstance().setVisible(false);

						Models model = (Models) combo.getSelectedItem();

						try {
							model.getConfConstructor().newInstance(model,
									combo, 0);
							Simulation.getInstance().run();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			});
		}
		return cmbChurn;

	}

	/**
	 * This method initializes cmbTopology
	 * 
	 * @return javax.swing.JComboBox
	 */
	public JComboBox getCmbTopology() {
		if (cmbTopology == null) {
			cmbTopology = new JComboBox();
			cmbTopology.setFont(new Font("Arial", Font.BOLD, 12));
			cmbTopology.setBounds(new Rectangle(200, 102, 170, 20));
			cmbTopology.setEditable(false);
			cmbTopology.addItem("- SELECT -");
			cmbTopology.addItem("WireKOut");
			cmbTopology.addItem("Erdos-Renyi");
			cmbTopology.addItem("Barabasi-Albert");
			cmbTopology.addItem("Watts&Strogatz");
			cmbTopology.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if (cmbTopology.getSelectedItem().toString()
							.equalsIgnoreCase("WireKOut")) {
						JDialog itemFreqDialog = getJDialogTopologyWireKout();
						itemFreqDialog.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						itemFreqDialog.setLocation(loc);
						itemFreqDialog.setVisible(true);
					} else if (cmbTopology.getSelectedItem().toString()
							.equalsIgnoreCase("Erdos-Renyi")) {
						JDialog itemFreqDialog1 = getJDialogErdosRenyi();
						itemFreqDialog1.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						itemFreqDialog1.setLocation(loc);
						itemFreqDialog1.setVisible(true);
					} else if (cmbTopology.getSelectedItem().toString()
							.equalsIgnoreCase("Barabasi-Albert")) {
						JDialog itemFreqDialog1 = getJDialogBarabasi();
						itemFreqDialog1.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						itemFreqDialog1.setLocation(loc);
						itemFreqDialog1.setVisible(true);
					} else if (cmbTopology.getSelectedItem().toString()
							.equalsIgnoreCase("Watts&Strogatz")) {
						JDialog itemFreqDialog1 = getJDialogWatts();
						itemFreqDialog1.pack();
						Point loc = ProFIDMainVisual.getInstance()
								.getLocation();
						loc.translate(20, 20);
						itemFreqDialog1.setLocation(loc);
						itemFreqDialog1.setVisible(true);
					}
				}
			});
		}
		return cmbTopology;
	}

	/**
	 * This method initializes jDialogTopologyWireKout
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getJDialogTopologyWireKout() {
		if (jDialogWireKout == null) {
			jDialogWireKout = new JDialog();
			jDialogWireKout.setTitle("WireKOut");
			jDialogWireKout.setBounds(new Rectangle(0, 0, 240, 110));
			jDialogWireKout.setPreferredSize(new Dimension(240, 110));
			jDialogWireKout.setContentPane(getJContentPane());
			jDialogWireKout.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					jDialogWireKout.dispose();
					cmbTopology.setSelectedIndex(0);
				}

			});

		}
		return jDialogWireKout;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane() {
		if (jContentPane == null) {
			lblWireKOut = new JLabel();
			lblWireKOut.setBounds(new Rectangle(15, 15, 95, 20));
			lblWireKOut
					.setToolTipText("Number of random neighbors each peer will connect to");
			lblWireKOut.setText("k ( >0 )");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(lblWireKOut, null);
			jContentPane.add(getTxtWireKOut(), null);
			jContentPane.add(getBtnWireKOut(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes txtWireKOut
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtWireKOut() {
		if (txtWireKOut == null) {
			txtWireKOut = new JTextField();
			txtWireKOut.setText("5");
			txtWireKOut.setBounds(new Rectangle(125, 15, 85, 20));
		}
		return txtWireKOut;
	}

	/**
	 * This method initializes btnWireKOut
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnWireKOut() {
		if (btnWireKOut == null) {
			btnWireKOut = new JButton();
			btnWireKOut.setBounds(new Rectangle(125, 44, 83, 21));
			btnWireKOut.setText("OK");
			btnWireKOut.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean isInt = ValidationController.isInteger(txtWireKOut
							.getText());
					if (isInt && Integer.parseInt(txtWireKOut.getText()) > 0) {
						jDialogWireKout.dispose();
					} else {
						JOptionPane.showMessageDialog(jDialogWireKout,
								"Please enter a positive integer!");
					}

				}

			});

		}
		return btnWireKOut;
	}

	/**
	 * This method initializes jDialogErdosRenyi
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getJDialogErdosRenyi() {
		if (jDialogErdosRenyi == null) {
			jDialogErdosRenyi = new JDialog();
			jDialogErdosRenyi.setTitle("Erdos-Renyi");
			jDialogErdosRenyi.setBounds(new Rectangle(0, 0, 240, 110));
			jDialogErdosRenyi.setPreferredSize(new Dimension(240, 110));
			jDialogErdosRenyi.setContentPane(getJContentPane1());
			jDialogErdosRenyi.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					jDialogErdosRenyi.dispose();
					cmbTopology.setSelectedIndex(0);
				}

			});

		}
		return jDialogErdosRenyi;
	}

	/**
	 * This method initializes jContentPane1
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane1() {
		if (jContentPane1 == null) {
			lblErdos = new JLabel();
			lblErdos.setBounds(new Rectangle(15, 15, 95, 20));
			lblErdos.setToolTipText("The probability of setting an edge between each pair of nodes ");
			lblErdos.setText("p ( [0,1] )");
			jContentPane1 = new JPanel();
			jContentPane1.setLayout(null);
			jContentPane1.add(lblErdos, null);
			jContentPane1.add(getTxtErdos(), null);
			jContentPane1.add(getBtnOkErdos(), null);
		}
		return jContentPane1;
	}

	/**
	 * This method initializes txtErdos
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtErdos() {
		if (txtErdos == null) {
			txtErdos = new JTextField();
			txtErdos.setText("0.5");
			txtErdos.setBounds(new Rectangle(125, 15, 85, 20));
		}
		return txtErdos;
	}

	/**
	 * This method initializes btnOkErdos
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnOkErdos() {
		if (btnOkErdos == null) {
			btnOkErdos = new JButton();
			btnOkErdos.setBounds(new Rectangle(125, 45, 85, 20));
			btnOkErdos.setText("OK");
			btnOkErdos.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean isPosDouble = ValidationController
							.isPositiveDouble(txtErdos.getText());
					if (isPosDouble
							&& Double.parseDouble(txtErdos.getText()) <= 1) {
						jDialogErdosRenyi.dispose();
					} else {
						JOptionPane
								.showMessageDialog(jDialogErdosRenyi,
										"Please enter a real value in the range [0,1]!");
					}

				}

			});

		}
		return btnOkErdos;
	}

	/**
	 * This method initializes jDialogBarabasi
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getJDialogBarabasi() {
		if (jDialogBarabasi == null) {
			jDialogBarabasi = new JDialog();
			jDialogBarabasi.setBounds(new Rectangle(0, 0, 240, 110));
			jDialogBarabasi.setTitle("Barabasi-Albert");
			jDialogBarabasi.setPreferredSize(new Dimension(240, 110));
			jDialogBarabasi.setContentPane(getJContentPane2());
			jDialogBarabasi.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					jDialogBarabasi.dispose();
					cmbTopology.setSelectedIndex(0);
				}

			});

		}
		return jDialogBarabasi;
	}

	/**
	 * This method initializes jContentPane2
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane2() {
		if (jContentPane2 == null) {
			lblBarabasi = new JLabel();
			lblBarabasi.setBounds(new Rectangle(15, 15, 95, 20));
			lblBarabasi
					.setToolTipText("The number of edges added to each new node (apart from those forming the initial network.");
			lblBarabasi.setText("k ( >0 )");
			jContentPane2 = new JPanel();
			jContentPane2.setLayout(null);
			jContentPane2.add(lblBarabasi, null);
			jContentPane2.add(getTxtBarabasi(), null);
			jContentPane2.add(getBtnOkBarabasi(), null);
		}
		return jContentPane2;
	}

	/**
	 * This method initializes txtBarabasi
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtBarabasi() {
		if (txtBarabasi == null) {
			txtBarabasi = new JTextField();
			txtBarabasi.setText("5");
			txtBarabasi.setBounds(new Rectangle(125, 15, 85, 20));
		}
		return txtBarabasi;
	}

	/**
	 * This method initializes btnOkBarabasi
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnOkBarabasi() {
		if (btnOkBarabasi == null) {
			btnOkBarabasi = new JButton();
			btnOkBarabasi.setBounds(new Rectangle(125, 45, 85, 20));
			btnOkBarabasi.setText("OK");
			btnOkBarabasi
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							boolean isInt = ValidationController
									.isInteger(txtBarabasi.getText());
							if (isInt
									&& Integer.parseInt(txtBarabasi.getText()) > 0) {
								jDialogBarabasi.dispose();
							} else {
								JOptionPane
										.showMessageDialog(jDialogBarabasi,
												"Please enter a positive integer value!");
							}
						}
					});

		}
		return btnOkBarabasi;
	}

	/**
	 * This method initializes jDialogWatts
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getJDialogWatts() {
		if (jDialogWatts == null) {
			jDialogWatts = new JDialog();
			jDialogWatts.setBounds(new Rectangle(0, 0, 240, 140));
			jDialogWatts.setTitle("Watts&Strogatz");
			jDialogWatts.setPreferredSize(new Dimension(240, 140));
			jDialogWatts.setContentPane(getJContentPane3());
			jDialogWatts.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					jDialogWatts.dispose();
					cmbTopology.setSelectedIndex(0);
				}

			});

		}
		return jDialogWatts;
	}

	/**
	 * This method initializes jContentPane3
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane3() {
		if (jContentPane3 == null) {
			lblWattsK = new JLabel();
			lblWattsK.setBounds(new Rectangle(14, 46, 95, 17));
			lblWattsK.setToolTipText("The degree of the graph");
			lblWattsK.setText("k ( >0 )");
			lblWattsBeta = new JLabel();
			lblWattsBeta.setBounds(new Rectangle(15, 16, 95, 18));
			lblWattsBeta
					.setToolTipText("The probability for a node to be rewired");
			lblWattsBeta.setText("Beta([0,1])");
			jContentPane3 = new JPanel();
			jContentPane3.setLayout(null);
			jContentPane3.add(lblWattsBeta, null);
			jContentPane3.add(lblWattsK, null);
			jContentPane3.add(getTxtWattsBeta(), null);
			jContentPane3.add(getTxtWattsK(), null);
			jContentPane3.add(getBtnOKWatts(), null);
		}
		return jContentPane3;
	}

	/**
	 * This method initializes txtWattsBeta
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtWattsBeta() {
		if (txtWattsBeta == null) {
			txtWattsBeta = new JTextField();
			txtWattsBeta.setBounds(new Rectangle(125, 15, 85, 20));
		}
		return txtWattsBeta;
	}

	/**
	 * This method initializes txtWattsK
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtWattsK() {
		if (txtWattsK == null) {
			txtWattsK = new JTextField();
			txtWattsK.setBounds(new Rectangle(125, 45, 85, 20));
		}
		return txtWattsK;
	}

	/**
	 * This method initializes btnOKWatts
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnOKWatts() {
		if (btnOKWatts == null) {
			btnOKWatts = new JButton();
			btnOKWatts.setBounds(new Rectangle(125, 75, 85, 20));
			btnOKWatts.setText("OK");
			btnOKWatts.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean goodBeta = ValidationController
							.isPositiveDouble(txtWattsBeta.getText());
					boolean goodK = ValidationController.isInteger(txtWattsK
							.getText());
					if (goodBeta
							&& Double.parseDouble(txtWattsBeta.getText()) <= 1
							&& goodK) {
						jDialogWatts.dispose();
					} else {
						JOptionPane.showMessageDialog(jDialogWatts,
								"Please, fill all fields correctly!");
					}
				}
			});

		}
		return btnOKWatts;
	}

	/**
	 * This method initializes sldNumOfExp
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldNumOfExp() {
		if (sldNumOfExp == null) {
			sldNumOfExp = new JSlider(1, 50, 20);
			sldNumOfExp.setFont(new Font("Arial", Font.PLAIN, 11));
			sldNumOfExp.setBounds(new Rectangle(200, 20, 200, 40));
			sldNumOfExp.setMajorTickSpacing(10);
			sldNumOfExp.setPaintLabels(true);
			sldNumOfExp.setPaintTicks(false);

			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(new Integer(1), new JLabel("1"));
			table.put(new Integer(10), new JLabel("10"));
			table.put(new Integer(20), new JLabel("20"));
			table.put(new Integer(30), new JLabel("30"));
			table.put(new Integer(40), new JLabel("40"));
			table.put(new Integer(50), new JLabel("50"));
			sldNumOfExp.setLabelTable(table);
			sldNumOfExp.setPaintLabels(true);

			sldNumOfExp
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtNumOfExp.setText(String.valueOf(sldNumOfExp
									.getValue()));
						}
					});
		}
		return sldNumOfExp;
	}

	/**
	 * This method initializes sldSize
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldSize() {
		if (sldSize == null) {
			sldSize = new JSlider(1, 5, 1);
			sldSize.setFont(new Font("Arial", Font.PLAIN, 11));
			sldSize.setBounds(new Rectangle(194, 60, 212, 40));
			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(new Integer(1), new JLabel("50"));
			table.put(new Integer(2), new JLabel("100"));
			table.put(new Integer(3), new JLabel("200"));
			table.put(new Integer(4), new JLabel("500"));
			table.put(new Integer(5), new JLabel("1000"));
			sldSize.setMajorTickSpacing(1);
			sldSize.setSnapToTicks(true);
			sldSize.setLabelTable(table);
			sldSize.setPaintLabels(true);

			sldSize.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					int[] array = { 50, 100, 200, 500, 1000 };
					int index = Integer.parseInt(String.valueOf(sldSize
							.getValue()));
					txtSize.setText(array[index - 1] + "");
				}
			});
		}
		return sldSize;
	}

	/**
	 * This method initializes sldNumOfItems
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldNumOfItems() {
		if (sldNumOfItems == null) {
			sldNumOfItems = new JSlider(1, 500, 1);
			sldNumOfItems.setBounds(new Rectangle(198, 187, 208, 40));
			sldNumOfItems.setPaintLabels(true);
			sldNumOfItems.setPaintTicks(false);

			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(new Integer(1), new JLabel("1"));
			table.put(new Integer(100), new JLabel("100"));
			table.put(new Integer(200), new JLabel("200"));
			table.put(new Integer(300), new JLabel("300"));
			table.put(new Integer(400), new JLabel("400"));
			table.put(new Integer(500), new JLabel("500"));
			sldNumOfItems.setLabelTable(table);

			sldNumOfItems
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtNumOfItems.setText(String.valueOf(sldNumOfItems
									.getValue()));
						}
					});
		}
		return sldNumOfItems;
	}

	/**
	 * This method initializes sldDrop
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldDrop() {
		if (sldDrop == null) {
			sldDrop = new JSlider();
			sldDrop.setFont(new Font("Arial", Font.PLAIN, 11));
			sldDrop.setBounds(new Rectangle(203, 322, 200, 40));
			sldDrop.setMaximum(10);
			sldDrop.setMinimum(0);
			sldDrop.setMinorTickSpacing(2);
			sldDrop.setPaintLabels(true);
			sldDrop.setPaintTicks(false);
			sldDrop.setSnapToTicks(false);
			sldDrop.setValue(0);
			sldDrop.setMajorTickSpacing(1);

			sldDrop.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					txtDrop.setText(String.valueOf(sldDrop.getValue()));
				}
			});
		}
		return sldDrop;
	}

	/**
	 * This method initializes txtNumOfItems
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtNumOfItems() {
		if (txtNumOfItems == null) {
			txtNumOfItems = new JTextField();
			txtNumOfItems.setFont(new Font("Arial", Font.PLAIN, 12));
			txtNumOfItems.setBounds(new Rectangle(420, 187, 45, 25));
			txtNumOfItems.setEnabled(true);
			txtNumOfItems.setEditable(false);
			txtNumOfItems.setText(String.valueOf(sldNumOfItems.getValue()));
		}
		return txtNumOfItems;
	}

	/**
	 * This method initializes txtSize
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtSize() {
		if (txtSize == null) {
			txtSize = new JTextField();
			txtSize.setFont(new Font("Arial", Font.PLAIN, 12));
			txtSize.setText("50");
			txtSize.setEnabled(true);
			txtSize.setEditable(true);
			txtSize.setBounds(new Rectangle(420, 60, 45, 25));
		}
		return txtSize;
	}

	/**
	 * This method initializes txtNumOfExp
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtNumOfExp() {
		if (txtNumOfExp == null) {
			txtNumOfExp = new JTextField();
			txtNumOfExp.setFont(new Font("Arial", Font.PLAIN, 12));
			txtNumOfExp.setText(String.valueOf(sldNumOfExp.getValue()));
			txtNumOfExp.setEnabled(true);
			txtNumOfExp.setEditable(false);
			txtNumOfExp.setBounds(new Rectangle(420, 20, 45, 25));
		}
		return txtNumOfExp;
	}

	/**
	 * This method initializes txtDrop
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtDrop() {
		if (txtDrop == null) {
			txtDrop = new JTextField();
			txtDrop.setFont(new Font("Arial", Font.PLAIN, 12));
			txtDrop.setText(String.valueOf(sldDrop.getValue()));
			txtDrop.setEnabled(true);
			txtDrop.setEditable(false);
			txtDrop.setBounds(new Rectangle(420, 322, 45, 25));
		}
		return txtDrop;
	}

	/**
	 * This method initializes sldMinDelay
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldMinDelay() {
		if (sldMinDelay == null) {
			sldMinDelay = new JSlider(0, 20, 2);
			sldMinDelay.setFont(new Font("Arial", Font.PLAIN, 11));
			sldMinDelay.setBounds(new Rectangle(203, 372, 200, 40));
			sldMinDelay.setMinorTickSpacing(10);
			sldMinDelay.setPaintLabels(true);
			sldMinDelay.setPaintTicks(false);
			sldMinDelay.setSnapToTicks(false);
			sldMinDelay.setMajorTickSpacing(5);
			sldMinDelay
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtMinDelay.setText(String.valueOf(sldMinDelay
									.getValue()));
						}
					});
		}
		return sldMinDelay;
	}

	/**
	 * This method initializes sldMaxDelay
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldMaxDelay() {
		if (sldMaxDelay == null) {
			sldMaxDelay = new JSlider(0, 200, 100);
			sldMaxDelay.setFont(new Font("Arial", Font.PLAIN, 11));
			sldMaxDelay.setBounds(new Rectangle(200, 422, 205, 40));
			sldMaxDelay.setMinorTickSpacing(5);
			sldMaxDelay.setPaintLabels(true);
			sldMaxDelay.setPaintTicks(false);
			sldMaxDelay.setSnapToTicks(true);
			sldMaxDelay.setMajorTickSpacing(40);
			sldMaxDelay
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtMaxDelay.setText(String.valueOf(sldMaxDelay
									.getValue()));
						}
					});

		}
		return sldMaxDelay;
	}

	/**
	 * This method initializes alphaDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getAlphaDialog() {
		if (alphaDialog == null) {
			alphaDialog = new JDialog();
			alphaDialog.setSize(new Dimension(238, 108));
			alphaDialog.setTitle("Power Law Distribution");
			alphaDialog.setMinimumSize(new Dimension(236, 110));
			alphaDialog.setContentPane(getAlphaPanel());
			alphaDialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					alphaDialog.dispose();
					cmbDistToPeers.setSelectedIndex(0);
				}
			});

		}
		return alphaDialog;
	}

	/**
	 * This method initializes alphaPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getAlphaPanel() {
		if (alphaPanel == null) {
			lblVariate = new JLabel();
			lblVariate.setBounds(new Rectangle(14, 15, 131, 20));
			lblVariate
					.setToolTipText("see http://mathworld.wolfram.com/RandomNumber.html for details");
			lblVariate.setText("Ditsribution Power (n)");
			alphaPanel = new JPanel();
			alphaPanel.setLayout(null);
			alphaPanel.setPreferredSize(new Dimension(220, 70));
			alphaPanel.add(lblVariate, null);
			alphaPanel.add(getTxtVariate(), null);
			alphaPanel.add(getBtnVariate(), null);
		}
		return alphaPanel;
	}

	/**
	 * This method initializes txtVariate
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtVariate() {
		if (txtVariate == null) {
			txtVariate = new JTextField();
			txtVariate.setText("2");
			txtVariate.setBounds(new Rectangle(147, 15, 58, 20));
		}
		return txtVariate;
	}

	/**
	 * This method initializes btnVariate
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnVariate() {
		if (btnVariate == null) {
			btnVariate = new JButton();
			btnVariate.setBounds(new Rectangle(120, 45, 85, 20));
			btnVariate.setText("OK");
			btnVariate.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean boolVariate = ValidationController
							.isPositiveDouble(txtVariate.getText());

					if (boolVariate) {
						// UserInputs.variate=txtVariate.getText();
						alphaDialog.dispose();
					} else {
						JOptionPane.showMessageDialog(jDialogItemFreqDist1,
								"Please, enter a positive real value for n!");
					}
				}
			});

		}
		return btnVariate;
	}

	/**
	 * This method initializes jDialogItemFreqDist
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getJDialogItemFreqDist() {
		if (jDialogItemFreqDist == null) {
			jDialogItemFreqDist = new JDialog();
			jDialogItemFreqDist.setTitle("Uniform Distribution");
			jDialogItemFreqDist.setMinimumSize(new Dimension(100, 100));
			jDialogItemFreqDist
					.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			jDialogItemFreqDist.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			jDialogItemFreqDist.setBounds(new Rectangle(0, 0, 240, 140));
			jDialogItemFreqDist.setContentPane(getContPaneItemFreqDist());
			jDialogItemFreqDist.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					jDialogItemFreqDist.dispose();
					cmbItemFreqDist.setSelectedIndex(0);
				}

			});
		}
		return jDialogItemFreqDist;
	}

	/**
	 * This method initializes contPaneItemFreqDist
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getContPaneItemFreqDist() {
		if (contPaneItemFreqDist == null) {
			lblMaxFreq = new JLabel();
			lblMaxFreq.setBounds(new Rectangle(15, 45, 95, 20));
			lblMaxFreq.setToolTipText("maximum global frequency of an item");
			lblMaxFreq.setText("Max Frequency");
			lblMinFreq = new JLabel();
			lblMinFreq.setBounds(new Rectangle(15, 15, 95, 20));
			lblMinFreq.setToolTipText("minimum global frequency of an item");
			lblMinFreq.setText("Min Frequency");
			contPaneItemFreqDist = new JPanel();
			contPaneItemFreqDist.setLayout(null);
			contPaneItemFreqDist.setPreferredSize(new Dimension(220, 100));
			contPaneItemFreqDist.add(lblMinFreq, null);
			contPaneItemFreqDist.add(lblMaxFreq, null);
			contPaneItemFreqDist.add(getTxtMinFreq(), null);
			contPaneItemFreqDist.add(getTxtMaxFreq(), null);
			contPaneItemFreqDist.add(getBtnRndOK(), null);
		}
		return contPaneItemFreqDist;
	}

	/**
	 * This method initializes txtMinFreq
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMinFreq() {
		if (txtMinFreq == null) {
			txtMinFreq = new JTextField();
			txtMinFreq.setBounds(new Rectangle(125, 15, 85, 20));
		}
		return txtMinFreq;
	}

	/**
	 * This method initializes txtMaxFreq
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMaxFreq() {
		if (txtMaxFreq == null) {
			txtMaxFreq = new JTextField();
			txtMaxFreq.setBounds(new Rectangle(125, 45, 85, 20));
		}
		return txtMaxFreq;
	}

	/**
	 * This method initializes btnRndOK
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnRndOK() {
		if (btnRndOK == null) {
			btnRndOK = new JButton();
			btnRndOK.setBounds(new Rectangle(125, 75, 85, 20));
			btnRndOK.setText("OK");
			btnRndOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean boolMin = ValidationController.isInteger(txtMinFreq
							.getText());
					boolean boolMax = ValidationController.isInteger(txtMaxFreq
							.getText());

					if (boolMin
							&& boolMax
							&& Integer.parseInt(txtMaxFreq.getText()) >= Integer
									.parseInt(txtMinFreq.getText())) {
						int max = Integer.parseInt(txtMaxFreq.getText());
						int min = Integer.parseInt(txtMinFreq.getText());

						sldThreshold.setMinimum(min);
						sldThreshold.setMaximum(max);
						sldThreshold.setValue((min + max) / 2);
						Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
						table.put(new Integer(min),
								new JLabel(txtMinFreq.getText()));
						table.put(new Integer(max),
								new JLabel(txtMaxFreq.getText()));

						sldThreshold.setLabelTable(table);
						sldThreshold.setPaintLabels(true);

						jDialogItemFreqDist.dispose();
					} else {
						JOptionPane.showMessageDialog(jDialogItemFreqDist,
								"Please, fill all fields correctly!");
					}

				}

			});
		}
		return btnRndOK;
	}

	/**
	 * This method initializes jDialogItemFreqDist2
	 * 
	 * @return javax.swing.JDialog
	 */
	public JDialog getJDialogItemFreqDist1() {
		if (jDialogItemFreqDist1 == null) {
			jDialogItemFreqDist1 = new JDialog();
			jDialogItemFreqDist1.setMinimumSize(new Dimension(100, 100));
			jDialogItemFreqDist1.setSize(new Dimension(239, 170));
			jDialogItemFreqDist1.setContentPane(getContPaneItemFreqDist1());
			jDialogItemFreqDist1.setTitle("Zipf Distribution");
			jDialogItemFreqDist1.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					jDialogItemFreqDist1.dispose();
					cmbItemFreqDist.setSelectedIndex(0);
				}

			});

		}
		return jDialogItemFreqDist1;
	}

	/**
	 * This method initializes contPaneItemFreqDist1
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getContPaneItemFreqDist1() {
		if (contPaneItemFreqDist1 == null) {
			lblSkew = new JLabel();
			lblSkew.setBounds(new Rectangle(15, 75, 95, 20));
			lblSkew.setToolTipText("Must be >0");
			lblSkew.setText("Skew ( >0 )");
			lblMaxFreq1 = new JLabel();
			lblMaxFreq1.setBounds(new Rectangle(15, 45, 95, 20));
			lblMaxFreq1.setToolTipText("maximum global frequency of an item");
			lblMaxFreq1.setText("Max Frequency");
			lblMinFreq1 = new JLabel();
			lblMinFreq1.setBounds(new Rectangle(15, 15, 95, 20));
			lblMinFreq1.setToolTipText("minimum global frequency of an item");
			lblMinFreq1.setText("Min Frequency");
			contPaneItemFreqDist1 = new JPanel();
			contPaneItemFreqDist1.setLayout(null);
			contPaneItemFreqDist1.setPreferredSize(new Dimension(220, 130));
			contPaneItemFreqDist1.add(lblMinFreq1, null);
			contPaneItemFreqDist1.add(lblMaxFreq1, null);
			contPaneItemFreqDist1.add(getTxtMinFreq1(), null);
			contPaneItemFreqDist1.add(getTxtMaxFreq1(), null);
			contPaneItemFreqDist1.add(getBtnRndOK1(), null);
			contPaneItemFreqDist1.add(lblSkew, null);
			contPaneItemFreqDist1.add(getTxtSkew(), null);
		}
		return contPaneItemFreqDist1;
	}

	/**
	 * This method initializes txtMinFreq1
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMinFreq1() {
		if (txtMinFreq1 == null) {
			txtMinFreq1 = new JTextField();
			txtMinFreq1.setBounds(new Rectangle(125, 15, 85, 20));
		}
		return txtMinFreq1;
	}

	/**
	 * This method initializes txtMaxFreq1
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMaxFreq1() {
		if (txtMaxFreq1 == null) {
			txtMaxFreq1 = new JTextField();
			txtMaxFreq1.setBounds(new Rectangle(125, 45, 85, 20));
		}
		return txtMaxFreq1;
	}

	/**
	 * This method initializes btnRndOK1
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getBtnRndOK1() {
		if (btnRndOK1 == null) {
			btnRndOK1 = new JButton();
			btnRndOK1.setBounds(new Rectangle(125, 105, 85, 20));
			btnRndOK1.setText("OK");
			btnRndOK1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean boolMin = ValidationController
							.isInteger(txtMinFreq1.getText());
					boolean boolMax = ValidationController
							.isInteger(txtMaxFreq1.getText());
					boolean boolSkew = ValidationController
							.isPositiveDouble(txtSkew.getText());

					if (boolMin
							&& boolMax
							&& boolSkew
							&& Integer.parseInt(txtMaxFreq1.getText()) >= Integer
									.parseInt(txtMinFreq1.getText())) {
						int max = Integer.parseInt(txtMaxFreq1.getText());
						int min = Integer.parseInt(txtMinFreq1.getText());
						sldThreshold.setMinimum(min);
						sldThreshold.setMaximum(max);
						sldThreshold.setValue((min + max) / 2);
						Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
						table.put(new Integer(min),
								new JLabel(txtMinFreq1.getText()));
						table.put(new Integer(max),
								new JLabel(txtMaxFreq1.getText()));

						sldThreshold.setLabelTable(table);
						sldThreshold.setPaintLabels(true);

						jDialogItemFreqDist1.dispose();
					} else {
						JOptionPane.showMessageDialog(jDialogItemFreqDist1,
								"Please, fill all fields correctly!");
					}
				}
			});

		}

		return btnRndOK1;
	}

	/**
	 * This method initializes txtSkew
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtSkew() {
		if (txtSkew == null) {
			txtSkew = new JTextField();
			txtSkew.setText("0.271");
			txtSkew.setBounds(new Rectangle(125, 75, 85, 20));
		}
		return txtSkew;
	}

	/**
	 * This method initializes chkChurn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getChkChurn() {
		if (chkChurn == null) {
			chkChurn = new JCheckBox();
			chkChurn.setBounds(new Rectangle(201, 148, 19, 16));
			chkChurn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (((JCheckBox) arg0.getSource()).isSelected()) {
						Affluenza.getInstance().setVisible(false);
						new ModelSelector(0);
						Simulation.getInstance().run();
						if (!Simulation.UPTODATE) {
							((JCheckBox) arg0.getSource()).setSelected(false);
						}
					}
				}
			});
		}
		return chkChurn;

	}
}