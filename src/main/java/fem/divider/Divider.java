/*
 * Divider.java
 *
 * Created on Saturday, 19, March 2005, 10:45
 */

package fem.divider;

//import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import fem.divider.figure.Figure;
import fem.divider.figure.FigureStreamer;
import fem.divider.figure.command.CreateContourCommand;
import fem.divider.figure.command.EditInfluenceCommand;
import fem.divider.figure.command.CreateAirFrameCommand;
import fem.divider.figure.command.RedoAction;
import fem.divider.figure.command.UndoAction;
import fem.divider.mesh.MeshFileFilter;
import fem.divider.mesh.MethodDefault;
import fem.divider.mesh.MethodRRegular;

/**
 *
 * @author  gefox
 * @version 
 */
public class Divider {
   public static final double DEFAULT_WORLD_LEFT = -10;
   public static final double DEFAULT_WORLD_RIGHT = 100;
   public static final double DEFAULT_WORLD_BOTTOM = -10;
   public static final double DEFAULT_WORLD_TOP = 100;
   
   Action newFigureAction;
   Action openFigureAction;
   Action saveFigureAction;
   Action exitAction;
   Action createContourAction;
   Action editWorldAction;
   Action createAirFrameAction;
   Action editMeshSettingsAction;
   Action editPreferencesAction;
   Action editInfluenceAction;
   Action meshdownAction;
   Action saveMeshAction;
   UndoAction undoAction;
   RedoAction redoAction;
   
   DividerUI dividerUI;
   World world;
   Figure figure;
   MeshdownActor meshdownActor;
   String[] cmdLine;
   CmdLineDetails cmdLineDetails=null;
   fem.divider.mesh.Mesh lastMesh = null;
   fem.divider.mesh.measure.Measure measure;   
   
		/** Creates new Divider */
    public Divider(String args[]) {
    			divider=this;
    			cmdLine=args;
    			cmdLineDetails=new CmdLineDetails( args );
				if( cmdLineDetails.locale!=null )
    				Messages.setLocale( new java.util.Locale(cmdLineDetails.locale));
				world = new World(DEFAULT_WORLD_LEFT, DEFAULT_WORLD_RIGHT,
								DEFAULT_WORLD_BOTTOM, DEFAULT_WORLD_TOP);
				if( cmdLineDetails.figureFile!=null )
						openFigure( cmdLineDetails.figureFile );
//					else
				if( figure==null )
						figure = new Figure();
//				meshSettings = new divider.mesh.MeshSettings();
				createActions();
				undoAction.setCommandStack( figure.getCommandStack() );
				redoAction.setCommandStack( figure.getCommandStack() );
				preferences = new Preferences(this);
				preferences.load();//load preferences from storage
				measure = new fem.divider.mesh.measure.Measure();
				meshdownActor = new MeshdownActor(this);
				meshdownActor.setMethods(//here are method that are used in program
						new fem.divider.mesh.MethodAbstract[] {
						MethodDefault.getInstance(),
						MethodRRegular.getInstance()
						});
				meshdownActor.setMethodIndex(preferences.getMeshdownMethodIndex());
				dividerUI = new DividerUI(this);
				dividerUI.show();
				measure.setMeshPanel(dividerUI.meshPanel);
				dividerUI.meshPanel.setMeasure(measure);
				updateNumberFormat();
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
	
				new Divider(args);
    }

		public void exit(int code)
		{
				preferences.save();
				dividerUI=null;
				System.exit(code);
		}

