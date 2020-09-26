package in.curies.alpha.utils;

public class Utilities {

    public static String getDbIdFromEmail(String email) {
        return email.replace("@", "")
                .replace(".", "")
                .replace("_", "");
    }

    public static String getChatId(String number1, String number2) {
        String e1 = number1.substring(number1.length() - 10);
        String e2 = number2.substring(number2.length() - 10);

        return (e1.compareTo(e2) > 0) ? e1 + e2 : e2 + e1;
    }

    public static Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 -i 5 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
