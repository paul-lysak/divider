/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.apacker.model;

/**
 *
 * @author Paul Lysak
 */
public class SegmentInfluence extends AbstractInfluence {

    private double load1;
    private double load2;

    private boolean loaded1;
    private boolean loaded2;
    
    public double getLoad1() {
        return load1;
    }

    public void setLoad1(double load1) {
        this.load1 = load1;
    }

    public double getLoad2() {
        return load2;
    }

    public void setLoad2(double load2) {
        this.load2 = load2;
    }

    public boolean isLoaded1() {
        return loaded1;
    }

    public void setLoaded1(boolean loaded1) {
        this.loaded1 = loaded1;
    }

    public boolean isLoaded2() {
        return loaded2;
    }

    public void setLoaded2(boolean loaded2) {
        this.loaded2 = loaded2;
    }

    @Override
    public String toString() {
        return "load1="+load1+", load2="+load2+", loaded1="+loaded1+", loaded2="+loaded2;
    }
    
   
    
}//end class SegmentInfluence
