package main.java.csvReader;

import main.java.Ratings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadRatingsFromCSVFile {

    public List<Ratings> read(String filename) throws FileNotFoundException {
       final List<Ratings> ratings = new ArrayList<Ratings>();
        final File file1 = new File("data.csv");
        Scanner sc = new Scanner(file1);
        String data = sc.next();

        while (sc.hasNext()){
            String scData = sc.next();
            String dataList[] = scData.split(",");
            String userid = dataList[0];
            String item = dataList[1];
            String rating = dataList[2];
            Ratings ratingObj = new Ratings();
            ratingObj.userid = userid;
            ratingObj.item = item;
            ratingObj.rating = rating;
            ratings.add(ratingObj);
        }
        sc.close();
        return ratings;
    }
}

