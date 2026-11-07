package com.parking.model;

import java.time.LocalDateTime;

/**
 * Bộ kiểm thử tự động độc lập (Self-Test Runner) cho tầng Model của Nhóm trưởng.
 * Cho phép biên dịch và chạy kiểm tra trực tiếp qua dòng lệnh bằng JDK mà không cần phụ thuộc thư viện ngoài.
 */
public class ModelSelfTest {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("   KIỂM THỬ ĐỘC LẬP TẦNG MODEL (THÀNH VIÊN 1 - LEADER)");
        System.out.println("======================================================");

        testSpotSizeHierarchy();
        testVehicleInheritanceAndPolymorphism();
        testParkingSpotEncapsulationAndCanFit();
        testParkingSessionLifecycle();

        System.out.println("\n------------------------------------------------------");
        System.out.printf("KẾT QUẢ KIỂM THỬ: %d/%d tests thành công (%.1f%%)\n", 
                testsPassed, testsTotal, (testsPassed * 100.0) / testsTotal);
        System.out.println("======================================================");
    }

    private static void assertTrue(String testName, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println(" [PASS] " + testName);
        } else {
            System.err.println(" [FAIL] " + testName);
        }
    }

    private static void testSpotSizeHierarchy() {
        System.out.println("\n--- 1. Kiểm thử phân cấp kích thước (SpotSize) ---");
        assertTrue("SMALL vừa với SMALL", SpotSize.SMALL.canFit(SpotSize.SMALL));
        assertTrue("MEDIUM vừa với SMALL", SpotSize.MEDIUM.canFit(SpotSize.SMALL));
        assertTrue("LARGE vừa với MEDIUM", SpotSize.LARGE.canFit(SpotSize.MEDIUM));
        assertTrue("SMALL KHÔNG vừa với MEDIUM", !SpotSize.SMALL.canFit(SpotSize.MEDIUM));
        assertTrue("MEDIUM KHÔNG vừa với LARGE", !SpotSize.MEDIUM.canFit(SpotSize.LARGE));
    }

    private static void testVehicleInheritanceAndPolymorphism() {
        System.out.println("\n--- 2. Kiểm thử Kế thừa & Đa hình (Vehicle) ---");
        Vehicle bike = new Motorcycle("29B1-12345");
        Vehicle car = new Car("30A-99999");
        Vehicle truck = new Truck("29C-55555");

        assertTrue("Motorcycle kế thừa Vehicle", bike instanceof Vehicle);
        assertTrue("Car kế thừa Vehicle", car instanceof Vehicle);
        assertTrue("Truck kế thừa Vehicle", truck instanceof Vehicle);

        // Đa hình: requiredSize() trả về kích thước chính xác tương ứng
        assertTrue("Motorcycle yêu cầu cỡ SMALL", bike.requiredSize() == SpotSize.SMALL);
        assertTrue("Car yêu cầu cỡ MEDIUM", car.requiredSize() == SpotSize.MEDIUM);
        assertTrue("Truck yêu cầu cỡ LARGE", truck.requiredSize() == SpotSize.LARGE);

        // Chuẩn hóa biển số xe thành chữ hoa
        Vehicle lowercaseBike = new Motorcycle(" 29b1-88888 ");
        assertTrue("Biển số được chuẩn hóa Trim và UpperCase", lowercaseBike.getLicensePlate().equals("29B1-88888"));

        // Kiểm tra validation đóng gói
        boolean exceptionThrown = false;
        try {
            new Car("   ");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue("Ném ngoại lệ khi biển số rỗng", exceptionThrown);
    }

    private static void testParkingSpotEncapsulationAndCanFit() {
        System.out.println("\n--- 3. Kiểm thử Đóng gói & Xếp chỗ (ParkingSpot) ---");
        ParkingSpot smallSpot = new ParkingSpot("F1-S01", 1, SpotSize.SMALL);
        ParkingSpot mediumSpot = new ParkingSpot("F2-M01", 2, SpotSize.MEDIUM);
        ParkingSpot largeSpot = new ParkingSpot("F3-L01", 3, SpotSize.LARGE);

        Vehicle bike = new Motorcycle("29B1-11111");
        Vehicle car = new Car("30E-22222");
        Vehicle truck = new Truck("29C-33333");

        // Ma trận canFit theo quy tắc thiết kế:
        // Xe máy vào được cả 3 cỡ ô
        assertTrue("Xe máy vừa ô SMALL", smallSpot.canFit(bike));
        assertTrue("Xe máy vừa ô MEDIUM", mediumSpot.canFit(bike));
        assertTrue("Xe máy vừa ô LARGE", largeSpot.canFit(bike));

        // Ô tô không vừa ô SMALL, nhưng vừa ô MEDIUM và LARGE
        assertTrue("Ô tô KHÔNG vừa ô SMALL", !smallSpot.canFit(car));
        assertTrue("Ô tô vừa ô MEDIUM", mediumSpot.canFit(car));
        assertTrue("Ô tô vừa ô LARGE", largeSpot.canFit(car));

        // Xe tải chỉ vừa ô LARGE
        assertTrue("Xe tải KHÔNG vừa ô SMALL", !smallSpot.canFit(truck));
        assertTrue("Xe tải KHÔNG vừa ô MEDIUM", !mediumSpot.canFit(truck));
        assertTrue("Xe tải vừa ô LARGE", largeSpot.canFit(truck));

        // Kiểm thử occupy và release
        mediumSpot.occupy(car);
        assertTrue("Ô MEDIUM đã có xe đỗ", mediumSpot.isOccupied());
        assertTrue("Ô đã có xe thì không thể nhận thêm xe", !mediumSpot.canFit(bike));

        boolean doubleOccupyFailed = false;
        try {
            mediumSpot.occupy(bike);
        } catch (IllegalStateException e) {
            doubleOccupyFailed = true;
        }
        assertTrue("Chặn xếp đè xe vào ô đã có người đỗ (IllegalStateException)", doubleOccupyFailed);

        Vehicle releasedVehicle = mediumSpot.release();
        assertTrue("Giải phóng ô thành công", !mediumSpot.isOccupied());
        assertTrue("Phương tiện rời bãi trùng khớp", car.equals(releasedVehicle));
    }

    private static void testParkingSessionLifecycle() {
        System.out.println("\n--- 4. Kiểm thử Vòng đời phiên gửi xe (ParkingSession) ---");
        LocalDateTime entry = LocalDateTime.of(2026, 11, 7, 8, 0);
        ParkingSession session = new ParkingSession(101L, "30A-99999", "CAR", "F2-M01", entry);

        assertTrue("Phiên mới tạo ở trạng thái Active", session.isActive());
        assertTrue("Chưa kết thúc thì exitTime là null", session.getExitTime() == null);

        LocalDateTime nowSimulation = LocalDateTime.of(2026, 11, 7, 9, 30);
        assertTrue("Tính đúng thời gian đỗ (90 phút)", session.getDurationMinutes(nowSimulation) == 90);

        // Check-out
        LocalDateTime exit = LocalDateTime.of(2026, 11, 7, 10, 0);
        session.completeSession(exit, 30000L); // 30,000 VND

        assertTrue("Phiên đã kết thúc không còn Active", !session.isActive());
        assertTrue("Ghi nhận đúng mức phí", session.getFee() != null && session.getFee() == 30000L);
        assertTrue("Tính đúng thời gian đỗ sau khi hoàn tất (120 phút)", session.getDurationMinutes(null) == 120);
    }
}
