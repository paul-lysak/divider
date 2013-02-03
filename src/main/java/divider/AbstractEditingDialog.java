/*
 * AbstractDialog.java
 *
 * Created on Monday, 21, March 2005, 18:23
 */

package divider;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *Abstract dialog to edit something.
 *Should be subclassed to use
 *In subclass constructor call constructor of AbstractEditingDialog,
 *then add controls to this
 *Add method like this:
 public boolean run(SomeClass val_)
 {
				result = false;
				val = val_;
				show();
				return result;
}
*and fields 
*SomeClass val; 
*Redefine onOk to apply entered data and to set result=true;
 * @author  gefox
 * @version
 */
public abstract class AbstractEditingDialog extends JDialog {
		
		/** Creates new AbstractDialog */
		public AbstractEditingDialog(String title) {
				super((JFrame)null, title, true);
				
				Container content= getContentPane();
				content.setLayout(new BorderLayout());
				buttonPanel = new JPanel();
				contentPanel = new JPanel();
				content.add(buttonPanel, BorderLayout.SOUTH);
				content.add(contentPanel, BorderLayout.CENTER);
				
				okButton = new JButton(Messages.getString("AbstractEditingDialog.OK")); //$NON-NLS-1$
				cancelButton = new JButton(Messages.getString("AbstractEditingDialog.Cancel")); //$NON-NLS-1$
				
				buttonPanel.add(okButton);
				buttonPanel.add(cancelButton);
				
				layout = new SpringLayout();
				contentPanel.setLayout(layout);
				
				
				okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
								result = true;
								String msg = onOk();
								if(msg==null)
								{
										setVisible(false);
										return;
								}
								System.err.println(msg);
								JOptionPane.showMessageDialog(null, msg, errorMessageTitle , JOptionPane.ERROR_MESSAGE);
						}
				}
				);
				cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
								result = false;
								setVisible(false);
						}
				}
				);
				
				setSize(340, 280);				
		}
		
		/**
		 *Action, that is performed on "ok"
		 *@returns null, if data, entered by user is good or error message otherwise
		 */
		abstract protected String onOk();
		

		protected JButton okButton;
		protected JButton cancelButton;
		protected JPanel buttonPanel;		
		protected boolean result;
		protected SpringLayout layout;
		protected JPanel contentPanel;
		protected String errorMessageTitle=Messages.getString("AbstractEditingDialog.Node_editing_error_3"); //$NON-NLS-1$
}
