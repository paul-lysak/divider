/*
 * Created on 10/7/2006
 */
package divider;
import java.util.regex.*;

/**
 * Get some information from command line
 * @author gefox
 */
public class CmdLineDetails {

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
				if(key[0].equals("locale")) //$NON-NLS-1$
				{locale=key[1];
				}
				else
				{System.out.println(Messages.getString("CmdLineDetails.Warning__unknown_key__2")+key[0]); //$NON-NLS-1$
				}
			}
			else //not a key, just some text
			{
				if( i==c-1) //found file namd
						figureFile=cmd;
			}
		}
	}

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
	}
	
	String locale=null;
	String figureFile=null;
	
//	private static Pattern keyPattern = Pattern.compile("--([(?Alnum)_]+)=([(?Alnum)_]*)");
	private static Pattern keyPattern = Pattern.compile("--([A-Za-z0-9_]+)=([A-Za-z0-9_]*)"); //$NON-NLS-1$
}
