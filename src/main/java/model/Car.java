package model;

public class Car {
    private double mpg;
    private int cylinders;
    private double displacement;
    private int horsepower;
    private int weight;
    private double acceleration;
    private int modelYear;
    private int origin;
    private String carName;

    public Car(double mpg, int cylinders, double displacement, int horsepower, int weight, double acceleration, int modelYear, int origin, String carName) {
        this.mpg = mpg;
        this.cylinders = cylinders;
        this.displacement = displacement;
        this.horsepower = horsepower;
        this.weight = weight;
        this.acceleration = acceleration;
        this.modelYear = modelYear;
        this.origin = origin;
        this.carName = carName;
    }

    // toString method to display car details
    @Override
    public String toString() {
        return "Car{" +
                "mpg=" + mpg +
                ", cylinders=" + cylinders +
                ", displacement=" + displacement +
                ", horsepower=" + horsepower +
                ", weight=" + weight +
                ", acceleration=" + acceleration +
                ", modelYear=" + modelYear +
                ", origin=" + origin +
                ", carName='" + carName + '\'' +
                '}';
    }
}
