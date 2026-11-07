package com.parking.model;

import java.util.Objects;

/**
 * Lớp biểu diễn một ô đỗ xe trong bãi.
 * Minh họa tính Đóng gói nghiêm ngặt (Encapsulation - Slide 03):
 * - Thuộc tính dữ liệu private.
 * - Trạng thái occupied chỉ có thể thay đổi thông qua phương thức nghiệp vụ occupy() và release().
 * - Bảo đảm bất biến (Invariant): Không thể xếp đè xe vào ô đã có xe hoặc ô không vừa kích cỡ.
 */
public class ParkingSpot {
    private final String spotId;
    private final int floor;
    private final SpotSize size;
    private boolean occupied;
    private Vehicle currentVehicle;

    /**
     * Khởi tạo ô đỗ xe ban đầu ở trạng thái trống.
     *
     * @param spotId Mã ô đỗ duy nhất (Ví dụ: "F1-01")
     * @param floor Tầng chứa ô đỗ (>= 1)
     * @param size Kích thước của ô đỗ
     */
    public ParkingSpot(String spotId, int floor, SpotSize size) {
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã ô đỗ không được để trống!");
        }
        if (floor < 1) {
            throw new IllegalArgumentException("Số tầng phải lớn hơn hoặc bằng 1!");
        }
        if (size == null) {
            throw new IllegalArgumentException("Kích thước ô đỗ không được null!");
        }
        this.spotId = spotId.trim().toUpperCase();
        this.floor = floor;
        this.size = size;
        this.occupied = false;
        this.currentVehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getFloor() {
        return floor;
    }

    public SpotSize getSize() {
        return size;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    /**
     * Kiểm tra xem phương tiện có thể đỗ vừa vào ô này hay không.
     * Điều kiện:
     * 1. Ô đỗ hiện tại chưa có xe đỗ (!occupied).
     * 2. Xe hợp lệ (not null).
     * 3. Kích thước ô đỗ >= Kích thước yêu cầu của phương tiện (size.canFit).
     *
     * @param vehicle Phương tiện cần kiểm tra
     * @return true nếu có thể đỗ vừa, ngược lại false
     */
    public boolean canFit(Vehicle vehicle) {
        if (occupied || vehicle == null) {
            return false;
        }
        return this.size.canFit(vehicle.requiredSize());
    }

    /**
     * Gán xe vào ô đỗ (Chiếm giữ ô đỗ).
     *
     * @param vehicle Phương tiện vào đỗ
     * @throws IllegalStateException nếu ô đỗ đã có xe
     * @throws IllegalArgumentException nếu xe không vừa kích thước
     */
    public void occupy(Vehicle vehicle) {
        if (this.occupied) {
            throw new IllegalStateException("Ô đỗ " + spotId + " hiện đã có xe đang đỗ!");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Không thể gán phương tiện null vào ô đỗ!");
        }
        if (!this.size.canFit(vehicle.requiredSize())) {
            throw new IllegalArgumentException(String.format(
                    "Ô đỗ cỡ %s không đủ sức chứa phương tiện %s yêu cầu cỡ %s!",
                    this.size, vehicle.getVehicleType(), vehicle.requiredSize()));
        }
        this.occupied = true;
        this.currentVehicle = vehicle;
    }

    /**
     * Giải phóng ô đỗ khi xe rời đi.
     *
     * @return Phương tiện vừa rời khỏi ô đỗ
     * @throws IllegalStateException nếu ô đỗ vốn đang trống
     */
    public Vehicle release() {
        if (!this.occupied) {
            throw new IllegalStateException("Ô đỗ " + spotId + " đang trống, không thể giải phóng!");
        }
        Vehicle departedVehicle = this.currentVehicle;
        this.occupied = false;
        this.currentVehicle = null;
        return departedVehicle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpot that = (ParkingSpot) o;
        return Objects.equals(spotId, that.spotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spotId);
    }

    @Override
    public String toString() {
        return String.format("ParkingSpot{id='%s', tầng=%d, cỡ=%s, trạng_thái=%s, xe=%s}",
                spotId, floor, size, occupied ? "CÓ XE" : "TRỐNG", 
                currentVehicle != null ? currentVehicle.getLicensePlate() : "None");
    }
}
