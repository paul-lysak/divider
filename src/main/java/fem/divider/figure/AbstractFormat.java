/*
 * AbstractFormat.java
 *
 * Created on Sunday, 27, March 2005, 8:51
 */

package fem.divider.figure;

import java.io.*;

import fem.divider.*;
/**
 *
 * @author  gefox
 * @version 
 */
abstract public class AbstractFormat {

	
abstract String save(Figure fig, OutputStream out);
abstract Figure open(World world_, InputStream in);
}
