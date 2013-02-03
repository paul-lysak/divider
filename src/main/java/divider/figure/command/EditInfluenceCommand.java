/*
 * Created on 15/3/2006
 */
package divider.figure.command;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import divider.figure.CZone;
import divider.Messages;

/**
 * @author gefox
 */
public class EditInfluenceCommand extends AbstractCommand{
//	public EditInfluenceCommand(divider.Divider _divider, divider.figure.Figure _fig)
	public EditInfluenceCommand(divider.figure.Figure _fig)
	{
		super(_fig);
		figure=_fig;
		modelBefore = new FigureInfluenceModel(figure);
		modelAfter = (FigureInfluenceModel)modelBefore.clone();
	} 
	
	/**
	 * @see divider.command.Command#execute()
	 */
	boolean execute() {
		boolean ok_to_apply = influenceEditor.run(modelAfter);
		if(ok_to_apply)
		{
			modelAfter.apply(true);
//			divider.getDividerUI().getFigurePanel().redraw();
//			divider.getDividerUI().getMeshPanel().redraw();
			figure.redraw();
			figure.redrawMesh();
		}
		return ok_to_apply;
	}
	
	
	private static FigureInfluenceEditor influenceEditor = new FigureInfluenceEditor();
	
	divider.figure.Figure figure;
	FigureInfluenceModel modelBefore, modelAfter;

	/**
	 * @see divider.command.Command#isUndoable()
	 */
	public boolean isUndoable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("EditInfluenceCommand.edit_influence_1"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		modelBefore.apply(false);
		figure.redraw();
		figure.redrawMesh();
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		modelAfter.apply(false);
		figure.redraw();
		figure.redrawMesh();
	}
}//end of EditInfluenceCommand class


/**
 * Model of contact group
 */
class CGroupInfluenceModel implements Cloneable
{
	private CGroupInfluenceModel() //empty constructor for private usage only
	{}

	/**
	 * This constructor creates model with parameters, taken from czone
	 */
	public CGroupInfluenceModel(divider.figure.CZone czone)
	{
		groupName=czone.getGroupName();
		influence_mode=czone.getInfluenceMode();
		
		forbid_x_motion=czone.isForbidXMotion();
		forbid_y_motion=czone.isForbidYMotion();
		
		force_zero_direction=czone.getForceZeroDirection();
		force_direction=czone.getForceDirection();
		force_value=czone.getForceValue();
	}
	
	/**
	 * Creates clone of itself
	 */
	public Object clone()
	{
		CGroupInfluenceModel cgim = new CGroupInfluenceModel();
		cgim.groupName=groupName;
		cgim.influence_mode=influence_mode;
		
		cgim.forbid_x_motion=forbid_x_motion;
		cgim.forbid_y_motion=forbid_y_motion;
		
		cgim.force_zero_direction=force_zero_direction;
		cgim.force_direction=force_direction;
		cgim.force_value=force_value;

		return cgim;  
	}
	
	
	public void applyTo(divider.figure.CZone czone)
	{
		czone.setInfluenceMode(influence_mode);
		
		czone.setForbidXMotion(forbid_x_motion);
		czone.setForbidYMotion(forbid_y_motion);
		
		czone.setForceZeroDirection(force_zero_direction);
		czone.setForceDirection(force_direction);
		czone.setForceValue(force_value);
	}
	
	public String getGroupName() {return groupName;}
	public void setGroupName(String val) {groupName=val; setChanged(true);}
	
	public int getInfluenceMode() {return influence_mode;}
	public void setinfluenceMode(int val) {influence_mode=val; setChanged(true);}
	
	//for contact 
	public boolean isForbidXMotion() {return forbid_x_motion;}
	public void setForbidXMotion(boolean val) {forbid_x_motion=val;  setChanged(true);}
	public boolean isForbidYMotion() {return forbid_y_motion;}
	public void setForbidYMotion(boolean val) {forbid_y_motion=val;  setChanged(true);}

	//for force
	public int getForceZeroDirection() {return force_zero_direction;}
	public void setForceZeroDirection(int val) {force_zero_direction=val;  setChanged(true);}

