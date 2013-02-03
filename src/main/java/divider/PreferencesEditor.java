/*
 * Created on 17/2/2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package divider;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author gefox
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PreferencesEditor extends AbstractEditingDialog{
	public PreferencesEditor()
	{
			super("Preferences");
			numberFormatField = new JTextField(20);
			numberDecimalSeparatorField = new JTextField(2);
			numberGroupSeparatorField = new JTextField(2);
			
			numberSetDefaultButton = new JButton("Default");
			numberSetDefaultButton.addActionListener(
					new ActionListener()
							{
								public void actionPerformed(ActionEvent event)
								{
									System.out.println("Set Default");
									numberFormatField.setText(Preferences.default_numberFormat);
									numberDecimalSeparatorField.setText(Preferences.default_numberDecimalSeparator+"");
									numberGroupSeparatorField.setText(Preferences.default_numberGroupSeparator+"");
								}
							}
							);

			numberFormatField.setMinimumSize( numberFormatField.getPreferredSize() );
			numberDecimalSeparatorField.setMinimumSize( numberDecimalSeparatorField.getPreferredSize() );
			numberGroupSeparatorField.setMinimumSize( numberGroupSeparatorField.getPreferredSize() );
			
			numberFormatField.setMaximumSize( numberFormatField.getPreferredSize() );
			numberDecimalSeparatorField.setMaximumSize( numberDecimalSeparatorField.getPreferredSize() );
			numberGroupSeparatorField.setMaximumSize( numberGroupSeparatorField.getPreferredSize() );
			
			numberSetDefaultButton.setMaximumSize( numberSetDefaultButton.getPreferredSize() );
		

			GridBagLayout layout = new GridBagLayout();
			contentPanel.setLayout(layout);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.NONE;
			c.anchor=GridBagConstraints.LINE_START;
	
			c.gridx=0; c.gridy=0;
			contentPanel.add(new JLabel("Number Format:"), c);			
			c.gridx=1; c.gridy=0;
			contentPanel.add(numberFormatField, c);
		
			c.gridx=0; c.gridy=1;
			contentPanel.add(new JLabel("Decimal Separator"), c);
			c.gridx=1; c.gridy=1;
			contentPanel.add(numberDecimalSeparatorField, c); 
			
			c.gridx=0; c.gridy=2;
			contentPanel.add(new JLabel("Group Separator"), c);
			c.gridx=1; c.gridy=2;
			contentPanel.add(numberGroupSeparatorField, c);			

			c.gridx=0; c.gridy=3;
			contentPanel.add(numberSetDefaultButton, c);			
		
			setSize(380, 200);
	}
	
	protected  void setDefault()
	{
	//TODO: MAKE HERE SET TO DEFAULT
	}

	/**
	 * Action, that is performed on "ok"
	 * @returns null, if data, entered by user is good or error message otherwise
	 */
	protected String onOk() {
			result = true;
			String format, decSeparator, grpSeparator;
		
			format=numberFormatField.getText();
			decSeparator =  numberDecimalSeparatorField.getText();
			grpSeparator=  numberGroupSeparatorField.getText();

			prefs.setNumberFormat(format);
			prefs.setNumberDecimalSeparator((decSeparator+".").charAt(0));
			prefs.setNumberGroupSeparator((grpSeparator+",").charAt(0));
			
			divider.Divider.getDivider().updateNumberFormat();
			return null;
	}

	public boolean run(Preferences prefs_)
	{
			result = false;
			prefs = prefs_;

/*			leftField.setText( Double.toString(world.getLeft()));
			rightField.setText( Double.toString(world.getRight()));
			bottomField.setText( Double.toString(world.getBottom()));
			topField.setText( Double.toString(world.getTop()));
	*/	
			numberFormatField.setText( prefs.getNumberFormat());
			numberDecimalSeparatorField.setText( prefs.getNumberDecimalSeparator()+"");
			numberGroupSeparatorField.setText( prefs.getNumberGroupSeparator()+"");
			
			show();
			return result;
	}

	Preferences prefs;
	boolean result;

/*	JTextField leftField;
	JTextField rightField;
	JTextField bottomField;
	JTextField topField;
	*/
	JTextField numberFormatField;
	JTextField numberDecimalSeparatorField;
	JTextField numberGroupSeparatorField;
	JButton numberSetDefaultButton;
}
