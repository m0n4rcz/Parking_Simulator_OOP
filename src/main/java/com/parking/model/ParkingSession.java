package com.parking.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Lớp biểu diễn một phiên gửi xe (Parking Session).
 * Tương ứng với bảng sessions trong cơ sở dữ liệu SQLite.
 * Tiền phí được lưu dưới dạng số nguyên long (VND) để tránh sai số số thực.
 */
public class ParkingSession {
    private final long sessionId;
    private final String licensePlate;
    private final String vehicleType;
    private final String spotId;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long fee; // Đơn vị: VND (null nếu xe chưa lấy)

    /**
     * Khởi tạo một phiên gửi xe mới khi xe vừa vào bãi.
     *
     * @param sessionId Mã phiên gửi xe (tự tăng hoặc UUID)
     * @param licensePlate Biển số xe
     * @param vehicleType Loại phương tiện ("MOTORCYCLE", "CAR", "TRUCK")
     * @param spotId Mã ô đỗ được cấp
     * @param entryTime Thời điểm xe vào bãi
     */
    public ParkingSession(long sessionId, String licensePlate, String vehicleType, 
                          String spotId, LocalDateTime entryTime) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống!");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã ô đỗ không được để trống!");
        }
        if (entryTime == null) {
            throw new IllegalArgumentException("Thời gian vào không được null!");
        }
        this.sessionId = sessionId;
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.entryTime = entryTime;
        this.exitTime = null;
        this.fee = null;
    }

    /**
     * Khởi tạo đầy đủ phiên gửi xe (dùng khi nạp từ cơ sở dữ liệu).
     */
    public ParkingSession(long sessionId, String licensePlate, String vehicleType, 
                          String spotId, LocalDateTime entryTime, LocalDateTime exitTime, Long fee) {
        this(sessionId, licensePlate, vehicleType, spotId, entryTime);
        this.exitTime = exitTime;
        this.fee = fee;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public Long getFee() {
        return fee;
    }

    /**
     * Kiểm tra xem phiên gửi xe còn đang hoạt động (xe đang ở trong bãi) hay không.
     *
     * @return true nếu xe chưa thanh toán rời bãi, ngược lại false
     */
    public boolean isActive() {
        return exitTime == null;
    }

    /**
     * Kết thúc phiên gửi xe khi xe làm thủ tục check-out.
     *
     * @param exitTime Thời điểm xe rời bãi
     * @param calculatedFee Số tiền cước đã tính toán (VND >= 0)
     */
    public void completeSession(LocalDateTime exitTime, long calculatedFee) {
        if (!isActive()) {
            throw new IllegalStateException("Phiên gửi xe này đã kết thúc trước đó!");
        }
        if (exitTime == null || exitTime.isBefore(this.entryTime)) {
            throw new IllegalArgumentException("Thời gian ra không hợp lệ (phải sau thời gian vào)!");
        }
        if (calculatedFee < 0) {
            throw new IllegalArgumentException("Tiền phí gửi xe không thể âm!");
        }
        this.exitTime = exitTime;
        this.fee = calculatedFee;
    }

    /**
     * Tính tổng thời gian đỗ xe (tính bằng phút).
     *
     * @param currentTime Thời điểm hiện tại nếu xe chưa rời bãi
     * @return Số phút đã đỗ
     */
    public long getDurationMinutes(LocalDateTime currentTime) {
        LocalDateTime end = (this.exitTime != null) ? this.exitTime : currentTime;
        if (end == null || end.isBefore(entryTime)) {
            return 0;
        }
        return Math.max(0, Duration.between(entryTime, end).toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSession that = (ParkingSession) o;
        return sessionId == that.sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return String.format("ParkingSession{id=%d, xe='%s' (%s), ô='%s', vào=%s, ra=%s, phí=%s VND}",
                sessionId, licensePlate, vehicleType, spotId, entryTime, 
                exitTime != null ? exitTime.toString() : "ĐANG ĐỖ", 
                fee != null ? fee.toString() : "Chưa thanh toán");
    }
}