	public double  getForceDirection() {return force_direction;}
	public void setForceDirection(double val) {force_direction=val;  setChanged(true);}
	public double  getForceDirectionDeg() //same in degres 
			{return force_direction*180/Math.PI;}
	public void setForceDirectionDeg(double val) 
			{force_direction=val*Math.PI/180; setChanged(true);}
	public double  getForceValue() {return force_value;}
	public void setForceValue(double val) {force_value=val;  setChanged(true);}	
	
	//traces changes
	public boolean isChanged() {return changed;}
	public void setChanged(boolean val) {changed=val;}
	
	private String groupName;
	private int influence_mode;
	
	private boolean forbid_x_motion, forbid_y_motion;
	
	private int force_zero_direction;
	private double force_direction, force_value;
	
	private boolean changed=false;
}//end of CGroupInfluenceModel class


/**
 * Model of figure influences
 */
class FigureInfluenceModel  implements Cloneable
{
	//empty constructor for private usage only
	private FigureInfluenceModel()
	{
	}  
	
	/**
	 * This constructor creates model with data, taken from from figure
	 */
	public FigureInfluenceModel(divider.figure.Figure _figure)
	{
		figure=_figure;
		CZone czone, czone_later;
		
		ArrayList czones = figure.getCZones(null, 0);
		int maxCGroups=czones.size();
		int cz_i, i;
		for(cz_i=0; cz_i<maxCGroups; cz_i++) //for CZones, that may belong to unique CGroup
		{
			czone = (CZone)czones.get(cz_i); //we've got czone of unique cgroup
			cgroups.add( new CGroupInfluenceModel(czone) );  //add this group's model to list
			for(i=cz_i+1; i<maxCGroups; ) //for czones after this czone. Search cgroup repetition
			{
				czone_later = (CZone)czones.get(i);
				if( czone_later.getGroupName().equals(czone.getGroupName()) )
				{//repetition found, replace this czone with last possible and continue
					czone_later = (CZone)czones.get(maxCGroups-1); //get last possible
					czones.set(i, czone_later); //replace current with last
					maxCGroups--; //one possible group less
					//now replaced zone will be revised again
				}
				else
				{//no repetition, go on
					i++;
				}
			}
		}//end of for czones
	}//end of FigureInfluenceModel constructor
	
	/**
	 * Creates clone of itself
	 */
	public Object clone()
	{
		FigureInfluenceModel new_model = new FigureInfluenceModel();
		new_model.figure = figure;
		
		CGroupInfluenceModel gim;
		int i, n;
		n = cgroups.size();
		for(i=0; i<n; i++) //clone all cgroups models to new model of figure
		{
			gim = (CGroupInfluenceModel)cgroups.get(i);
			new_model.cgroups.add( gim.clone() );			
		}
		
		return new_model;
	}
	
	/**
	 *Writes data from model to figure
	 *@param onlyChanged -- apply only changed groups 
	 */
	public void apply(boolean onlyChanged)
	{
		ArrayList czones = figure.getCZones(null, 0);
		CGroupInfluenceModel cgroup;
		CZone czone;
		
		int g_i, z_i,  g_n, z_n;
		g_n=cgroups.size(); z_n=czones.size();
		for(g_i=0; g_i<g_n; g_i++)
		{
			cgroup=(CGroupInfluenceModel)cgroups.get(g_i);
			if( onlyChanged&&!cgroup.isChanged() ) continue; //nothing to do if this group isn't changed
			for(z_i=0; z_i<z_n; z_i++)
			{
				czone = (CZone)czones.get(z_i);
				if( czone.getGroupName().equals( cgroup.getGroupName() ) )
					cgroup.applyTo(czone);
			}
		}
	}//end of apply method
	
	public divider.figure.Figure getFigure() {return figure;}
	public ArrayList getCGroups() {return cgroups;}
	
	private divider.figure.Figure figure;
	private ArrayList cgroups = new ArrayList(2);
}//end of FigureInfluenceModel class



/**
 * Dialog for editing influence
 */
