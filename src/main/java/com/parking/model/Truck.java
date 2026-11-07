package com.parking.model;

/**
 * Lớp biểu diễn phương tiện Xe tải / Xe khách cỡ lớn.
 * Kế thừa từ Vehicle và yêu cầu kích thước ô đỗ SpotSize.LARGE.
 */
public class Truck extends Vehicle {

    public Truck(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public SpotSize requiredSize() {
        return SpotSize.LARGE;
    }

    @Override
    public String getVehicleType() {
        return "TRUCK";
    }
}
