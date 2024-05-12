import csvReader.ReadGameFromCSVFile;
import model.Game;

import java.io.FileNotFoundException;
import java.util.List;

public class Regression {
    public static void main(String[] args) {
        // Create an instance of ReadGameFromCSVFile
        ReadGameFromCSVFile gameReader = new ReadGameFromCSVFile();

        // Specify the filename of the CSV file containing game data
        String filename = "java/data/games_2024.csv";

        try {
            // Read game data from the CSV file
            List<Game> games = gameReader.read(filename);

            // Process the game data as needed
            for (Game game : games) {
                // For example, print each game to the console
                System.out.println(game.toString());
            }
        } catch (FileNotFoundException e) {
            // Handle the case where the file is not found
            System.err.println("File not found: " + filename);
            e.printStackTrace();
        }
    }
}
