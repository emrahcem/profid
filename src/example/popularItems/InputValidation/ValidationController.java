package example.popularItems.InputValidation;

public class ValidationController {

	public static boolean isInteger(String text) {

		try {
			Integer.parseInt(text);
		} catch (Exception e) {

			return false;
		}

		return true;
	}

	public static boolean isPositiveDouble(String text) {
		double val = 0;
		try {
			val = Double.parseDouble(text);
		} catch (Exception e) {
			return false;
		}
		if (val > 0)
			return true;
		else
			return false;
	}
}
