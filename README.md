# Smart Parking Management & Simulation System

> **Môn học:** Object-Oriented Language and Theory (IT3100 / IT3103 - SoICT - HUST)  
> **Giảng viên hướng dẫn:** PGS. Lê Đức Hậu  
> **Trưởng nhóm & Kiến trúc sư phần mềm:** Nguyễn Nhật Anh  

---

## 1. Giới thiệu Đề tài
Dự án xây dựng phần mềm quản lý và mô phỏng bãi đỗ xe thông minh đa tầng áp dụng chuẩn mực lập trình hướng đối tượng (OOP) trên nền tảng Java 17.

Hệ thống hỗ trợ 2 chế độ vận hành:
1. **Chế độ Thủ công (Manual Mode):** Nhân viên vận hành check-in và check-out từng xe trực tiếp trên giao diện.
2. **Chế độ Mô phỏng (Simulation Mode):** Đồng hồ giả lập thời gian ảo (`SimClock`) kết hợp `javax.swing.Timer` sinh ngẫu nhiên luồng xe vào/ra bãi theo thời gian thực (hỗ trợ tốc độ 1x, 2x, 5x).

---

## 2. Kiến trúc 3 tầng (3-Tier Architecture)

```
+-------------------------------------------------------------+
| 1. UI Layer (Java Swing)                                    |
|    MainFrame, FloorGridPanel, SpotButton, SimulationControl |
+-------------------------------------------------------------+
        | (Gọi nghiệp vụ)               ^ (Observer Events)
        v                               |
+-------------------------------------------------------------+
| 2. Service Layer (Business Logic & Simulation)              |
|    ParkingService, FeeStrategy (Hourly/Flat), SimClock, Stats|
+-------------------------------------------------------------+
        | (Lưu trữ / Truy vấn)
        v
+-------------------------------------------------------------+
| 3. Data Layer (Persistence Layer)                           |
|    SessionRepository -> SqliteSessionRepository (JDBC)      |
+-------------------------------------------------------------+
```

---

## 3. Cấu trúc Thư mục Mã nguồn (Tầng Cốt lõi của Nhóm trưởng)

```text
Parking_sim_pj/
├── pom.xml                                   # Cấu hình Maven chuẩn Java 17
├── docs/
│   └── UML_DESIGN.md                         # Đặc tả thiết kế Use Case & Class Diagram
├── src/main/java/com/parking/
│   ├── App.java                              # Điểm khởi chạy trung tâm (Bootstrap & Wiring)
│   ├── model/                                # Tầng Thực thể (Model Layer)
│   │   ├── SpotSize.java                     # Enum phân cấp: SMALL, MEDIUM, LARGE
│   │   ├── Vehicle.java                      # Lớp trừu tượng (Abstraction & Encapsulation)
│   │   ├── Motorcycle.java                   # Xe máy (Inheritance & Polymorphism)
│   │   ├── Car.java                          # Ô tô con
│   │   ├── Truck.java                        # Xe tải / Xe khách
│   │   ├── ParkingSpot.java                  # Quản lý ô đỗ (canFit, occupy, release)
│   │   ├── ParkingSession.java               # Phiên gửi xe
│   │   └── ModelSelfTest.java                # Test runner độc lập cho tầng Model (33 tests pass)
│   ├── service/
│   │   └── FeeStrategy.java                  # Interface Strategy tính phí
│   ├── event/
│   │   └── ParkingListener.java              # Interface Observer thông báo sự kiện
│   └── data/
│       └── SessionRepository.java            # Interface Repository lưu trữ SQLite
└── src/test/java/com/parking/model/
    └── ModelTest.java                        # Unit Test chuẩn JUnit 5
```

---

## 4. Hướng dẫn Biên dịch và Chạy Thử nghiệm

### Yêu cầu môi trường:
- **JDK 17+**
- **Maven 3.8+** (hoặc mở trực tiếp bằng IntelliJ IDEA / Eclipse)

### Chạy kiểm thử Unit Test:
```bash
# Bằng Maven:
mvn test

# Hoặc biên dịch chạy độc lập bằng javac:
javac -encoding UTF-8 -d bin src/main/java/com/parking/model/*.java
java -cp bin com.parking.model.ModelSelfTest
```
*(Hiện tại 33/33 tests đạt 100% PASS)*.
