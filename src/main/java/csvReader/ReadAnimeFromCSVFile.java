package csvReader;

import model.Anime;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadAnimeFromCSVFile {

    public List<Anime> read(String filename) throws FileNotFoundException {
       final List<Anime> animeList = new ArrayList<Anime>();
        final File file1 = new File("data/anime.csv");
        Scanner sc = new Scanner(file1);
        String data = sc.next();

        while (sc.hasNext()){
            String scData = sc.next();
            String dataList[] = scData.split(",");
            //skip the first row
            if(dataList[0].equals("anime_id")){
                continue;
            }
            Anime anime = new Anime();
            anime.anime_id = dataList[0];
            anime.name = dataList[1];
            anime.genre = dataList[2];
            anime.type = dataList[3];
            anime.episodes = dataList[4];
            anime.rating = dataList[5];
            anime.members = dataList[6];
            animeList.add(anime);
        }
        sc.close();
        return animeList;
    }
}

