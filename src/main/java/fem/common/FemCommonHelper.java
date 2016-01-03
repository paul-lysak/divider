/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fem.common;

/**
 *
 * @author Paul
 */
public class FemCommonHelper {

    /**
     * Return -1 if arg<0, if arg==0, 1 if arg>0
     * 
     * @param arg
     * @return
     */
    public static int signum(int arg)
    {
        if(arg<0)
            return -1;
        else
        if(arg>0)
            return 1;
        else
            return 0;
    }
    
    /**
     * Return -1 if n1>n2. 0 if n1==n2, 1 if n1<n2
     * 
     * @param n1
     * @param n2
     * @return
     */
    public static int compare(int n1, int n2)
    {
        return signum(n1 - n2);
    }
}//end class FemCommonHelper
