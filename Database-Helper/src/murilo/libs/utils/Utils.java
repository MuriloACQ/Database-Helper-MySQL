package murilo.libs.utils;

public class Utils {

	public static String firstLetterToUpperCase(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static String snakeToCamelCase(String snakecase) {
		String[] parts = snakecase.split("_");
		String camelcase = parts[0].toLowerCase();
		if (parts.length > 1) {
			for (int i = 1; i < parts.length; i++) {
				camelcase += firstLetterToUpperCase(parts[i].toLowerCase());
			}
		}
		return camelcase;
	}

	public static String camelToSnakeCase(String camelcase) {
		return camelcase.replaceAll("([A-Z][a-z])", "_$1");
	}
}