class FigureInfluenceEditor extends divider.AbstractEditingDialog
   {
		FigureInfluenceEditor()
		{
			super( Messages.getString("EditInfluenceCommand.Edit_Influences_2")); //$NON-NLS-1$
			errorMessageTitle=Messages.getString("EditInfluenceCommand.Error_while_editing_influences_3"); //$NON-NLS-1$
			tableModel = new InfluenceTableModel(this);
			table = new JTable(tableModel); //create and set up table
				table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
				//install influence mode editor			
				TableColumn influenceModeCol = table.getColumnModel().getColumn(1);
				influenceModeComboBox= new JComboBox();
				influenceModeComboBox.addItem(CZone.INFLUENCE_MODE_DESCRIPTION[0]);
				influenceModeComboBox.addItem(CZone.INFLUENCE_MODE_DESCRIPTION[1]);
				influenceModeComboBox.addItem(CZone.INFLUENCE_MODE_DESCRIPTION[2]);
				influenceModeComboBox.addItem(CZone.INFLUENCE_MODE_DESCRIPTION[3]);
				influenceModeCol.setCellEditor(new DefaultCellEditor(influenceModeComboBox));

				//install force zero direction editor
				TableColumn forceZeroDirectionCol = table.getColumnModel().getColumn(4);
				forceZeroDirectionComboBox= new JComboBox();
				forceZeroDirectionComboBox.addItem(CZone.FORCE_ZERO_DIRECTION_DESCRIPTION[0]);
				forceZeroDirectionComboBox.addItem(CZone.FORCE_ZERO_DIRECTION_DESCRIPTION[1]);
				forceZeroDirectionCol.setCellEditor(new DefaultCellEditor(forceZeroDirectionComboBox));

				//install forbid_x_motion editor
				/*
				 TableColumn fixXCol = table.getColumnModel().getColumn(2);
				fixXCheckBox = new JCheckBox();
				fixXCol.setCellEditor(new DefaultCellEditor(fixXCheckBox));
				*/


			Box b1 = Box.createVerticalBox();
			getContentPane().add(b1, BorderLayout.CENTER);
			b1.add(new JScrollPane(table) );

			setSize(600, 200);
		}
			
		protected String onOk()
		{
			//System.out.println("Influence OK");
			return null;
		}
			
		/**
		 * Run the figure influence editing dialog
		 */
		boolean run(FigureInfluenceModel _influenceModel)
		{
			influenceModel = _influenceModel;
			tableModel.setFigureInfluence(influenceModel);

			tableModel.fireTableDataChanged();
			show();
			
			tableModel.setFigureInfluence(null);
			return result;
		}

		JTable table;
		JComboBox influenceModeComboBox;
		JComboBox forceZeroDirectionComboBox;
		//JCheckBox fixXCheckBox, fixYCheckBox;
		
		FigureInfluenceModel influenceModel;
		InfluenceTableModel tableModel;
		
		
		/**
		 * it's an adapter for FigureInfluenceModel, to edit it with table 
		 */
		private class InfluenceTableModel extends AbstractTableModel
		{
			public InfluenceTableModel(FigureInfluenceEditor _dialog)
			{
				dialog=_dialog;
			}
			
			/**
			 * set FigureInfluenceModel instance, to which instance of this class is an adapter
			 * null means having no model 
			 */
			public void setFigureInfluence(FigureInfluenceModel fi)
			{
				figureInfluence=fi;
			}
			
			public int getColumnCount() {
				return 7;
			}//end of getColumnCount

			public Class getColumnClass(int col)
			{
				if(col==2||col==3) return Boolean.class;
				return Object.class;
			}//end of getColumnClass

			public String getColumnName(int col)
			{
				switch(col)
				{
					case 0: return Messages.getString("EditInfluenceCommand.Group_Name_4"); //$NON-NLS-1$
					case 1: return Messages.getString("EditInfluenceCommand.Influence_5"); //$NON-NLS-1$
					case 2: return Messages.getString("EditInfluenceCommand.Fix_X_6"); //$NON-NLS-1$
					case 3: return Messages.getString("EditInfluenceCommand.Fix_Y_7"); //$NON-NLS-1$
					case 4: return Messages.getString("EditInfluenceCommand.Zero_Direction_8"); //$NON-NLS-1$
					case 5: return Messages.getString("EditInfluenceCommand.Direction_9"); //$NON-NLS-1$
					case 6: return Messages.getString("EditInfluenceCommand.Force_10"); //$NON-NLS-1$
					default: return null;
				}
			}//end of getColumnName

			public int getRowCount() {
				if(figureInfluence==null)
						return 0;
					else
						{
							return figureInfluence.getCGroups().size();
						}
			}//end of getRowCount

			public Object getValueAt(int rowIndex, int columnIndex) {
				ArrayList groups = figureInfluence.getCGroups();
				CGroupInfluenceModel group = (CGroupInfluenceModel)groups.get(rowIndex);
				
				switch(columnIndex)
				{
					case 0: return group.getGroupName();
					case 1: 
						return CZone.INFLUENCE_MODE_DESCRIPTION[
							group.getInfluenceMode()];
					case 2: 
						return new Boolean(group.isForbidXMotion());
					case 3: 
						return new Boolean(group.isForbidYMotion());
					case 4: 
						return CZone.FORCE_ZERO_DIRECTION_DESCRIPTION[
							group.getForceZeroDirection()];
					case 5: return new Double(group.getForceDirectionDeg());
					case 6: return new Double(group.getForceValue());
					default: return null;
				}
			}//end of getValueAt


			public void setValueAt(Object newValue, int rowIndex, int columnIndex)
			{
				ArrayList groups = figureInfluence.getCGroups();
				CGroupInfluenceModel group = (CGroupInfluenceModel)groups.get(rowIndex);
				String str;
				
				switch(columnIndex)
				{
					case 1: //influence mode
//						System.out.println(newValue);
						if(dialog.influenceModeComboBox.getSelectedIndex()==-1) break;
						group.setinfluenceMode( dialog.influenceModeComboBox.getSelectedIndex() );
						break;
					case 2: //fix x
						group.setForbidXMotion( ((Boolean)newValue).booleanValue() );
						break;
					case 3: //fix y
						group.setForbidYMotion( ((Boolean)newValue).booleanValue() );
						break;
					case 4: //zero direction
						if(dialog.forceZeroDirectionComboBox.getSelectedIndex()==-1) break;
						group.setForceZeroDirection( dialog.forceZeroDirectionComboBox.getSelectedIndex() );
						break;
					case 5: //force direction
						try
						{
								double val  = Double.parseDouble( (String)newValue );
								group.setForceDirectionDeg(val);
						}
						catch(NumberFormatException e)
						{
								 JOptionPane.showMessageDialog(null, 
											(String)newValue+Messages.getString("EditInfluenceCommand._is_not_a_valid_positive_floating-point_number_11"),  //$NON-NLS-1$
											Messages.getString("EditInfluenceCommand.Wrong_format_12"),  //$NON-NLS-1$
											JOptionPane.ERROR_MESSAGE);
						}
						break;
					case 6: //force value
						try
						{
								double val  = Double.parseDouble( (String)newValue );
								group.setForceValue(val);
						}
						catch(NumberFormatException e)
						{
								 JOptionPane.showMessageDialog(null, 
											(String)newValue+Messages.getString("EditInfluenceCommand._is_not_a_valid_positive_floating-point_number_13"),  //$NON-NLS-1$
											Messages.getString("EditInfluenceCommand.Wrong_format_14"),  //$NON-NLS-1$
											JOptionPane.ERROR_MESSAGE);
						}
						break;					
				}
			}//end of setValueAt


			
			public boolean isCellEditable(int row, int column)
			{
				ArrayList groups = figureInfluence.getCGroups();
				CGroupInfluenceModel group = (CGroupInfluenceModel)groups.get(row);
				
				if(column==1)  return true; //influence mode always can be edited
				if( (column==2||column==3)&&group.getInfluenceMode()==CZone.INFLUENCE_CONTACT )
					return true;
				if( (column==4||column==5||column==6)&&
					(group.getInfluenceMode()==CZone.INFLUENCE_FORCE ||
					group.getInfluenceMode()==CZone.INFLUENCE_DISTRIBUTED_FORCE)
					)
					return true;
				return false; //others are non-editable
			}//end of isCellEditable
			
			FigureInfluenceModel figureInfluence = null;
			FigureInfluenceEditor dialog;
		}//end of InfluenceTableModel class
   }//end of FigureInfluenceEditor class
