/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.common.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 *
 * @author Admin
 */
public class AbstractTabbedParser {

    private String fileName;
    
    private static final String SPACES_REGEX = "\\s+";
    private Pattern spacesRegex = Pattern.compile(SPACES_REGEX);
    

    /**
     * Initiates parsing: creates reader for file wihich name is fileName
     * and runs parse(BufferedReader reader) for this reader.
     * 
     */
    public void parse() throws FileNotFoundException, IOException
    {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fileReader);
        
        parse(reader);
        
        reader.close();
    }//end method parse
    
    /**
     * Do parsing for specified reader
     * 
     * @param reader
     * @throws java.io.IOException
     */
    public void parse(BufferedReader reader) throws IOException {
        String line = "";
        while( (line = reader.readLine()) != null)
        {
            parseLine(line);
        }        
    }
   

    /**
     * Called by parse() for each line of file to parse it.
     * (Of course, if subclass didn't change behaviour of parse()).
     */
    protected void parseLine(String line) {
        String lineParts[] = spacesRegex.split(line.trim());
        if(lineParts.length > 0)
            parseLineParts(lineParts);
//        System.out.println("length="+lineParts.length+", Line = "+lineParts[0]);
    }
    

    /**
     * Parse parts of line. Line is divided on parts by spaces after trimming.
     * 
     * 
     * @param parts
     */
    protected void parseLineParts(String []parts)
    {
        //Can be implemented in subclass
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}//end class AbstractTabbedParser
