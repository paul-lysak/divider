/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.common;

/**
 *
 * Constants for .PMD file format
 * 
 * @author Paul Lysak
 */
public interface IPmdSettings {

	public static final String SECTION_TAG_SETTINGS = "[settings]";

	public static final String SECTION_TAG_INDEXES = "[inds]";
	
	public static final String SECTION_TAG_COORDINATES = "[koor]";

	public static final String SECTION_TAG_CONTACTS = "[contact]";

	public static final String SECTION_TAG_FORCES = "[force]";
	
	
	
	public static final String PROPERTY_NNODES = "n_nodes="; 
	public static final String PROPERTY_NELEMENTS = "n_elements="; 
	public static final String PROPERTY_NCONTACTS = "n_contacts="; 
	public static final String PROPERTY_NFORCES = "n_forces=";     
}//end interface IPmdSettings
