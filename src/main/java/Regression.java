import csvReader.ReadCarFromCSVFile;
import model.Car; // Import the Car class

import java.io.FileNotFoundException;
import java.util.List;

public class Regression {
    public static void main(String[] args) {
        // Create an instance of ReadCarFromCSVFile
        ReadCarFromCSVFile carReader = new ReadCarFromCSVFile();

        // Specify the filename of the CSV file containing car data
        String filename = "src/main/java/data/auto-mpg.csv"; // Adjust the filename as needed

        try {
            // Read car data from the CSV file
            List<Car> cars = carReader.read(filename);

            // Process the car data as needed
            for (Car car : cars) {
                // Print each car's details to the console
                System.out.println(car.toString());
            }
        } catch (FileNotFoundException e) {
            // Handle the case where the file is not found
            System.err.println("File not found: " + filename);
            e.printStackTrace();
        }
    }
}
