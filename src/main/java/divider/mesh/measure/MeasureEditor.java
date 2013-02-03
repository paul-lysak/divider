/*
 * Created on 8/10/2006
 */

package divider.mesh.measure;
import java.util. ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import divider.Messages;

/**
 * MeasureEditor --- editing dialog for measurement system
 * @author gefox
 */
public class MeasureEditor extends divider.AbstractEditingDialog{

	/**
	 * Constructor
	 */
	public MeasureEditor() {
		super(Messages.getString("MeasureEditor.Edit_Measure_1")); //$NON-NLS-1$
		errorMessageTitle=Messages.getString("MeasureEditor.Measure_Editing_Error_2"); //$NON-NLS-1$
			measureModel = new MeasureModel();
			table = new JTable(measureModel); //create and set up table
				table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		JButton delButton = new JButton(Messages.getString("MeasureEditor.Delete_3")); //$NON-NLS-1$
		JButton addButton = new JButton(Messages.getString("MeasureEditor.Add_4")); //$NON-NLS-1$
		
		addButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event)
				{
					measureModel.lines.add(new MeasureLineModel());
					measureModel.fireTableDataChanged();
				}
			}
			);
			
		delButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event)
				{
					int rows[];
					rows = table.getSelectedRows();
					if(rows.length==0) return;
					measureModel.lines.remove(rows[0]);									
					measureModel.fireTableDataChanged();				}
			}
			);

		Box b1 = Box.createHorizontalBox();
		Box b2 = Box.createVerticalBox();
		getContentPane().add(b1, BorderLayout.CENTER);
		b1.add(new JScrollPane(table) );
		b1.add(b2);
		b2.add(delButton);
		b2.add(addButton);

		setSize(480, 280);
	}


	protected String onOk() {
		measureModel.apply(measure);
		return null;
	}

	public boolean run(Measure measure_)
	{
				   result = false;
				   measure = measure_;
				   measureModel.extract(measure);
				   show();
				   if(result)
				   		measureModel.apply(measure);
				   return result;
   }
   	
 	private JTable table;
	private Measure measure;
	private MeasureModel measureModel = new MeasureModel(); 
}






class MeasureModel extends AbstractTableModel
{
	/**
	 * get data from measure_
	 */
	void extract(Measure measure_)
	{
		lines.clear();
		int i, l;
		MeasureLine line;
		ArrayList linesList = measure_.getMeasureLines();
		l = linesList.size();
		for(i=0; i<l; i++)
		{
			line=(MeasureLine)linesList.get(i);
			lines.add( new MeasureLineModel(line));
		}
	}
	
	/**
	 * save data to measure_
	 * measure_ should be same as for extract
	 */
	void apply(Measure measure_)
	{
		ArrayList linesList = new ArrayList(3);
		MeasureLineModel lmodel;
		int i, l;
		l = lines.size();
		for(i=0; i<l; i++)
		{
			lmodel =(MeasureLineModel)lines.get(i);
			lmodel.applyToMeasureLine(measure_);
			linesList.add(lmodel.line);
		}
		measure_.removeAllLines();
		measure_.setLines(linesList);
	}

	
	public int getColumnCount() {return 6;}
	
