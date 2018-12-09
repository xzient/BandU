package cmsc424.cmsc424_pg11;

import java.util.Date;

public class SearchData {


    private static SearchData singleInstance = null;

    public String genre;
    public Date time;
    public double[] location;




    public static SearchData getSingleInstance() {
        if (singleInstance == null)
            singleInstance = new SearchData();


        //DATA



        return singleInstance;
    }





}
