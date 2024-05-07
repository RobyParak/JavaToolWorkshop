import csvReader.ReadAnimeFromCSVFile;
import model.Anime;

import java.io.FileNotFoundException;
import java.util.List;

public class MainRecommendation {
        public static void main(String[] args) throws FileNotFoundException {
            ReadAnimeFromCSVFile reader = new ReadAnimeFromCSVFile();
            try {
                List<Anime> animeList = reader.read("data/anime.csv");

                // Log details of the first 5 anime
                System.out.println("Details of the first 5 anime:");
                for (int i = 0; i < Math.min(5, animeList.size()); i++) {
                    Anime anime = animeList.get(i);
                    System.out.println("Anime " + anime);
                    System.out.println();
                }
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            }
        }
    }


