package fem.apacker.parsers;

import fem.apacker.model.ModelFacade;

/**
 * Parser for NLIST file (nodes list)
 * 
 * @author Paul Lysak
 */
public class NParser extends AbstractAnsysParser {

    private final static int MIN_POSITIONS = 3;
    
    private final static int NODE_INDEX_POSITION = 0;
    private final static int NODE_X_COORD_POSITION = 1;
    private final static int NODE_Y_COORD_POSITION = 2;

    
    public NParser(ModelFacade modelFacade, String fileName) {
        super(modelFacade, fileName);
    }

    @Override
    protected void parseLineParts(String[] parts) {
        if(parts.length < MIN_POSITIONS) //need at least index, x, y
            return;
        
        int index;
        double x,y;
        try{
           index = Integer.parseInt(parts[NODE_INDEX_POSITION]);
           x = Double.parseDouble(parts[NODE_X_COORD_POSITION]);
           y = Double.parseDouble(parts[NODE_Y_COORD_POSITION]);
        }
        catch(NumberFormatException e)
        {
            return; //can't handle line, skip it
        }
        handleNewNode(index, x, y);
    }//end method parseLineParts

    private void handleNewNode(int index, double x, double y) {
//        System.out.println(MessageFormat.format("Node: {0}]=({1};{2})", new Object[]{index, x, y}));
        getModelFacade().addNode(index-1, x, y);
    }
    
    
}//end class NParser
