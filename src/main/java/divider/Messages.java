/*
 * Created on 10/7/2006
 */
package divider;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
/**
 * @author gefox
 */
public class Messages {

	private static Locale locale; 
	
	private static String BUNDLE_NAME = "divider.resources.text.messages"; //$NON-NLS-1$

	private static  ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);

	public static void setLocale(Locale locale_)
	{
		locale=locale_;
		RESOURCE_BUNDLE =
				ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}
	
	/**
	 * 
	 */
	private Messages() {

		// TODO Auto-generated constructor stub
	}
	/**
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		// TODO Auto-generated method stub
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}//end Messages class


/*use
 * native2ascii -encoding cp1251 messages_uk.properties.cp1251 messages_uk.properties
 * to rebuild messages after editing messages_uk.properties.cp1251
*/