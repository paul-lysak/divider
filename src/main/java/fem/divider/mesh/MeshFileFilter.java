package fem.divider.mesh;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MeshFileFilter extends FileFilter
{
	private String fileExtension; 
	
	private IMeshStreamer meshStreamer;

	private String description;
	
	


	public MeshFileFilter(String fileExtension,  String description, IMeshStreamer meshStreamer) {
		setFileExtension(fileExtension);
		setMeshStreamer(meshStreamer);
		setDescription(description);
	}
	
	public boolean accept(File f)
	{
		if( f.isDirectory() ) return true;
		if(fileExtension == null || fileExtension == "") return true;
		
		String name = f.getName();
		int dot_i=name.lastIndexOf("."); //$NON-NLS-1$
		if( dot_i>0 && name.substring(dot_i+1).equalsIgnoreCase(fileExtension) ) return true; //$NON-NLS-1$
			else return false;
	}//end accept(...)
	
	public String getDescription() {return description;}
	
	public final void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultExtention() {return fileExtension;}

	public final String getFileExtension() {
		return fileExtension;
	}

	/**
	 * Sets file extension. 
	 * Extension is case-insensitive. null means any.
	 * @param fileExtension
	 */
	public final void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public final IMeshStreamer getMeshStreamer() {
		return meshStreamer;
	}

	public final void setMeshStreamer(IMeshStreamer meshStreamer) {
		this.meshStreamer = meshStreamer;
	}; 
	
	
	/**
	 * If fileName has an extension, returns fileName
	 * Otherwise adds fileExtension to fileName abd returns it.
	 * @param fileName
	 * @return
	 */
	public String ensureExtension(String fileName)
	{
		int dotI=fileName.lastIndexOf("."); //$NON-NLS-1$
		if(dotI<0 && fileExtension!=null && fileExtension!="") //$NON-NLS-1$
			return fileName+"."+fileExtension; //$NON-NLS-1$
		else
			return fileName;
		
	}
};