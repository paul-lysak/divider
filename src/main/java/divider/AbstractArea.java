/*
 * AbstractArea.java
 *
 * Created on Tuesday, 22, March 2005, 14:41
 */

package divider;
import java.util.*;

/**
 *
 * @author  gefox
 * @version 
 */
abstract public class AbstractArea {

		public void addListener(AreaListener listener)
		{
				listeners.add(listener);
		}
		
		public void removeListener(AreaListener listener)
		{
				listeners.remove(listener);
		}
		
		protected void tellChanged()
		{
				for(Iterator i=listeners.iterator(); i.hasNext(); )
						((AreaListener)i.next()).onAreaChanged(this);
		}
		
		protected ArrayList listeners = new ArrayList(2);				
}
