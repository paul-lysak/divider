/*
 * FigureStreamer.java
 *
 * Created on Sunday, 27, March 2005, 8:41
 */

package fem.divider.figure;
import fem.divider.*;

/**
 *This class is a wrapper for figure saving and loading capabilities.
 *It may use classes, subclassed from AbstractFormat, such as DfigFormat to work with file formats
 * @author  gefox
 * @version 
 */
public class FigureStreamer {


		public static String save(Figure fig, java.io.OutputStream out)
		{
				return defaultFormat.save(fig, out);
		}
		
		public static Figure open(World world_, java.io.InputStream in)
		{
				return defaultFormat.open(world_, in);
		}
		
		static void setDefaultFormat(AbstractFormat format)
		{
				defaultFormat = format;
		}
		
		static AbstractFormat defaultFormat;
		
		static{
				setDefaultFormat(new DfigFormat());
		}
}
