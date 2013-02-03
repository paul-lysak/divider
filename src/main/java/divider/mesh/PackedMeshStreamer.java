package divider.mesh;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import divider.Messages;

public class PackedMeshStreamer extends MultifileMeshStreamer {

	private static final String SECTION_TAG_SETTINGS = "[settings]";

	private static final String SECTION_TAG_INDEXES = "[inds]";
	
	private static final String SECTION_TAG_COORDINATES = "[koor]";

	private static final String SECTION_TAG_CONTACTS = "[contact]";

	private static final String SECTION_TAG_FORCES = "[force]";
	
	
	private static final int DEFAULT_BUFFER_SIZE = 256;
	
	private static final String PROPERTY_NNODES = "n_nodes="; 
	private static final String PROPERTY_NELEMENTS = "n_elements="; 
	private static final String PROPERTY_NCONTACTS = "n_contacts="; 
	private static final String PROPERTY_NFORCES = "n_forces="; 
	
	
	public void save(Mesh mesh, String fileBasename) throws Exception {
		FileOutputStream fileStream;
		try
		{
			fileStream = new FileOutputStream(fileBasename); //$NON-NLS-1$
		}
		catch(FileNotFoundException e)
		{
			String msg = Messages.getString("Divider.Can__t_save_to_file__43")+fileBasename; //$NON-NLS-1$ 
			throw new Exception(msg, e);
		}
		
		PrintWriter out = new PrintWriter(fileStream, true);
		
		InfluencesContext iContext = prepareInfluences(mesh);
		writeSettingsSection(mesh, iContext, out);
		writeIndexesSection(mesh, out);
		writeCoordinatesSection(mesh, out);
		writeInfluencesSections(iContext, out);
	}

	private void writeSettingsSection(Mesh mesh, InfluencesContext iContext, PrintWriter out) throws Exception
	{
		out.println(SECTION_TAG_SETTINGS);
		out.println(PROPERTY_NNODES + mesh.getNodes().size());
		out.println(PROPERTY_NELEMENTS + mesh.getElements().size());
		out.println(PROPERTY_NFORCES + iContext.forcesCount);
		out.println(PROPERTY_NCONTACTS + iContext.contactsCount);
	}

	private void writeIndexesSection(Mesh mesh, PrintWriter out) throws Exception
	{
		out.println(SECTION_TAG_INDEXES);
		saveIndexes(mesh, out);
	}

	private void writeCoordinatesSection(Mesh mesh, PrintWriter out) throws Exception
	{
		out.println(SECTION_TAG_COORDINATES);
		saveCoords(mesh, out);
	}

	private InfluencesContext prepareInfluences(Mesh mesh) throws Exception
	{
		StringWriter contactsSWriter = new StringWriter(DEFAULT_BUFFER_SIZE);
		StringWriter forcesSWriter = new StringWriter(DEFAULT_BUFFER_SIZE);
		
		PrintWriter contactsPWriter = new PrintWriter(contactsSWriter);
		PrintWriter forcesPWriter = new PrintWriter(forcesSWriter);
		
		int[] counts = saveInfluence(mesh, contactsPWriter, forcesPWriter);
		
		InfluencesContext iContext = new InfluencesContext();
		iContext.contactsText = contactsSWriter.toString();
		iContext.forcesText = forcesSWriter.toString();
		if(counts.length>0)		
			iContext.contactsCount = counts[0];
		if(counts.length>1)		
			iContext.forcesCount = counts[1];
		
		return iContext;
	}
	
	private void writeInfluencesSections(InfluencesContext iContext, PrintWriter out) throws Exception
	{
		
		out.println(SECTION_TAG_CONTACTS);
		out.println(iContext.contactsText);
		out.println(SECTION_TAG_FORCES);
		out.println(iContext.forcesText);
	}

	
	private static class InfluencesContext {
		public String contactsText;
		public String forcesText;
		public int contactsCount;
		public int forcesCount;
	}
}
