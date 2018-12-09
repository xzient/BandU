package cmsc424.cmsc424_pg11;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Methods {
    /**
     * This methods will reduce the number of genres
     * @param listA
     * @return
     */
    public static ArrayList reduceGenres(ArrayList<String> listA) {
        ArrayList<String> listB = new ArrayList<String>();

        for (int i = 0; i < listA.size(); i++) {
            String a = listA.get(i);
            int resultCheck = 2;

            for (int j = 0; j < listA.size(); j++) {
                if(i == j) {
                    continue;
                }
                String b = listA.get(j);
                resultCheck = checkStringValue(a,b);
                if (resultCheck == 0) {
                    break;
                }

            }
            if (resultCheck != 0) {
                listB.add(a);
            }
        }



        return listB;
    }

    /**
     * This method will return if there is a matching for the genre
     * @param listA
     * @param b
     * @return
     */
    public static boolean genreFoundInList(ArrayList<String> listA, String b) {
        for(String a: listA) {
            if (a.equals(b) || checkStringValue(a, b) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * This methos check if genre string value is higher or less.
     * @param a
     * @param b
     * @return
     */
    public static int checkStringValue(String a, String b) {
        String currA = a.substring(0, 2);
        String currB = b.substring(0, 2);

        if(currA.equals(currB)) { //Root level
            currA = a.substring(2, 4);
            currB = b.substring(2, 4);

            if(currA.equals("00")) {
                return 1;
            }
            else if (currB.equals("00")) {
                return 0;
            }
            else if (currA.equals(currB)) { //First layer
                currA = a.substring(4, 6);
                currB = b.substring(4, 6);

                if(currA.equals("00")) {
                    return 1;
                }
                else if (currB.equals("00")) {
                    return 0;
                }
                else if (currA.equals(currB)) { //Second layer
                    currA = a.substring(6, 8);
                    currB = b.substring(6, 8);

                    if(currA.equals("00")) {
                        return 1;
                    }
                    else if (currB.equals("00")) {
                        return 0;
                    }
                }
            }
        }

        return 2;
    }


    //DATE METHODS
    public static boolean betweenTwoDates(String a, String b, Date dateC) {
        Date dateA = stringToDate(a);
        Date dateB = stringToDate(b);
        //Date dateC = stringToDate(c);

        return dateA.compareTo(dateC) * dateC.compareTo(dateB) >= 0;

    }

    public static Date stringToDate(String a) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt("20" + a.substring(0,2)));
        cal.set(Calendar.MONTH, Integer.parseInt(a.substring(2,4)) - 1);
        cal.set(Calendar.DATE, Integer.parseInt(a.substring(4,6)));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(a.substring(6, 8)));
        cal.set(Calendar.MINUTE, Integer.parseInt(a.substring(8, 10)));

        Date d = cal.getTime();

        return d;
    }
}
