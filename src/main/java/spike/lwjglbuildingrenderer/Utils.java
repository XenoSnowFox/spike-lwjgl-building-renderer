package spike.lwjglbuildingrenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

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


	public static String loadResource(String fileName) throws Exception {
		String result;
		try (InputStream in = Utils.class.getResourceAsStream(fileName);
				Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}

	public static List<String> readAllLines(String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}

	public static List<String> readAllFileLines(String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		File file = new File(fileName);
		try (
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr)
		) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}

	/**
	 * Hidden constructor.
	 */
	private Utils() { }
}
