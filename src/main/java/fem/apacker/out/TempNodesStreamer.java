/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.apacker.out;

/**
 *
 * @author Paul Lysak
 */
public class TempNodesStreamer extends TempStreamer {

    @Override
    protected boolean isIncludeNodeNumbers() {
        return true;
    }
    
}