		//bring number format settings in order with preferences
		public void updateNumberFormat()
		{
			decimalFormat.applyPattern(preferences.getNumberFormat());                                         
			decimalFormatSymbols.setDecimalSeparator(preferences.getNumberDecimalSeparator());                                           
			decimalFormatSymbols.setGroupingSeparator(preferences.getNumberGroupSeparator());                                          
			decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);			
		}
		
		//format double number according to actual settings
		public String formatNumber(double val)
		{
			return decimalFormat.format(val);
		}
		
		public static String formatNumberS(double val)
		{return getDivider().formatNumber(val);
		}

		private void setFigure(Figure fig)
		{
				figure = fig;
				if(dividerUI!=null)
				{
					dividerUI.updateFigure();
					dividerUI.figurePanel.redraw();
				}
		}
		
		public Figure getFigure()
		{
			return figure;
		}

		public DividerUI getDividerUI() {return dividerUI;}

		public static Divider getDivider() {return divider;}
		
		public Preferences getPreferences() {return preferences;}
		
		public void openFigure(String filename)
		{
			FileInputStream in;
			try
			{
					 in = new FileInputStream(filename);
			}
			catch(FileNotFoundException e)
			{
					JOptionPane.showMessageDialog(null, 
					Messages.getString("Divider.File_not_found")+filename, //$NON-NLS-1$
					Messages.getString("Divider.Open_failed_2"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					return;
			}
			Figure new_fig = FigureStreamer.open(world, in);
			if(new_fig!=null) 
			{
					setFigure(new_fig);
					if(undoAction!=null)
						undoAction.setCommandStack( figure.getCommandStack() );
					if(redoAction!=null)
						redoAction.setCommandStack( figure.getCommandStack() );
					if(dividerUI!=null)
						{
							dividerUI.switchToFigure();
							dividerUI.setStatusbarText(Messages.getString("Divider.Open_figure_success___3")+filename); //$NON-NLS-1$
//							dividerUI.getFileChooser().setSelectedFile(new File(filename));
						}
			}
			else
					JOptionPane.showMessageDialog(null, 
					Messages.getString("Divider.Unable_to_open_this_file_4"), //$NON-NLS-1$
					Messages.getString("Divider.Open_failed_5"), JOptionPane.ERROR_MESSAGE);			 //$NON-NLS-1$
		}//end openFigure(String filename)
		
		
		
		
		private void createActions()
		{
		newFigureAction = new
		AbstractAction (Messages.getString("Divider.New_figure_6"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/stock_new_16.png")) //$NON-NLS-1$
				)
		{
				/**
          * 
          */
         private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						Figure newFigure = new Figure();
						setFigure( newFigure );
						dividerUI.switchToFigure();
						dividerUI.setStatusbarText(Messages.getString("Divider.Figure_created_8")); //$NON-NLS-1$
						undoAction.setCommandStack( newFigure.getCommandStack() );
						redoAction.setCommandStack( newFigure.getCommandStack() );
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Create_new_figure_9")); //$NON-NLS-1$
				}
		};
			
		
		openFigureAction = new
		AbstractAction (Messages.getString("Divider.Open_Figure_10"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/stock_open_16.png")) //$NON-NLS-1$
				)
		{
		      private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						if( dividerUI.getFileChooser().showOpenDialog(null) ==
										JFileChooser.CANCEL_OPTION) return; //cancel opening
						String filename = dividerUI.getFileChooser().getSelectedFile().getPath();
						openFigure(filename);
						if( figure.isNonEmpty() )
						   divider.createAirFrameAction.setEnabled(true);
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Open_figure_12")); //$NON-NLS-1$
				}
		};
		
		
		saveFigureAction = new
		AbstractAction (Messages.getString("Divider.Save_Figure_13"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/stock_save_16.png")) //$NON-NLS-1$
				)
		{

         private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						if(dividerUI.getFileChooser().showSaveDialog(null) == 
								JFileChooser.CANCEL_OPTION) return; //cancel saving
						String filename = dividerUI.getFileChooser().getSelectedFile().getPath();
						FileOutputStream out;
						try
						{
						out = new FileOutputStream(filename);
						}
						catch(FileNotFoundException e)
						{
								JOptionPane.showMessageDialog(null, Messages.getString("Divider.Can__t_save_to_file__15")+filename,  //$NON-NLS-1$
								Messages.getString("Divider.Save_failed_16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
								return;
						}
						String msg = FigureStreamer.save(figure, out);
						if(msg == null) //all right
						{
								dividerUI.setStatusbarText(Messages.getString("Divider.Figure_save_success___17")+filename); //$NON-NLS-1$
								return;
						}
						//we've got an error message
						JOptionPane.showMessageDialog(null, msg, Messages.getString("Divider.Save_failed_18"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Save_figure_19")); //$NON-NLS-1$
				}
		};//end saveFigureAction
		
		exitAction = new
		AbstractAction (Messages.getString("Divider.Exit_20"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/stock_exit.png")) //$NON-NLS-1$
				)
		{
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						exit(0);
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Exit_application_22")); //$NON-NLS-1$
				}
		};//end exitAction
		
		createContourAction = new
		AbstractAction(Messages.getString("Divider.Create_Contour_23"), //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/createContour.png")) //$NON-NLS-1$
				)
		{
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						dividerUI.switchToFigure();
						if(getFigure().getCommandStack().doNewCommand(new CreateContourCommand(figure)))
						{
							figure.panel.redraw();
							dividerUI.setStatusbarText(Messages.getString("Divider.Contour_created_25")); //$NON-NLS-1$
						}
						if( figure.isNonEmpty() )
                     divider.createAirFrameAction.setEnabled(true);
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Create_new_contour_26")); //$NON-NLS-1$
				}
		};//end createContourAction
		
		editWorldAction = new
		AbstractAction (Messages.getString("Divider.Edit_world_27"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/editWorld.png")) //$NON-NLS-1$
				)
		{
         private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						world.edit();
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Edit_limits_of_world_29")); //$NON-NLS-1$
				}
		};//end editWorldAction

		
		editMeshSettingsAction = new
		AbstractAction (Messages.getString("Divider.Edit_meshdown_settings_30"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/editMeshSettings.png")) //$NON-NLS-1$
				)
		{
         private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						figure.getMeshSettings().edit();
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Edit_meshdown_settings_32")); //$NON-NLS-1$
				}
		};//end editMeshSettingsAction

		createAirFrameAction = new 
		AbstractAction ( Messages.getString("Divider.Create_air_frame_1"), 
				new ImageIcon(Divider.class.getResource("resources/images/createAirFrame.png"))
			)
		{
         private static final long serialVersionUID = 1L;

         public void actionPerformed(ActionEvent event){
				dividerUI.setStatusbarText("Add air");
				dividerUI.switchToFigure();
            if(getFigure().getCommandStack().doNewCommand(new CreateAirFrameCommand(figure, dividerUI)))
            {
               figure.panel.redraw();
               dividerUI.setStatusbarText("Select parameters for the air frame");
            }
			}
			{
				putValue(Action.SHORT_DESCRIPTION, "Generate frame as box (by to opposite points)");
			}
		};// end createAirFrameAction
		createAirFrameAction.setEnabled(false);


		editPreferencesAction = new
		AbstractAction ("Preferences" //,
				//new ImageIcon(Divider.class.getResource("resources/images/editMeshSettings.png")) //$NON-NLS-1$
				)
		{
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						if( preferencesEditor.run(preferences) )
							dividerUI.setStatusbarText("Preferences applied");
						else
							dividerUI.setStatusbarText("Failed to apply preferences");
				}
				{
						putValue(Action.SHORT_DESCRIPTION, "Preferences");
				}
		};//end editPreferencesAction

		editInfluenceAction = new
		AbstractAction (Messages.getString("Divider.Edit_Influence_33"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/Influence.png")) //$NON-NLS-1$
				)
		{
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
//						commandStack.doNewCommand(new EditInfluenceCommand(divider, figure));
						getFigure().getCommandStack().doNewCommand(new EditInfluenceCommand(figure));
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Edit_influence_35")); //$NON-NLS-1$
				}
		};//end editInfluenceAction


		meshdownAction = new
		AbstractAction (Messages.getString("Divider.Meshdown_36"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/createMesh.png")) //$NON-NLS-1$
				)
		{
		      private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						dividerUI.switchToMesh();
						meshdownActor.start();
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Meshdown_figure_38")); //$NON-NLS-1$
				}
		};//end MeshdownAction

		
		saveMeshAction = new
		AbstractAction (Messages.getString("Divider.Save_mesh_39"),  //$NON-NLS-1$
				new ImageIcon(Divider.class.getResource("resources/images/saveMesh.png")) //$NON-NLS-1$
				)
		{
				/**
          * 
          */
         private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent event)
				{
						if(dividerUI.getMeshFileChooser().showDialog( null, Messages.getString("Divider.Save_mesh_41")) ==  //$NON-NLS-1$
								JFileChooser.CANCEL_OPTION) return; //cancel saving

						
						FileFilter filter = dividerUI.getMeshFileChooser().getFileFilter();
						if(filter instanceof MeshFileFilter)
							{String filename = dividerUI.getMeshFileChooser().getSelectedFile().getPath();
							filename = ((MeshFileFilter)filter).ensureExtension(filename);
							boolean allRight = true;
							try{
								((MeshFileFilter)filter).getMeshStreamer().save(lastMesh, filename);
							}
							catch(Exception e)
							{
								allRight = false;
								JOptionPane.showMessageDialog(null, e.getMessage(),  //$NON-NLS-1$ //$NON-NLS-2$
										Messages.getString("Divider.Save_failed_45"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
								dividerUI.setStatusbarText("Failed to save mesh");								
							}
							if(allRight)
								dividerUI.setStatusbarText(Messages.getString("Divider.Mesh_saved___63")+filename+".*"); //$NON-NLS-1$ //$NON-NLS-2$
							}//end if filter instanceof MeshFileFilter (true)
						else
							throw new RuntimeException("ERROR:meshFileChooser has file filter another than MeshFileFilter or its subclass");
							
				}
				{
						putValue(Action.SHORT_DESCRIPTION, Messages.getString("Divider.Save_mesh_into_files_65")); //$NON-NLS-1$
				}
		}; //end of saveMeshAction

		undoAction=new UndoAction();
		redoAction=new RedoAction();
		}//end createActions()

		private DecimalFormat decimalFormat = new DecimalFormat();
		private DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
	   private Preferences preferences;
	   private PreferencesEditor preferencesEditor=new PreferencesEditor();
		private static Divider divider;		
}
