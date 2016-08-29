package core;

public class WordChecker {

	public static boolean IS_EVERYTHING_OKAY(String INPUT, int TYPE) {
		if(TYPE == 1) {
			if(INPUT != null && !INPUT.equals("") && INPUT.length() <= 50)
				return true;
		} else {
			if(INPUT.length() <= 500)
				return true;
		}
		return false;
	}
	
}
