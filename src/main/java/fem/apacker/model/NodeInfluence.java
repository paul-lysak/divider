/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.apacker.model;

/**
 *
 * @author Paul Lysak
 */
public class NodeInfluence extends AbstractInfluence {

    private boolean fixedX;
    private boolean fixedY;

    public boolean isFixedX() {
        return fixedX;
    }

    public void setFixedX(boolean fixedX) {
        this.fixedX = fixedX;
    }

    public boolean isFixedY() {
        return fixedY;
    }

    public void setFixedY(boolean fixedY) {
        this.fixedY = fixedY;
    }
    
    
}
