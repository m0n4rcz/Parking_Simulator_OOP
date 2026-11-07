package com.parking.data;

import com.parking.model.ParkingSession;
import com.parking.model.ParkingSpot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface cho tầng lưu trữ dữ liệu phiên gửi xe và ô đỗ (Repository Pattern).
 * Phân tách tầng trừu tượng (Abstraction - Slide 07) khỏi SQLite JDBC.
 * Được bàn giao cho ĐỒNG MINH TUÂN hiện thực (SqliteSessionRepository).
 */
public interface SessionRepository {
    /**
     * Khởi tạo các bảng dữ liệu (spots, sessions) nếu chưa tồn tại.
     */
    void initDatabase();

    /**
     * Tự động nạp danh sách 60 ô đỗ mẫu nếu bảng spots đang trống.
     */
    void seedDefaultSpotsIfEmpty();

    /**
     * Đọc toàn bộ danh sách các ô đỗ từ cơ sở dữ liệu lên bộ nhớ.
     */
    List<ParkingSpot> loadAllSpots();

    /**
     * Thêm mới một phiên gửi xe (khi xe vào bãi).
     */
    void save(ParkingSession session);

    /**
     * Cập nhật thông tin phiên gửi xe (khi xe rời bãi: ghi nhận exitTime và fee).
     */
    void update(ParkingSession session);

    /**
     * Tìm phiên gửi xe đang hoạt động theo mã ô đỗ.
     */
    Optional<ParkingSession> findActiveSessionBySpotId(String spotId);

    /**
     * Tìm phiên gửi xe đang hoạt động theo biển số xe.
     */
    Optional<ParkingSession> findActiveSessionByLicensePlate(String licensePlate);

    /**
     * Lấy toàn bộ danh sách các phiên đang đỗ xe trong bãi (exit_time IS NULL).
     * Dùng để khôi phục trạng thái bãi xe khi khởi động ứng dụng.
     */
    List<ParkingSession> findAllActiveSessions();

    /**
     * Lấy lịch sử các phiên gửi xe đã hoàn thành.
     */
    List<ParkingSession> findHistorySessions(int limit);

    /**
     * Lấy mốc thời gian vào lớn nhất trong các phiên đang đỗ (exit_time IS NULL).
     * Dùng để khởi tạo SimClock tránh trường hợp thời gian ảo bị lùi về quá khứ khi mở lại app.
     */
    default Optional<LocalDateTime> findMaxActiveEntryTime() {
        return findAllActiveSessions().stream()
                .map(ParkingSession::getEntryTime)
                .max(LocalDateTime::compareTo);
    }
}
