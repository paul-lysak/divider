package fem.divider.figure;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import fem.divider.Messages;

class SegmentGeometryEditor extends fem.divider.AbstractEditingDialog {
	SegmentGeometryEditor() {
		super(Messages.getString("SegmentGeometryEditor.DialogTitle")); //$NON-NLS-1$
		errorMessageTitle = Messages
				.getString("SegmentGeometryEditor.EditingError"); //$NON-NLS-1$

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints constraints;
		GridBagConstraints defaultConstraints = new GridBagConstraints();
		defaultConstraints.weightx = 0;
		defaultConstraints.weighty = 0;
		defaultConstraints.gridx = 1;
		defaultConstraints.gridy = 1;
		defaultConstraints.gridwidth = 1;
		defaultConstraints.gridheight = 1;
		defaultConstraints.ipadx = 5;
		defaultConstraints.ipady = 1;
		defaultConstraints.anchor = GridBagConstraints.WEST;
		defaultConstraints.fill = GridBagConstraints.NONE;
		
		geometryTypeComboBox = new JComboBox();
		geometryTypeComboBox.addItem(Messages.getString("SegmentGeometryEditor.Line")); //$NON-NLS-1$
		geometryTypeComboBox.addItem(Messages.getString("SegmentGeometryEditor.Arc")); //$NON-NLS-1$
		geometryTypeComboBox.setMaximumSize(geometryTypeComboBox
				.getPreferredSize());

		constraints = (GridBagConstraints)defaultConstraints.clone();
		panel.add(new JLabel(Messages.getString("SegmentGeometryEditor.GeometryType")), constraints); //$NON-NLS-1$
		constraints.gridx = 2;
		constraints.weightx = 0;
		panel.add(geometryTypeComboBox, constraints);

//		arcControlsBox = Box.createVerticalBox();
		GridBagLayout arcControlsLayout = new GridBagLayout();
		arcControls = new JPanel();
		arcControls.setLayout(arcControlsLayout);
		
		radiusField = new JTextField();
		radiusField.setColumns(10);
		radiusField.setMaximumSize(radiusField.getPreferredSize());

		centerSideComboBox = new JComboBox();
		centerSideComboBox.addItem(Messages.getString("SegmentGeometryEditor.Left")); //$NON-NLS-1$
		centerSideComboBox.addItem(Messages.getString("SegmentGeometryEditor.Right")); //$NON-NLS-1$
		centerSideComboBox
				.setMaximumSize(centerSideComboBox.getPreferredSize());

		arcSideComboBox = new JComboBox();
		arcSideComboBox.addItem(Messages.getString("SegmentGeometryEditor.Left")); //$NON-NLS-1$
		arcSideComboBox.addItem(Messages.getString("SegmentGeometryEditor.Right")); //$NON-NLS-1$
		arcSideComboBox.setMaximumSize(arcSideComboBox.getPreferredSize());

		constraints = (GridBagConstraints)defaultConstraints.clone();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 20;
		arcControls.add(new JLabel(Messages.getString("SegmentGeometryEditor.Radius")), constraints); //$NON-NLS-1$
		constraints.gridx = 2;
		constraints.weightx = 30;
		arcControls.add(radiusField, constraints);
		constraints = (GridBagConstraints)defaultConstraints.clone();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 20;
		arcControls.add(new JLabel(Messages.getString("SegmentGeometryEditor.CenterSide")), constraints); //$NON-NLS-1$
		constraints.gridx = 2;
		constraints.weightx = 30;
		arcControls.add(centerSideComboBox, constraints);
		constraints = (GridBagConstraints)defaultConstraints.clone();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 20;
		arcControls.add(new JLabel(Messages.getString("SegmentGeometryEditor.ArcSide")), constraints); //$NON-NLS-1$
		constraints.gridx = 2;
		constraints.gridheight = 2;
		constraints.weightx = 30;
		arcControls.add(arcSideComboBox, constraints);

		//Make border and give minimal value for additional controls
		JPanel arcControlsContainer = new JPanel();
		Border arcControlsBorder = BorderFactory.createTitledBorder(Messages.getString("SegmentGeometryEditor.GeometryParameters")); //$NON-NLS-1$
		arcControlsContainer.setBorder(arcControlsBorder);
		arcControlsContainer.add(arcControls, BorderLayout.NORTH);
		arcControlsContainer.setPreferredSize(
			new Dimension(DEFAULT_ADDITIONAL_CONTROLS_WIDTH, DEFAULT_ADDITIONAL_CONTROLS_HEIGHT));
		arcControlsContainer.setMinimumSize(arcControlsContainer.getPreferredSize());

		constraints = (GridBagConstraints)defaultConstraints.clone();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.NORTH;
		panel.add(arcControlsContainer, constraints);
		
	
		// set listener on changing geometry type
		geometryTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (geometryTypeComboBox.getSelectedIndex() == 1)
					arcControls.setVisible(true);
				else
					arcControls.setVisible(false);
			}
		});

