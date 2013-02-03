/*
 * MeshSettings.java
 *
 * Created on Monday, 28, March 2005, 18:56
 */

package divider.mesh;
import javax.swing.*;
import java.text.MessageFormat;
import divider.Messages;

/**
 *
 * @author  gefox
 * @version 
 */
public class MeshSettings {

		/** Creates new MeshSettings */
    public MeshSettings() {
    }

		public boolean edit()
		{
				boolean result =  dialog.run(this);
				return result;
		
		}
		
		public double getMinAngle()
		{
				return minAngle;
		}
		
		public void setMinAngle(double angle)
		{
				minAngle = angle;
		}
		
		public double getMaxArea()
		{
				return maxArea;
		}
		
		public void setMaxArea(double area)
		{
				maxArea = area;
		}
		
		public double getMinArea()
		{
				return minArea;
		}
		
		public void setMinArea(double area)
		{
				minArea = area;
		}
		
		public double getCurveQuantLength() {
			return Math.sqrt(4*getMaxArea()/Math.sqrt(3));
		}
				
		double minAngle=Math.toRadians(15);
		double maxArea=100;
//		double maxRadius = 10;
		double minArea=10;
		
		private static MeshSettingsEditingDialog dialog  = 
				new MeshSettingsEditingDialog();
}




class MeshSettingsEditingDialog extends divider.AbstractEditingDialog
{
		public MeshSettingsEditingDialog()
		{
				super(Messages.getString("MeshSettings.Edit_meshdown_settings_1")); //$NON-NLS-1$
				
				JLabel l1 = new JLabel(Messages.getString("MeshSettings.Max._element_area__2")); //$NON-NLS-1$
				maxAreaField = new JTextField(15);
				JLabel l2 = new JLabel(Messages.getString("MeshSettings.Min._element_area__3")); //$NON-NLS-1$
				minAreaField = new JTextField(15);
				JLabel l3 = new JLabel(Messages.getString("MeshSettings.Min._element_angle(deg.)__4")); //$NON-NLS-1$
				minAngleField = new JTextField(15);

				maxAreaField.setMaximumSize( maxAreaField.getPreferredSize() );
				minAreaField.setMaximumSize( minAreaField.getPreferredSize() );
				minAngleField.setMaximumSize( minAngleField.getPreferredSize() );

				contentPanel.setLayout( new BoxLayout(contentPanel, BoxLayout.Y_AXIS) );
				Box b1 = Box.createHorizontalBox();
				Box b2 = Box.createHorizontalBox();
				Box b3 = Box.createHorizontalBox();
				
				b1.add(l1); b1.add(maxAreaField);
				b2.add(l2); b2.add(minAreaField);
				b3.add(l3); b3.add(minAngleField);
				
				contentPanel.add(b1);
				contentPanel.add(b2);
				contentPanel.add(b3);
				contentPanel.add( Box.createGlue() );
				setSize(420, 140);
		}
		
		protected String onOk()
		{
				double maxArea, minArea, minAngleD;
				
				try
				{
						maxArea = Double.parseDouble( maxAreaField.getText() );
				}
				catch(NumberFormatException e)
				{
						return maxAreaField.getText()+Messages.getString("MeshSettings._is_not_a_valid_floating-point_number_5"); //$NON-NLS-1$
				}

				try
				{
						minArea = Double.parseDouble( minAreaField.getText() );
				}
				catch(NumberFormatException e)
				{
						return minAreaField.getText()+Messages.getString("MeshSettings._is_not_a_valid_floating-point_number_5"); //$NON-NLS-1$
				}

				try
				{
						minAngleD = Double.parseDouble( minAngleField.getText() );
				}
				catch(NumberFormatException e)
				{
						return minAngleField.getText()+Messages.getString("MeshSettings._is_not_a_valid_floating-point_number_5"); //$NON-NLS-1$
				}

				if(minArea>maxArea)
				{
						return Messages.getString("MeshSettings.min._area_can__t_be_greater_than_max._area_!_8"); //$NON-NLS-1$
				}

				if(minAngleD > 30)
				{
//						MessageFormat msgF =
//							new MessageFormat("min. angle={0} is too severe criteria. Reduce it to 30 or less.");
					Double[] val={new Double(minAngleD)};
					return MessageFormat.format(Messages.getString("MeshSettings.min._angle_{0}_is_too_severe_criteria._Reduce_it_to_30_or_less._9"),  //$NON-NLS-1$
							val);
//					return "min. angle="+minAngleD+" is too severe criteria. Reduce it to 30 or less.";
				}
				
				//allpy settings
				meshSettings.setMaxArea(maxArea);
				meshSettings.setMinArea(minArea);
				meshSettings.setMinAngle( Math.toRadians(minAngleD) );
				
				return null;
		}

		public boolean run(MeshSettings meshSettings_)
		{
				result = false;
				meshSettings = meshSettings_;

				maxAreaField.setText( Double.toString(meshSettings.getMaxArea()) );
				minAreaField.setText( Double.toString(meshSettings.getMinArea()) );
				minAngleField.setText( 
						Double.toString( Math.toDegrees(meshSettings.getMinAngle()) )
						);
				
				show();
				return result;
		}

		private boolean result;
		private MeshSettings meshSettings;
		
		private JTextField maxAreaField;
		private JTextField minAreaField;
		private JTextField minAngleField;
}
