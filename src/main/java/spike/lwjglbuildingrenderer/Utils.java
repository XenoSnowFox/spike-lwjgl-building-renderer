package spike.lwjglbuildingrenderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

	/**
	 * Loads the properties from a specified resource file.
	 *
	 * @param resourceName
	 * 		Name of the properties resource file to load
	 * @return project properties.
	 */
	public static Properties loadProperties(final String resourceName) {
		try (InputStream input = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
			assert input != null : "Properties file `" + resourceName + "` does not exist.";
			Properties properties = new Properties();
			properties.load(input);
			return properties;
		} catch (IOException ex) {
			throw new RuntimeException("Unable to load properties from `" + resourceName + "`", ex);
		}
	}

	/**
	 * Hidden constructor.
	 */
	private Utils() { }
}
