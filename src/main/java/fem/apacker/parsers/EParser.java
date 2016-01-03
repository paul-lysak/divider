package fem.apacker.parsers;

import fem.apacker.model.ModelFacade;

/**
 * Parser for ELIST.lis (elements list)
 * 
 * @author Paul Lysak
 */
public class EParser extends AbstractAnsysParser {
    private final static int MIN_POSITIONS = 9;
    
    private final static int ELEMENT_INDEX_POSITION = 0;
    private final static int ELEMENT_NODEINDEX1_POSITION = 6;
    private final static int ELEMENT_NODEINDEX2_POSITION = 7;
    private final static int ELEMENT_NODEINDEX3_POSITION = 8;

    
    public EParser(ModelFacade modelFacade, String fileName) {
        super(modelFacade, fileName);
    }

    @Override
    protected void parseLineParts(String[] parts) {
        if(parts.length < MIN_POSITIONS) //need at least index, x, y
            return;
        
        int index;
        int nodeIndex1;
        int nodeIndex2;
        int nodeIndex3;
        
        try{
           index = Integer.parseInt(parts[ELEMENT_INDEX_POSITION]);
           nodeIndex1 = Integer.parseInt(parts[ELEMENT_NODEINDEX1_POSITION]);
           nodeIndex2 = Integer.parseInt(parts[ELEMENT_NODEINDEX2_POSITION]);
           nodeIndex3 = Integer.parseInt(parts[ELEMENT_NODEINDEX3_POSITION]);
        }
        catch(NumberFormatException e)
        {
            return; //can't handle line, skip it
        }
        handleNewNode(index, nodeIndex1, nodeIndex2, nodeIndex3);
        
    }//end method parseLineParts

    private void handleNewNode(int index, int nodeIndex1, int nodeIndex2, int nodeIndex3) {
        getModelFacade().addElement(index-1, nodeIndex1-1, nodeIndex2-1, nodeIndex3-1);
    }
    
}//end class EParser
