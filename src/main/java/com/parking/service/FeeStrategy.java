package com.parking.service;

import com.parking.model.SpotSize;
import java.time.LocalDateTime;

/**
 * Interface định nghĩa chiến lược tính phí gửi xe (Strategy Pattern - Slide 07).
 * Được bàn giao cho THÀNH VIÊN 2 hiện thực (HourlyFee, FlatFee).
 */
public interface FeeStrategy {
    /**
     * Tính toán số tiền cước gửi xe (VND).
     *
     * @param entryTime Thời điểm xe vào bãi
     * @param exitTime Thời điểm xe ra khỏi bãi
     * @param vehicleSize Kích cỡ/loại xe
     * @return Số tiền phí cước (VND >= 0)
     */
    long calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, SpotSize vehicleSize);

    /**
     * Tên mô tả chiến lược (hiển thị trên UI).
     */
    String getStrategyName();
}
