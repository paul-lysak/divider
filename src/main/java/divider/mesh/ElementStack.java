/*
 * ElementStack.java
 *
 * Created on Thursday, 31, March 2005, 16:47
 */

package divider.mesh;

import java.util.*;
/**
 *
 * @author  gefox
 * @version 
 */
public class ElementStack {

		/** Creates new ElementStack */
    public ElementStack() {
    }

		public void push(Element el)
		{
				list.add(el);
		}
		
		public Element pop()
		{
				return (Element)list.removeLast();
		}
		
		private LinkedList list = new LinkedList();
}
