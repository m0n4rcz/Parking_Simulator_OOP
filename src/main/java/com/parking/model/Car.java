package com.parking.model;

/**
 * Lớp biểu diễn phương tiện Ô tô con.
 * Kế thừa từ Vehicle và yêu cầu kích thước ô đỗ SpotSize.MEDIUM.
 */
public class Car extends Vehicle {

    public Car(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public SpotSize requiredSize() {
        return SpotSize.MEDIUM;
    }

    @Override
    public String getVehicleType() {
        return "CAR";
    }
}
