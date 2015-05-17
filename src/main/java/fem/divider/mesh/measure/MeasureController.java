/*
 * Created on 8/10/2006
 */
package fem.divider.mesh.measure;
import javax.swing.*;
import java.awt.event.*;
//import divider.Messages;
//import java.util.ArrayList;
import javax.swing.filechooser.*;
import java.io.File;

import fem.divider.Messages;


/**
 * Contains actions for measuring system
 * @author gefox
 */
public class MeasureController {

	/**
	 * 
	 */
	public MeasureController(Measure _measure) {
		measure=_measure;
		createActions();
	}

	public JFileChooser getFileChooser()
	{
		return fem.divider.Divider.getDivider().getDividerUI().getMeasureFileChooser();
	}
	
	
	
	
	
	private void createActions()
	{
		editAction = new
		AbstractAction (Messages.getString("MeasureController.Edit_Measures_1"),   //$NON-NLS-1$
				new ImageIcon(fem.divider.Divider.class.getResource("resources/images/editMeasure.png")) //$NON-NLS-1$
				)
		{
				public void actionPerformed(ActionEvent event)
				{
					if( measureEditor.run(measure) )
						measure.getMeshPanel().redraw();
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("MeasureController.Edit_Measures_2"));  //$NON-NLS-1$
				}
		};//end editAction	

		saveAction = new
		AbstractAction (Messages.getString("MeasureController.Save_Measures_3")) //$NON-NLS-1$
		{
				public void actionPerformed(ActionEvent event)
				{
					//choose file
					JFileChooser fileChooser=getFileChooser();
					int result = fileChooser.showSaveDialog(null);
					if(result!=JFileChooser.APPROVE_OPTION) 
						{return;}
					File selFile = fileChooser.getSelectedFile();
					String fileName=selFile.getPath();
					//possibly, add extention
					int dot_i=selFile.getName().indexOf("."); //$NON-NLS-1$
					if( fileChooser.getFileFilter() instanceof MeasureFileFilter && dot_i<0 )
						{
							fileName=fileName+".measure"; //$NON-NLS-1$
						}
					MeasureStreamer.save(measure, fileName);
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("MeasureController.Save_Measures_6"));  //$NON-NLS-1$
				}
		};//end saveAction	


		loadAction = new
		AbstractAction (Messages.getString("MeasureController.Load_Measures_7")) //$NON-NLS-1$
		{
				public void actionPerformed(ActionEvent event)
				{
					//choose file
					JFileChooser fileChooser=getFileChooser();
					int result = fileChooser.showOpenDialog(null);
					if(result!=JFileChooser.APPROVE_OPTION) 
						{return;}
					File selFile = fileChooser.getSelectedFile();
					String fileName=selFile.getPath();
					MeasureStreamer.load(measure, fileName);
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("MeasureController.Save_Measures_6"));  //$NON-NLS-1$
				}
		};//end loadAction	
	}//end createActions()
	
	Measure measure;
	
	public Action loadAction;
	public Action saveAction;
	public Action editAction;
	
	
	private MeasureEditor measureEditor = new MeasureEditor();
	
}
