/*
 * Created on 10/7/2006
 */
package fem.apacker;
import java.util.regex.*;

/**
 * Get some information from command line
 * @author Paul Lysak
 */
public class CmdLineDetails {

	public static final String PARAM_KEY_DLIST_FILE = "dlist"; //In file
	public static final String PARAM_KEY_ELIST_FILE = "elist"; //In file
	public static final String PARAM_KEY_NLIST_FILE = "nlist"; //In file
	public static final String PARAM_KEY_SFLIST_FILE = "sflist"; //In file
	
	public static final String PARAM_KEY_PMD_FILE = "pmd"; //Out file - packed mesh data

	public static final String PARAM_KEY_TEMPIN_FILE = "tempin"; //temperature in file
	public static final String PARAM_KEY_TEMPOUT_FILE = "tempout"; //temperature out file
	public static final String PARAM_KEY_TEMPNODESOUT_FILE = "tempnodesout"; //temperature with nodes out file

	public static final String PARAM_FULLKEY_HELP_MODE = "--help"; //help mode
	
        private boolean mandaroryParametersFilled;
        
        
	private String dListFile = null;
	private String eListFile = null;
	private String nListFile = null;
	private String sfListFile = null;
	
	private String pmdFile = null;
	
	private String tempInFile = null;
	private String tempOutFile = null;
	private String tempNodesOutFile = null;
	
	private boolean helpMode = false;
	
    
//	private static Pattern keyPattern = Pattern.compile("--([(?Alnum)_]+)=([(?Alnum)_]*)");
	private static Pattern keyPattern = Pattern.compile("--([A-Za-z0-9_]+)=(.*)"); //$NON-NLS-1$
        
    
	
	private static final String HELP_TEXT = "Command line arguments:\n--elist=elements_list.lis --nlist=nodes_list.lis  \n"+
		"--dlist=constraints.lis --sflist=surface_loads.lis\n"+
		"--pmd=output_file.pmd \n"+
		"--tempin=temperature_input.lis --tempout=temperature_output_without_nodes.txt \n"+
		"--tempnodesout=temperature_output_with_nodes.txt";
	
	
	public CmdLineDetails(String[] cmdLine) {
		int i,c;
		c=cmdLine.length;
		String cmd;
		String[] key; 
		for(i=0; i<c; i++)
		{
			cmd=cmdLine[i];
			key=parseKey(cmd);
			if( key!=null ) //key found
			{
                            //geometry & influences
                            if(PARAM_KEY_DLIST_FILE.equals(key[0]))
                            {setDListFile(key[1]);
                            }
                            else
                            if(PARAM_KEY_ELIST_FILE.equals(key[0]))
                            {setEListFile(key[1]);
                            }
                            else
                            if(PARAM_KEY_NLIST_FILE.equals(key[0]))
                            {setNListFile(key[1]);
                            }
                            else
                            if(PARAM_KEY_SFLIST_FILE.equals(key[0]))
                            {setSfListFile(key[1]);
                            }
                            else
                            if(PARAM_KEY_PMD_FILE.equals(key[0]))
                            {setPmdFile(key[1]);
                            }
                            //temperature
                            else
                            if(PARAM_KEY_TEMPIN_FILE.equals(key[0]))
                            {setTempInFile(key[1]);
                            }
                            else
                            if(PARAM_KEY_TEMPOUT_FILE.equals(key[0]))
                            {setTempOutFile(key[1]);
                            }
                            else
                            if(PARAM_KEY_TEMPNODESOUT_FILE.equals(key[0]))
                            {setTempNodesOutFile(key[1]);
                            }
                            else
                            {System.out.println("WARNING: unknown command line key:"+cmd);
                            }
			}
			else
			if(PARAM_FULLKEY_HELP_MODE.equals(cmd)) {
				setHelpMode(true);
			}
			else  { //not a key, just some text
				System.out.println("WARNING: parameter not recognized as key: "+cmd);
			}
		}//end for cmdLine		
	}//end constructor CmdLineDetails(String[] cmdLine)

	
        /**
         * 
         * @return
         */
	public void ensureNecessaryParametersExist() throws MissingParametersException
	{
            //geometry & influences
//            if(getDListFile()==null)
//                    System.out.println("WARNING: parameter "+PARAM_KEY_DLIST_FILE+" not specified");		
//            if(getEListFile()==null)
//                    System.out.println("WARNING: parameter "+PARAM_KEY_ELIST_FILE+" not specified");		
//            if(getNListFile()==null)
//                    System.out.println("WARNING: parameter "+PARAM_KEY_NLIST_FILE+" not specified");		
//            if(getSfListFile()==null)
//                    System.out.println("WARNING: parameter "+PARAM_KEY_SFLIST_FILE+" not specified");		
//            if(getPmdFile()==null)
//                    System.out.println("WARNING: parameter "+PARAM_KEY_PMD_FILE+" not specified");		
//
//            //temperature
//            if(getTempInFile()==null)
//                    System.out.println("WARNING: parameter "+PARAM_KEY_TEMPIN_FILE+" not specified");		

            if( (getEListFile() != null || getNListFile() != null ||
                 getDListFile() != null || getSfListFile() != null)
                 &&
                 getPmdFile() == null) {
                throw new MissingParametersException(String.format(
                        "If %1$s, %2$s, %3$s or %4$s parameters specified then parameter %5$s should be specified too", 
                        new Object[] {PARAM_KEY_ELIST_FILE, PARAM_KEY_NLIST_FILE,
                                      PARAM_KEY_DLIST_FILE, PARAM_KEY_SFLIST_FILE,
                                      PARAM_KEY_PMD_FILE
                                      }));
            }//end PMD conditions
                    
            if( (getDListFile() != null || getSfListFile() != null) &&
                    (getEListFile() == null || getNListFile() == null) ) {
                throw new MissingParametersException(String.format(
                        "If %1$s or %2$s parameter specified then both %3$s and %4$s parameters should be specified too", 
                        new Object[] {PARAM_KEY_DLIST_FILE, PARAM_KEY_SFLIST_FILE,
                                      PARAM_KEY_ELIST_FILE, PARAM_KEY_NLIST_FILE,
                                      }));
            }//end mesh data conditions

            if( (getEListFile() == null) != (getNListFile() == null) || 
                    (getNListFile() == null) != (getPmdFile() == null)) {
                throw new MissingParametersException(String.format(
                        "Parameters %1$s, %2$s and %3$s should either be all skipped or all specified", 
                        new Object[] {PARAM_KEY_ELIST_FILE, PARAM_KEY_NLIST_FILE, PARAM_KEY_PMD_FILE
                                      }));   
            }//end mesh integrity
                
            
            if( getTempInFile() != null && getTempNodesOutFile() == null && getTempOutFile() == null) {
                throw new MissingParametersException(String.format(
                        "If parameter %1$s specified then either %2$s or %3$s should be specified", 
                        new Object[] {PARAM_KEY_TEMPIN_FILE, PARAM_KEY_TEMPNODESOUT_FILE, PARAM_KEY_TEMPOUT_FILE
                                      }));                   
            }//end temperature input dependencies
            
            if( (getTempNodesOutFile() != null || getTempOutFile() != null) &&
                    getTempInFile() == null ) {
                throw new MissingParametersException(String.format(
                        "If parameter %1$s or %2$s specified then parameter %3$s should also be specified", 
                        new Object[] {PARAM_KEY_TEMPNODESOUT_FILE, PARAM_KEY_TEMPOUT_FILE,
                                        PARAM_KEY_TEMPIN_FILE
                                      }));                   
            }//end temperature output dependencies
            
            
            if( getTempInFile()== null && getNListFile() == null) {
            	throw new MissingParametersException("Either figure-related or temperature-related parameters should be specified");
            }
	}//end method ensureNecessaryParametersExist
        
        
	/**
	 * Parse str, taken from command line. 
	 * If it's like  --key=value, then return array{key, value}
	 * Otherwise return null
	 * @return {key, value} on success, null on failure
	 */
	public String[] parseKey(String str)
	{
		String[] result = new String[2];
		Matcher matcher = keyPattern.matcher(str);
		if( matcher.matches() )
			{
				result[0]=matcher.group(1);
				result[1]=matcher.group(2);
				return result;
			}
			else
			{
				return null;
			}
	}//end method parseKey

	
	/**
	 * Get name of node constraints file - input file 
	 * @return
	 */
	public String getDListFile() {
		return dListFile;
	}

