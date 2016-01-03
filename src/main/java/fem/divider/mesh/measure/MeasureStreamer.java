/*
 * Created on 9/10/2006
 */
package fem.divider.mesh.measure;
import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

import fem.divider.Messages;
import fem.divider.mesh.Element;
import fem.geometry.Dot;

/**
 * MeasureStreamer --- contains methods to save measure-related data
 * @author gefox
 */
public class MeasureStreamer {

	private MeasureStreamer() {
	}

	/**
	 * Save measure to file
	 * TODO: use stream instead of fileName
	 * @return null on success, error message on failure
	 */
	public static String save(Measure measure, String fileName)
	{
		//Open/create file
		FileOutputStream measureFOS;
		try
		{
			measureFOS=new FileOutputStream(fileName);
		}
		catch(FileNotFoundException e)
		{
				JOptionPane.showMessageDialog(null, Messages.getString("Divider.Can__t_save_to_file__43")+fileName,  //$NON-NLS-1$
				Messages.getString("Divider.Save_failed_45"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				return Messages.getString("Divider.Save_failed_45"); //$NON-NLS-1$
		}
		
		PrintWriter out = new PrintWriter( measureFOS, true);		
		
		//write data
		List lines;
		MeasureLine line;
		List elements;
		Element el;
		lines = measure.getMeasureLines();
		int i,j,li, lj;
		li=lines.size();
		for(i=0; i<li; i++)
		{
			line=(MeasureLine)lines.get(i);
			out.println(line.name); //save name
			out.println(line.begin.getX()+" "+line.begin.getY()+" "+line.end.getX()+" "+line.end.getY());//save line coords //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			out.println(line.captureWidth);//save capture width
			elements = line.getElements();
			lj=elements.size();
			for(j=0; j<lj; j++)
			{
				el=(Element)elements.get(j);
				out.print((el.getIndex()+1)+" ");//save element //$NON-NLS-1$
			}
			out.println(""); //$NON-NLS-1$
		}//end for all lines
		
		return null;
	}//end save(..)
	
	
	/**
	 * Load measure from file
	 * TODO: use stream instead of fileName
	 * @return null on success, error message on failure
	 */
	public static String load(Measure measure, String fileName)
	{
		String errorMessage=null;
		FileInputStream measureFIS;
		try
		{
			measureFIS=new FileInputStream(fileName);
		}
		catch(FileNotFoundException e)
		{
				JOptionPane.showMessageDialog(null, Messages.getString("MeasureStreamer.Can__t_open_measure._File_not_found___7")+fileName,  //$NON-NLS-1$
				Messages.getString("MeasureStreamer.Open_failed_8"), JOptionPane.ERROR_MESSAGE);  //$NON-NLS-1$
				return Messages.getString("MeasureStreamer.Open_failed_8"); //$NON-NLS-1$
		}
		BufferedReader in = new BufferedReader
			(new InputStreamReader(measureFIS) );

		//remove all old lines
		measure.removeAllLines();

		//read file and insert new lines
		String nameLine, coordsLine, widthLine, elementsLine;
		try{
			nameLine=in.readLine();
			while(nameLine!=null)
			{
				coordsLine=in.readLine();
				widthLine=in.readLine();
				elementsLine=in.readLine();
				
				StringTokenizer t = new StringTokenizer(coordsLine, " \t"); //$NON-NLS-1$
				double x1=Double.parseDouble(t.nextToken());
				double y1=Double.parseDouble(t.nextToken());
				double x2=Double.parseDouble(t.nextToken());
				double y2=Double.parseDouble(t.nextToken());
				double captureWidth=Double.parseDouble(widthLine);
				//create line
				MeasureLine line = new MeasureLine(measure, nameLine.trim(), 
								new Dot(x1, y1), new Dot(x2, y2) );
				line.captureWidth=captureWidth;
				//write line
				measure.addLine(line);
				
				nameLine=in.readLine();
			}
		}
		catch(IOException e)
		{
			errorMessage=Messages.getString("MeasureStreamer.Failed_to_read_data_from_file___11")+e.getMessage(); //$NON-NLS-1$
			System.err.println(errorMessage);
		}
		
		measure.getMeshPanel().redraw();
		
		return errorMessage;
	}//end load(...)
	
}