	public int getRowCount() {return lines.size();}
		
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex>=lines.size()) return null;
		MeasureLineModel lineModel =(MeasureLineModel) lines.get(rowIndex);
		switch(columnIndex)
		{
			case 0:
				return lineModel.name;
			case 1:
				return new Double(lineModel.x1);
			case 2:
				return new Double(lineModel.y1);
			case 3:
				return new Double(lineModel.x2);
			case 4:
				return new Double(lineModel.y2);
			case 5:
				return new Double(lineModel.captureWidth);
			default:
				return "ERROR"; //$NON-NLS-1$
		}
	}//end getValueAt
	
	public void setValueAt(Object newVal, int rowIndex, int colIndex)
	{
		if(rowIndex>=lines.size()) return;
		MeasureLineModel lineModel =(MeasureLineModel) lines.get(rowIndex);
		switch(colIndex)
		{
			case 0:
				lineModel.name=(String)newVal;
				break;
			case 1:
				try{
					lineModel.x1=Double.parseDouble((String)newVal);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, 
							   (String)newVal+"is not a valid floating-point number",  //$NON-NLS-1$
							   Messages.getString("DefaultSegment.Wrong_format_24"),  //$NON-NLS-1$
							   JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 2:
				try{
				lineModel.y1=Double.parseDouble((String)newVal);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, 
							   (String)newVal+"is not a valid floating-point number",  //$NON-NLS-1$
							   Messages.getString("DefaultSegment.Wrong_format_24"),  //$NON-NLS-1$
							   JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 3:
				try{
				lineModel.x2=Double.parseDouble((String)newVal);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, 
							   (String)newVal+"is not a valid floating-point number",  //$NON-NLS-1$
							   Messages.getString("DefaultSegment.Wrong_format_24"),  //$NON-NLS-1$
							   JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 4:
				try{
				lineModel.y2=Double.parseDouble((String)newVal);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, 
							   (String)newVal+" is not a valid floating-point number",  //$NON-NLS-1$
							   Messages.getString("DefaultSegment.Wrong_format_24"),  //$NON-NLS-1$
							   JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 5:
				try{
				lineModel.captureWidth=Double.parseDouble((String)newVal);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, 
							   (String)newVal+" is not a valid floating-point number",  //$NON-NLS-1$
							   Messages.getString("DefaultSegment.Wrong_format_24"),  //$NON-NLS-1$
							   JOptionPane.ERROR_MESSAGE);
				}
				break;
		}
	}//end setValueAt
	
	
	public boolean isCellEditable(int rowIndex, int colIndex)
		{return true;}
	
	public String getColumnName(int colIndex)
	{
		int c=colIndex;
		if(c<=0) return Messages.getString("MeasureEditor.Name_6"); //$NON-NLS-1$
		c--;
		if(c<=0) return "X1"; //$NON-NLS-1$
		c--;
		if(c<=0) return "Y1"; //$NON-NLS-1$
		c--;
		if(c<=0) return "X2"; //$NON-NLS-1$
		c--;
		if(c<=0) return "Y2"; //$NON-NLS-1$
		c--;
		if(c<=0) return Messages.getString("MeasureEditor.Capture_11"); //$NON-NLS-1$
		c--;
		return "UNKNOWN COLUMN"; //$NON-NLS-1$
	}
	
	ArrayList lines = new ArrayList(3);
}//end class MeasureModel



class MeasureLineModel
{
	MeasureLineModel()
	{
	}
	
	MeasureLineModel(MeasureLine mline)
	{
		line=mline;
		name=mline.getName();
		x1=mline.begin.getX();
		y1=mline.begin.getY();
		x2=mline.end.getX();
		y2=mline.end.getY();
		captureWidth=mline.captureWidth;
	};
	
	MeasureLineModel(String name_, double x1_, double y1_, double x2_, double y2_)
	{
		line=null;
		x1=x1_; y1=y1_;
		x2=x2_; y2=y2_;
		name = name_;
	}
	
	/**
	 * Apply data from x1..y2, name to line.
	 * If line==null, create one.
	 * @param measure -- is used when need to create new line
	 */
	void applyToMeasureLine(Measure measure)
	{
		if(line==null)
			{
				line = new MeasureLine(measure, name,
									new divider.Dot(x1, y1),
									new divider.Dot(x2, y2));
				line.captureWidth=captureWidth;
			}
			else
			{ 
				line.begin=new divider.Dot(x1, y1);
				line.end=new divider.Dot(x2, y2);
				line.name=name;
				line.captureWidth=captureWidth;
			}
	}
	
	MeasureLine line=null;
	double x1=0;
	double y1=0;
	double x2=0;
	double y2=0;
	double captureWidth=1;
	String name=""; //$NON-NLS-1$
}//end class MeasureLineModel