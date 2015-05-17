/*
 * ElementStack.java
 *
 * Created on Thursday, 31, March 2005, 16:47
 */

package fem.divider.mesh;

import java.util.*;

import fem.geometry.Triangle;
/**
 *
 * @author  gefox
 * @version 
 */
public class ElementStack {

		/** Creates new ElementStack */
    public ElementStack() {
    }

		public void push(Triangle el)
		{
				list.add(el);
		}
		
		public Triangle pop()
		{
				return (Triangle)list.removeLast();
		}
		
		private LinkedList list = new LinkedList();
}
