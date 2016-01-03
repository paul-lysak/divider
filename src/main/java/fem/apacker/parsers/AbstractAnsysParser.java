package fem.apacker.parsers;

import fem.apacker.model.ModelFacade;
import fem.common.parser.AbstractTabbedParser;

abstract public class AbstractAnsysParser extends AbstractTabbedParser {
    private ModelFacade modelFacade;
    
    public AbstractAnsysParser(ModelFacade modelFacade, String fileName) {
        setModelFacade(modelFacade);
        setFileName(fileName);
    }
    
                
    public ModelFacade getModelFacade() {
        return modelFacade;
    }

    public void setModelFacade(ModelFacade modelFacade) {
        this.modelFacade = modelFacade;
    }
    
}//end class AbstractParser
