package com.parking.model;

import java.util.Objects;

/**
 * Lớp trừu tượng đại diện cho một phương tiện giao thông.
 * Minh họa tính Trừu tượng (Abstraction - Slide 03, 07)
 * và Đóng gói dữ liệu (Encapsulation - Slide 03).
 */
public abstract class Vehicle {
    private final String licensePlate;

    /**
     * Khởi tạo phương tiện với biển số xe.
     * Áp dụng kỹ thuật kiểm tra hợp lệ dữ liệu đầu vào (Validation).
     *
     * @param licensePlate Biển kiểm soát xe (không được rỗng)
     * @throws IllegalArgumentException nếu biển số null hoặc rỗng
     */
    public Vehicle(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống!");
        }
        this.licensePlate = licensePlate.trim().toUpperCase();
    }

    /**
     * Lấy biển kiểm soát của phương tiện.
     *
     * @return Chuỗi biển số xe
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Phương thức trừu tượng: Yêu cầu kích thước ô đỗ tối thiểu cho loại xe này.
     * Minh họa tính Đa hình (Polymorphism - Slide 07).
     *
     * @return Kích thước ô đỗ yêu cầu (SpotSize)
     */
    public abstract SpotSize requiredSize();

    /**
     * Tên loại phương tiện (dùng cho hiển thị UI và lưu trữ CSDL).
     *
     * @return Chuỗi định danh loại phương tiện ("MOTORCYCLE", "CAR", "TRUCK")
     */
    public abstract String getVehicleType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(licensePlate, vehicle.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licensePlate);
    }

    @Override
    public String toString() {
        return String.format("[%s] Biển số: %s (Cần ô: %s)", 
                getVehicleType(), licensePlate, requiredSize());
    }
}
