import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static Boolean isIP(String input) {

        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";

        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(input);

        return m.matches();
    }
}
