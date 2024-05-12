package csvReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Car; // Import the Car class

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
                double mpg = Double.parseDouble(data[0]);
                int cylinders = Integer.parseInt(data[1]);
                double displacement = Double.parseDouble(data[2]);
                int horsepower = Integer.parseInt(data[3]);
                int weight = Integer.parseInt(data[4]);
                double acceleration = Double.parseDouble(data[5]);
                int modelYear = Integer.parseInt(data[6]);
                int origin = Integer.parseInt(data[7]);
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
}
