/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.apacker.parsers;

import fem.apacker.model.ModelFacade;

/**
 *
 * @author Admin
 */
public class TParser extends AbstractAnsysParser {

    private final static int MIN_POSITIONS = 2;
    
    private final static int TEMP_NODE_INDEX_POSITION = 0;
    private final static int TEMP_VALUE_POSITION = 1;
    
    public TParser(ModelFacade modelFacade, String fileName) {
        super(modelFacade, fileName);
    }
   
    @Override
    protected void parseLineParts(String[] parts) {
        if(parts.length < MIN_POSITIONS) //need at least index, value
            return;
        
        int index;
        double value;
        try{
           index = Integer.parseInt(parts[TEMP_NODE_INDEX_POSITION]);
           value = Double.parseDouble(parts[TEMP_VALUE_POSITION]);
        }
        catch(NumberFormatException e)
        {
            return; //can't handle line, skip it
        }
        handleNewTemperatureEntry(index, value);
    }//end method parseLineParts
    
    
    private void handleNewTemperatureEntry(int nodeIndex, double value) {
        getModelFacade().addNodalTemperature(nodeIndex, value);
    }
            
}//end class

