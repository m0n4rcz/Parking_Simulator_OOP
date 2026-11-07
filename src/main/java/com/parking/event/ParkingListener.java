package com.parking.event;

import com.parking.model.ParkingSpot;
import com.parking.model.ParkingSession;
import com.parking.model.Vehicle;

import java.time.LocalDateTime;

/**
 * Observer Interface để lắng nghe các sự kiện phát ra từ bãi xe (Observer Pattern).
 * Tách rời tầng Service khỏi giao diện Swing (Loose Coupling).
 * Giao tiếp giữa THÀNH VIÊN 2 (Service), THÀNH VIÊN 4 (Simulation) và THÀNH VIÊN 5 (UI).
 */
public interface ParkingListener {
    /**
     * Kích hoạt khi có xe đỗ thành công vào một ô.
     */
    void onVehicleParked(ParkingSpot spot, ParkingSession session);

    /**
     * Kích hoạt khi có xe rời khỏi bãi đỗ.
     */
    void onVehicleLeft(ParkingSpot spot, ParkingSession session);

    /**
     * Kích hoạt khi xe đến bãi nhưng không tìm được ô đỗ vừa kích cỡ (Bãi đầy).
     */
    void onLotFull(Vehicle rejectedVehicle);

    /**
     * Kích hoạt mỗi khi đồng hồ giả lập dịch chuyển thời gian (SimClock tick).
     */
    void onClockTick(LocalDateTime currentTime);
}
