package csvReader;

import model.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadGameFromCSVFile {

    public List<Game> read(String filename) throws FileNotFoundException {
        List<Game> gameList = new ArrayList<Game>();
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        // Skip the header row
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] data = line.split(",");

            // Ensure data array has enough elements
            if (data.length >= 11) {
                String gameID = data[0];
                // Parse date, whiteElo, blackElo, totalMoves from string to appropriate data types
                // Example: Date date = Date.parse(data[1]);
                // Make sure to handle exceptions if parsing fails

                String white = data[2];
                int whiteElo = Integer.parseInt(data[3]);
                String black = data[4];
                int blackElo = Integer.parseInt(data[5]);
                int totalMoves = Integer.parseInt(data[6]);
                String opening = data[7];
                String timeControl = data[8];
                String termination = data[9];
                String result = data[10];

                // Create a new Game object and add it to the list
                Game game = new Game(gameID, null, white, whiteElo, black, blackElo, totalMoves, opening,
                        timeControl, termination, result);
                gameList.add(game);
            }
        }
        scanner.close();
        return gameList;
    }
}
