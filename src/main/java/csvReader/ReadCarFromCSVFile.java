package csvReader;

import model.Car;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadCarFromCSVFile {

    public List<Car> read(String filename) throws FileNotFoundException {
        List<Car> carList = new ArrayList<>();
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
            if (data.length >= 9) { // Assuming 9 attributes for car data
                double mpg = parseDouble(data[0]);
                int cylinders = parseInt(data[1]);
                double displacement = parseDouble(data[2]);
                int horsepower = parseInt(data[3]);
                int weight = parseInt(data[4]);
                double acceleration = parseDouble(data[5]);
                int modelYear = parseInt(data[6]);
                int origin = parseInt(data[7]);
                String carName = data[8];

                // Create a new Car object and add it to the list
                Car car = new Car(mpg, cylinders, displacement, horsepower, weight, acceleration,
                        modelYear, origin, carName);
                carList.add(car);
            }
        }
        scanner.close();
        return carList;
    }

    private double parseDouble(String value) {
        if (value.equals("?")) {
            return 0; // Return 0 if the value is "?"
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0; // Return 0 if parsing fails
        }
    }

    private int parseInt(String value) {
        if (value.equals("?")) {
            return 0; // Return 0 if the value is "?"
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // Return 0 if parsing fails
        }
    }
}