	/**
	 * Get name of element matrix file - input file
	 * @return
	 */
	public String getEListFile() {
		return eListFile;
	}

	/**
	 * Get name of nodes coordinates file - input file
	 * @return
	 */
	public String getNListFile() {
		return nListFile;
	}

	/**
	 * Get name of surface loads file - input file
	 * @return
	 */
	public String getSfListFile() {
		return sfListFile;
	}
	
	/**
	 * Get name of packed mesh data file - output file.
	 * @return
	 */
	public String getPmdFile() {
		return pmdFile;
	}
	

	public void setDListFile(String listFile) {
		dListFile = listFile;
	}

	public void setEListFile(String listFile) {
		eListFile = listFile;
	}

	public void setNListFile(String listFile) {
		nListFile = listFile;
	}

	public void setSfListFile(String sfListFile) {
		this.sfListFile = sfListFile;
	}

	public void setPmdFile(String pmdFile) {
		this.pmdFile = pmdFile;
	}	
	


    public boolean isMandaroryParametersFilled() {
        return mandaroryParametersFilled;
    }

    public void setMandaroryParametersFilled(boolean mandaroryParametersFilled) {
        this.mandaroryParametersFilled = mandaroryParametersFilled;
    }

    public String getTempInFile() {
        return tempInFile;
    }

    public void setTempInFile(String tempInFile) {
        this.tempInFile = tempInFile;
    }

    public String getTempOutFile() {
        return tempOutFile;
    }

    public void setTempOutFile(String tempOutFile) {
        this.tempOutFile = tempOutFile;
    }

    public String getTempNodesOutFile() {
        return tempNodesOutFile;
    }

    public void setTempNodesOutFile(String tempNodesOutFile) {
        this.tempNodesOutFile = tempNodesOutFile;
    }


	public boolean isHelpMode() {
		return helpMode;
	}


	public void setHelpMode(boolean helpMode) {
		this.helpMode = helpMode;
	}


	public String getHelpText() {
		return HELP_TEXT; 
	}


	

}
