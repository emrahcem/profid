package example.popularItems.gui.content;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class AlgoParamsPanel extends JPanel {

	/**
	 * 
	 */
	private static AlgoParamsPanel algoParamsPanel;
	private static final long serialVersionUID = -4621071923849750094L;

	private JLabel lblAlgorithm;
	private JLabel lblNeighborList;
	private JLabel lblInitState;
	private JLabel lblTraceFile;
	private JLabel lblDelta;
	private JLabel lblThreshold;
	private JLabel lblMms;
	private JLabel lblFanout;
	private JLabel lblEpsilon;
	private JLabel lblConvLimit;

	private JTextField txtConvLimit;
	private JTextField txtEpsilon;
	private JTextField txtFanout;
	private JTextField txtMms;
	private JTextField txtDelta;
	private JTextField txtThreshold;

	private JSlider sldConvLimit;
	private JSlider sldEpsilon;
	private JSlider sldFanout;
	private JSlider sldMms;
	private JSlider sldDelta;
	private JSlider sldThreshold;

	private JCheckBox chkCreateTraceFile;
	private JCheckBox chkInitState;
	private JCheckBox chkNeighborList;

	private JComboBox cmbAlgorithm;

	public static AlgoParamsPanel getInstance() {
		if (algoParamsPanel == null)
			return algoParamsPanel = new AlgoParamsPanel();
		else
			return algoParamsPanel;
	}

	private AlgoParamsPanel() {
		initLabels();
		setLayout(null);
		addComponents();
	}

	/**
	 * This method initializes txtConvLimit
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtConvLimit() {
		if (txtConvLimit == null) {
			txtConvLimit = new JTextField();
			txtConvLimit.setFont(new Font("Arial", Font.PLAIN, 12));
			txtConvLimit.setBounds(new Rectangle(422, 43, 45, 25));
			txtConvLimit.setText("10");
			txtConvLimit.setEditable(false);
		}
		return txtConvLimit;
	}

	/**
	 * This method initializes txtEpsilon
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtEpsilon() {
		if (txtEpsilon == null) {
			txtEpsilon = new JTextField();
			txtEpsilon.setFont(new Font("Arial", Font.PLAIN, 12));
			txtEpsilon.setBounds(new Rectangle(422, 83, 45, 25));
			txtEpsilon.setText("10");
			txtEpsilon.setEditable(false);
		}
		return txtEpsilon;
	}

	/**
	 * This method initializes txtFanout
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtFanout() {
		if (txtFanout == null) {
			txtFanout = new JTextField();
			txtFanout.setFont(new Font("Arial", Font.PLAIN, 12));
			txtFanout.setBounds(new Rectangle(422, 123, 45, 25));
			txtFanout.setText("1");
			txtFanout.setEditable(false);
		}
		return txtFanout;
	}

	/**
	 * This method initializes txtMms
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtMms() {
		if (txtMms == null) {
			txtMms = new JTextField();
			txtMms.setFont(new Font("Arial", Font.PLAIN, 12));
			txtMms.setBounds(new Rectangle(422, 163, 45, 25));
			txtMms.setText("100");
			txtMms.setEditable(false);
		}
		return txtMms;
	}

	/**
	 * This method initializes txtDelta
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtDelta() {
		if (txtDelta == null) {
			txtDelta = new JTextField();
			txtDelta.setFont(new Font("Arial", Font.PLAIN, 12));
			txtDelta.setBounds(new Rectangle(422, 243, 45, 25));
			txtDelta.setText("0");
			txtDelta.setEditable(false);
		}
		return txtDelta;
	}

	/**
	 * This method initializes sldConvLimit
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldConvLimit() {
		if (sldConvLimit == null) {
			sldConvLimit = new JSlider(4, 20, 10);
			sldConvLimit.setFont(new Font("Arial", Font.PLAIN, 11));
			sldConvLimit.setBounds(new Rectangle(167, 43, 220, 40));
			sldConvLimit.setMajorTickSpacing(4);
			sldConvLimit.setPaintLabels(true);
			sldConvLimit.setPaintTicks(false);
			sldConvLimit.setMinorTickSpacing(1);
			sldConvLimit.setSnapToTicks(true);
			sldConvLimit
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtConvLimit.setText(String.valueOf(sldConvLimit
									.getValue()));
						}
					});

		}
		return sldConvLimit;
	}

	/**
	 * This method initializes sldEpsilon
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldEpsilon() {
		if (sldEpsilon == null) {
			sldEpsilon = new JSlider(1, 25, 10);
			sldEpsilon.setFont(new Font("Arial", Font.PLAIN, 11));
			sldEpsilon.setBounds(new Rectangle(167, 83, 220, 40));
			sldEpsilon.setPaintLabels(true);
			sldEpsilon.setPaintTicks(false);
			sldEpsilon.setMajorTickSpacing(4);
			sldEpsilon
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtEpsilon.setText(String.valueOf(sldEpsilon
									.getValue()));
						}
					});

			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(new Integer(1), new JLabel("1"));
			table.put(new Integer(5), new JLabel("5"));
			table.put(new Integer(10), new JLabel("10"));
			table.put(new Integer(15), new JLabel("15"));
			table.put(new Integer(20), new JLabel("20"));
			table.put(new Integer(25), new JLabel("25"));
			sldEpsilon.setLabelTable(table);
			sldEpsilon.setPaintLabels(true);

		}
		return sldEpsilon;
	}

	/**
	 * This method initializes sldFanout
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldFanout() {
		if (sldFanout == null) {
			sldFanout = new JSlider(1, 5, 1);
			sldFanout.setFont(new Font("Arial", Font.PLAIN, 11));
			sldFanout.setBounds(new Rectangle(167, 123, 220, 40));
			sldFanout.setPaintLabels(true);
			sldFanout.setPaintTicks(false);
			sldFanout.setSnapToTicks(true);
			sldFanout.setMajorTickSpacing(1);

			sldFanout.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					txtFanout.setText(String.valueOf(sldFanout.getValue()));
				}
			});
		}
		return sldFanout;
	}

	/**
	 * This method initializes sldMms
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldMms() {
		if (sldMms == null) {
			sldMms = new JSlider(100, 1000, 100);
			sldMms.setBounds(new Rectangle(167, 163, 220, 40));
			sldMms.setPaintLabels(true);
			sldMms.setPaintTicks(false);
			sldMms.setSnapToTicks(true);
			sldMms.setMinorTickSpacing(100);
			sldMms.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					txtMms.setText(String.valueOf(sldMms.getValue()));
				}
			});
			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(new Integer(100), new JLabel("100"));
			table.put(new Integer(500), new JLabel("500"));
			table.put(new Integer(1000), new JLabel("1000"));
			sldMms.setLabelTable(table);
			sldMms.setPaintLabels(true);

		}
		return sldMms;
	}

	/**
	 * This method initializes sldDelta
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldDelta() {
		if (sldDelta == null) {
			sldDelta = new JSlider(0, 10, 0);
			sldDelta.setFont(new Font("Arial", Font.PLAIN, 11));
			sldDelta.setBounds(new Rectangle(167, 243, 220, 40));
			sldDelta.setPaintLabels(true);
			sldDelta.setPaintTicks(false);
			sldDelta.setSnapToTicks(true);
			sldDelta.setMajorTickSpacing(2);
			sldDelta.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					txtDelta.setText(String.valueOf(sldDelta.getValue()));
				}
			});
		}
		return sldDelta;
	}

	/**
	 * This method initializes chkCreateTraceFile
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getChkCreateTraceFile() {
		if (chkCreateTraceFile == null) {
			chkCreateTraceFile = new JCheckBox();
			chkCreateTraceFile.setBounds(new Rectangle(162, 293, 20, 20));
		}
		return chkCreateTraceFile;
	}

	/**
	 * This method initializes chkInitState
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getChkInitState() {
		if (chkInitState == null) {
			chkInitState = new JCheckBox();
			chkInitState.setBounds(new Rectangle(162, 333, 20, 20));
		}
		return chkInitState;
	}

	/**
	 * This method initializes chkNeighborList
	 * 
	 * @return javax.swing.JCheckBox
	 */
	public JCheckBox getChkNeighborList() {
		if (chkNeighborList == null) {
			chkNeighborList = new JCheckBox();
			chkNeighborList.setBounds(new Rectangle(162, 373, 20, 20));
		}
		return chkNeighborList;
	}

	/**
	 * This method initializes cmbAlgorithm
	 * 
	 * @return javax.swing.JComboBox
	 */
	public JComboBox getCmbAlgorithm() {
		if (cmbAlgorithm == null) {
			cmbAlgorithm = new JComboBox();
			cmbAlgorithm.setFont(new Font("Arial", Font.PLAIN, 12));
			cmbAlgorithm.setBounds(new Rectangle(167, 15, 151, 18));
			cmbAlgorithm.setEditable(false);
			cmbAlgorithm.addItem("ProFID");
			cmbAlgorithm.addItem("Adaptive ProFID");
			cmbAlgorithm.addItem("Hierarchical ProFID");
			cmbAlgorithm.addItem("PushSum");
			cmbAlgorithm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.err.println(cmbAlgorithm.getSelectedItem()
							.toString());
					if (cmbAlgorithm.getSelectedItem().toString()
							.equalsIgnoreCase("ProFID")) {
						sldMms.setVisible(true);
						lblMms.setVisible(true);
						txtMms.setVisible(true);
					} else if (cmbAlgorithm.getSelectedItem().toString()
							.equalsIgnoreCase("Adaptive ProFID")) {
						sldMms.setVisible(true);
						lblMms.setVisible(true);
						txtMms.setVisible(true);
					} else if (cmbAlgorithm.getSelectedItem().toString()
							.equalsIgnoreCase("Hierarchical ProFID")) {
						sldMms.setVisible(true);
						lblMms.setVisible(true);
						txtMms.setVisible(true);
					} else if (cmbAlgorithm.getSelectedItem().toString()
							.equalsIgnoreCase("PushSum")) {
						sldMms.setVisible(false);
						lblMms.setVisible(false);
						txtMms.setVisible(false);
					}
				}
			});

		}
		return cmbAlgorithm;
	}

	/**
	 * This method initializes sldThreshold
	 * 
	 * @return javax.swing.JSlider
	 */
	public JSlider getSldThreshold() {
		if (sldThreshold == null) {
			sldThreshold = new JSlider(0, 500, 250);
			sldThreshold.setBounds(new Rectangle(167, 203, 220, 40));
			sldThreshold.setPaintLabels(true);
			sldThreshold.setPaintTicks(false);
			sldThreshold.setSnapToTicks(true);

			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(
					new Integer(Integer.parseInt(String.valueOf(sldThreshold
							.getMinimum()))),
					new JLabel(String.valueOf(sldThreshold.getMinimum())));
			table.put(
					new Integer(Integer.parseInt(String.valueOf(sldThreshold
							.getMaximum()))),
					new JLabel(String.valueOf(sldThreshold.getMaximum())));
			sldThreshold.setLabelTable(table);
			sldThreshold.setPaintLabels(true);

			sldThreshold
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							txtThreshold.setText(String.valueOf(sldThreshold
									.getValue()));
						}
					});
		}
		return sldThreshold;
	}

	/**
	 * This method initializes txtThreshold
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getTxtThreshold() {
		if (txtThreshold == null) {
			txtThreshold = new JTextField();
			txtThreshold.setFont(new Font("Arial", Font.PLAIN, 12));
			txtThreshold.setBounds(new Rectangle(422, 203, 45, 25));
			txtThreshold.setText("250");
			txtThreshold.setEditable(false);
		}
		return txtThreshold;
	}

	private void addComponents() {
		add(lblConvLimit, null);
		add(lblEpsilon, null);
		add(lblFanout, null);
		add(lblMms, null);
		add(lblThreshold, null);
		add(lblDelta, null);
		add(lblTraceFile, null);
		add(lblInitState, null);
		add(lblNeighborList, null);
		add(lblAlgorithm, null);
		add(getTxtConvLimit(), null);
		add(getTxtEpsilon(), null);
		add(getTxtFanout(), null);
		add(getTxtMms(), null);
		add(getTxtThreshold(), null);
		add(getTxtDelta(), null);
		add(getSldConvLimit(), null);
		add(getSldEpsilon(), null);
		add(getSldFanout(), null);
		add(getSldMms(), null);
		add(getSldThreshold(), null);
		add(getSldDelta(), null);
		add(getChkCreateTraceFile(), null);
		add(getChkInitState(), null);
		add(getChkNeighborList(), null);
		add(getCmbAlgorithm(), null);
	}

	private void initLabels() {
		lblAlgorithm = new JLabel();
		lblAlgorithm.setFont(new Font("Arial", Font.BOLD, 12));
		lblAlgorithm.setBounds(new Rectangle(18, 10, 112, 23));
		lblAlgorithm.setText("Algorithm");
		lblNeighborList = new JLabel();
		lblNeighborList.setFont(new Font("Arial", Font.BOLD, 12));
		lblNeighborList.setBounds(new Rectangle(18, 373, 140, 18));
		lblNeighborList.setText("Write Neighbor List File");
		lblInitState = new JLabel();
		lblInitState.setFont(new Font("Arial", Font.BOLD, 12));
		lblInitState.setBounds(new Rectangle(18, 333, 127, 21));
		lblInitState.setText("Write Initial States File");
		lblTraceFile = new JLabel();
		lblTraceFile.setFont(new Font("Arial", Font.BOLD, 12));
		lblTraceFile.setBounds(new Rectangle(18, 293, 125, 20));
		lblTraceFile.setText("Write Trace File");
		lblDelta = new JLabel();
		lblDelta.setFont(new Font("Arial", Font.BOLD, 12));
		lblDelta.setBounds(new Rectangle(18, 243, 115, 20));
		lblDelta.setText("delta (\u0394)  %");
		lblThreshold = new JLabel();
		lblThreshold.setFont(new Font("Arial", Font.BOLD, 12));
		lblThreshold.setBounds(new Rectangle(18, 203, 115, 20));
		lblThreshold.setText("threshold (T)");
		lblMms = new JLabel();
		lblMms.setFont(new Font("Arial", Font.BOLD, 12));
		lblMms.setBounds(new Rectangle(18, 163, 115, 20));
		lblMms.setText("mms");
		lblFanout = new JLabel();
		lblFanout.setFont(new Font("Arial", Font.BOLD, 12));
		lblFanout.setBounds(new Rectangle(18, 123, 115, 20));
		lblFanout.setText("fanout");
		lblEpsilon = new JLabel();
		lblEpsilon.setFont(new Font("Arial", Font.BOLD, 12));
		lblEpsilon.setBounds(new Rectangle(18, 83, 115, 20));
		lblEpsilon.setText("epsilon (\u220A)");
		lblConvLimit = new JLabel();
		lblConvLimit.setFont(new Font("Arial", Font.BOLD, 12));
		lblConvLimit.setBounds(new Rectangle(18, 43, 115, 20));
		lblConvLimit.setToolTipText("");
		lblConvLimit.setText("convLimit");
	}
}
