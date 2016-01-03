package fem.apacker.parsers;

import fem.apacker.model.ModelFacade;

/**
 * Parser for DLIST.lis (constraints list)
 * @author Paul
 */
public class DParser extends AbstractAnsysParser{
    private final static int MIN_POSITIONS = 2;
    
    private final static int CONSTRAINT_NODE_INDEX_POSITION = 0;
    private final static int CONSTRAINT_CODE_POSITION = 1;

    private final static String CONSTRAINT_CODE_FIX_X = "UX";
    private final static String CONSTRAINT_CODE_FIX_Y = "UY";
    
    public DParser(ModelFacade modelFacade, String fileName) {
        super(modelFacade, fileName);
    }
    
    
    @Override
    protected void parseLineParts(String[] parts) {
        if(parts.length < MIN_POSITIONS) //need at least index, x, y
            return;
        
        int index;
        String constraintCode;
        try{
           index = Integer.parseInt(parts[CONSTRAINT_NODE_INDEX_POSITION]);
           constraintCode = parts[CONSTRAINT_CODE_POSITION];
        }
        catch(NumberFormatException e)
        {
            return; //can't handle line, skip it
        }
        boolean fixX = false, fixY = false;
        if(CONSTRAINT_CODE_FIX_X.equals(constraintCode))
            fixX = true;
        if(CONSTRAINT_CODE_FIX_Y.equals(constraintCode))
            fixY = true;
        handleNewConstraint(index, fixX, fixY);
    }//end method parseLineParts
    
    private void handleNewConstraint(int index, boolean fixX, boolean fixY)
    {
        getModelFacade().applyConstraint(index-1, fixX, fixY);
    }
}//end class DParser
