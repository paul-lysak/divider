package fem.apacker.parsers;

import fem.apacker.model.ModelFacade;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Parses SFLIS.lst file (sufrace loads)
 * 
 * @author Paul Lysak
 */
public class SFParser extends AbstractAnsysParser {
    private final static int POSITIONS_ROW1 = 5;
    private final static int POSITIONS_ROW2 = 3;
    
    
    private final static int LOADS_ELEMENT_POSITION = 0;
    private final static int LOADS_NODE1_POSITION = 2;
    private final static int LOADS_VALUE1_POSITION = 3;
    
    private final static int LOADS_NODE2_POSITION = 0;
    private final static int LOADS_VALUE2_POSITION = 1;
    
    
    private int elementIndex;
    private int nodeIndex1;
    private double value1;

    private boolean row1Parsed = false;
            
    public SFParser(ModelFacade modelFacade, String fileName) {
        super(modelFacade, fileName);
    }

    @Override
    public void parse() throws FileNotFoundException, IOException {
        this.row1Parsed = false;
        super.parse();
    }
    
    
    
    @Override
    protected void parseLineParts(String[] parts) {
        if(parts.length == POSITIONS_ROW1)
            parseLine1Parts(parts);
        else
        if(parts.length == POSITIONS_ROW2 && row1Parsed)
            parseLine2Parts(parts);
    }
    
    /**
     * Parses first line of item  - contains element index, node1 index and node1 load
     * 
     * @param parts
     */
    private void parseLine1Parts(String[] parts) {
        int element;
        int node1;
        double valueN1;
        
        try{
           element = Integer.parseInt(parts[LOADS_ELEMENT_POSITION]);
           node1 = Integer.parseInt(parts[LOADS_NODE1_POSITION]);
           valueN1 = Double.parseDouble(parts[LOADS_VALUE1_POSITION]);
        }
        catch(NumberFormatException e)
        {
            return; //can't handle line, skip it
        }
        this.elementIndex =  element;
        this.nodeIndex1 = node1;
        this.value1 = valueN1;
        this.row1Parsed = true;
    }//end method parseLine1Parts

    /**
     * Parses second line of item and saves result in mesh.
     * This line contains node2 index and node2 load.
     * 
     * @param parts
     */
    private void parseLine2Parts(String[] parts) {
        int nodeIndex2;
        double value2;
        
        try{
           nodeIndex2 = Integer.parseInt(parts[LOADS_NODE2_POSITION]);
           value2 = Double.parseDouble(parts[LOADS_VALUE2_POSITION]);
        }
        catch(NumberFormatException e)
        {
            return; //can't handle line, skip it
        }
        
        handleNewLoad(this.elementIndex, this.nodeIndex1, nodeIndex2, this.value1, value2);
        
        this.row1Parsed = false;
    }//end method parseLine2Parts

    private void handleNewLoad(int elementIndex, int nodeIndex1, int nodeIndex2, double value1, double value2)
    {
        getModelFacade().applyLoad(elementIndex-1, nodeIndex1-1, nodeIndex2-1, value1, value2);
    }
}//end class SFParser
