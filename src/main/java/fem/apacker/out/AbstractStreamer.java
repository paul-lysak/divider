/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.apacker.out;

import fem.apacker.model.ModelFacade;
import java.io.PrintStream;

/**
 *
 * @author Paul Lysak
 */
abstract public class AbstractStreamer implements IStreamer {

    private PrintStream printStream;
    private ModelFacade modelFacade;
    
    public void setPrintStream(PrintStream stream) {
        this.printStream = stream;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public ModelFacade getModelFacace() {
        return modelFacade;
    }

    public void setModelFacade(ModelFacade modelFacade) {
        this.modelFacade = modelFacade;
    }
    
}
