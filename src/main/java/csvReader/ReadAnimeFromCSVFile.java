package csvReader;

import model.Anime;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadAnimeFromCSVFile {

    public List<Anime> read(String filename) throws FileNotFoundException {
        List<Anime> animeList = new ArrayList<>();
        File file = new File(filename);
        Scanner sc = new Scanner(file);

        // Skip the header line
        if (sc.hasNextLine()) {
            sc.nextLine();
        }

        // Read data lines
        while (sc.hasNextLine()) {
            String scData = sc.nextLine();
            String[] dataList = scData.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            // Validate data line
            if (dataList.length >= 7) {
                Anime anime = new Anime();
                anime.anime_id = dataList[0];
                anime.name = dataList[1];
                anime.genre = dataList[2];
                anime.type = dataList[3];
                anime.episodes = dataList[4];
                anime.rating = dataList[5];
                anime.members = dataList[6];

                // Remove double quotes from genre and end comma
                anime.genre = anime.genre.replaceAll("\"", "");
                if (anime.genre.endsWith(",")) {
                    anime.genre = anime.genre.substring(0, anime.genre.length() - 1);
                }

                animeList.add(anime);
            } else {
                System.err.println("Invalid data line: " + scData);
            }
        }
        sc.close();
        return animeList;
    }
}