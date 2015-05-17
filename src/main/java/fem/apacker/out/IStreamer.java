package fem.apacker.out;

import fem.apacker.model.ModelFacade;
import java.io.PrintStream;

public interface IStreamer {

    public void setPrintStream(PrintStream stream);
    
    public PrintStream getPrintStream();
        
    public void write();
    
    public ModelFacade getModelFacace();
    
    public void setModelFacade(ModelFacade modelFacade);
    
}
