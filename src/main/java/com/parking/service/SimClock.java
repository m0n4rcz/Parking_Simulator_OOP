package com.parking.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Quản lý đồng hồ thời gian ảo của hệ thống bãi đỗ xe.
 * Đảm bảo tính nhất quán thời gian khi tua nhanh mô phỏng (1x, 2x, 5x).
 */
public class SimClock {
    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

    private LocalDateTime currentVirtualTime;

    public SimClock() {
        this.currentVirtualTime = LocalDateTime.now();
    }

    public SimClock(LocalDateTime initialTime) {
        this.currentVirtualTime = Objects.requireNonNull(initialTime, "initialTime không được null");
    }

    /**
     * Lấy mốc thời gian ảo hiện tại.
     */
    public synchronized LocalDateTime getCurrentTime() {
        return currentVirtualTime;
    }

    /**
     * Dịch chuyển đồng hồ tiến tới một số phút ảo.
     *
     * @param minutes số phút cần cộng thêm (phải >= 0)
     */
    public synchronized void advance(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Số phút dịch chuyển không được âm: " + minutes);
        }
        this.currentVirtualTime = this.currentVirtualTime.plusMinutes(minutes);
    }

    /**
     * Định dạng thời gian ảo hiện tại theo chuẩn hiển thị trên Dashboard.
     */
    public synchronized String format() {
        return currentVirtualTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Định dạng thời gian ảo theo pattern tùy chỉnh.
     */
    public synchronized String format(String pattern) {
        return currentVirtualTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public synchronized String toString() {
        return format();
    }
}