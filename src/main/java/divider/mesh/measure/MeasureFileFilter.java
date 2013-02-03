package divider.mesh.measure;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import divider.Messages;

public class MeasureFileFilter extends FileFilter
{
	public static final String FILE_EXT_MEASURE = "measure";
	
	public boolean accept(File f)
	{
		if( f.isDirectory() ) return true;
		String name = f.getName();
		int dot_i=name.lastIndexOf(FILE_EXT_MEASURE); //$NON-NLS-1$
		if( dot_i<0 ) return false;
		if( name.substring(dot_i+1).equalsIgnoreCase("") ) return true; //$NON-NLS-1$
			else return false;
	}//end accept(...)
	
	public String getDescription() {return Messages.getString("MeasureController.Measure_file_(*.measure)_11");} //$NON-NLS-1$
	
	public String getDefaultExtention() {return "measure";}; //$NON-NLS-1$
};