//		setSize(400, 300);
	}

	protected String onOk() {
		// System.out.println(geometryTypeComboBox.getSelectedIndex());
		if (geometryTypeComboBox.getSelectedIndex() == 0) {
			segment.setGeometry(new LineSegmentGeometry());
		}
		if (geometryTypeComboBox.getSelectedIndex() == 1) {
			ArcSegmentGeometry arcGeometry = new ArcSegmentGeometry();
			try {
				arcGeometry
						.setRadius(Double.parseDouble(radiusField.getText()));
				if(arcGeometry.getRadius()<0)
					return Messages.getString("SegmentGeometryEditor.NegativeForbidden")+radiusField.getText(); //$NON-NLS-1$
			} catch (NumberFormatException e) {
				return radiusField.getText()
						+ Messages
								.getString("Node._is_not_a_valid_floating-point_number_9"); //$NON-NLS-1$
			}
			if (centerSideComboBox.getSelectedIndex() == 1)
				arcGeometry.setCenterSide(arcGeometry.RIGHT_SIDE);
			else
				arcGeometry.setCenterSide(arcGeometry.LEFT_SIDE);
			if (arcSideComboBox.getSelectedIndex() == 1)
				arcGeometry.setArcSide(arcGeometry.RIGHT_SIDE);
			else
				arcGeometry.setArcSide(arcGeometry.LEFT_SIDE);

			segment.setGeometry(arcGeometry);
		}
		return null;
	}// end onOk()

	boolean run(Segment segment_) {
		segment = segment_;

		// Load geometry parameters to controls
		// For line segment
		if (segment.getGeometry() instanceof LineSegmentGeometry) {
			geometryTypeComboBox.setSelectedIndex(0);
			radiusField.setText("0.0"); //$NON-NLS-1$
			centerSideComboBox.setSelectedIndex(1);
			arcSideComboBox.setSelectedIndex(1);
		}
		// For arc segment
		if (segment.getGeometry() instanceof ArcSegmentGeometry) {
			geometryTypeComboBox.setSelectedIndex(1);
			ArcSegmentGeometry arcGeometry = (ArcSegmentGeometry) segment
					.getGeometry();
			radiusField.setText(Double.toString(arcGeometry.getRadius()));
			if (arcGeometry.getCenterSide() == ArcSegmentGeometry.RIGHT_SIDE)
				centerSideComboBox.setSelectedIndex(1);
			else
				centerSideComboBox.setSelectedIndex(0);

			if (arcGeometry.getArcSide() == ArcSegmentGeometry.RIGHT_SIDE)
				arcSideComboBox.setSelectedIndex(1);
			else
				arcSideComboBox.setSelectedIndex(0);
		}

		show();
		// tableModel.fireTableCellUpdated(table.getSelectedRow(),
		// table.getSelectedColumn());
		// if(result)//apply result if OK button pressed
		// segment.setCZones(tmpCZones);
		return result;
	}

	private static final int DEFAULT_ADDITIONAL_CONTROLS_WIDTH = 240;
	private static final int DEFAULT_ADDITIONAL_CONTROLS_HEIGHT = 150;
	
	private JComboBox geometryTypeComboBox;
	private JPanel arcControls;
	private JTextField radiusField;
	private JComboBox centerSideComboBox;
	private JComboBox arcSideComboBox;

	private Segment segment;

}// end of class CZonesEditDialog
