package com.parking.model;

/**
 * Lớp biểu diễn phương tiện Xe máy.
 * Kế thừa từ Vehicle (Slide 06 - Inheritance)
 * và ghi đè phương thức requiredSize() trả về SpotSize.SMALL (Slide 07 - Polymorphism).
 */
public class Motorcycle extends Vehicle {

    public Motorcycle(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public SpotSize requiredSize() {
        return SpotSize.SMALL;
    }

    @Override
    public String getVehicleType() {
        return "MOTORCYCLE";
    }
}
