# TÀI LIỆU THIẾT KẾ MÔ HÌNH HÓA UML (ASTAH & CLASS DIAGRAM)
**Môn học:** Object-Oriented Language and Theory (IT3100 / IT3103 - PGS. Lê Đức Hậu)  
**Đề tài:** Hệ thống Quản lý và Mô phỏng Bãi đỗ xe Thông minh (Smart Parking Simulator)  
**Tác giả:** Trưởng nhóm (Thành viên 1 - Software Architect & Domain Modeler)  

---

## 1. Biểu đồ Use Case (Use Case Diagram - Slide 02-2)

### Các tác nhân (Actors):
1. **Operator (Nhân viên vận hành):** Người thực hiện các thao tác quản lý trực tiếp bằng tay trên hệ thống.
2. **Simulation System (Hệ thống mô phỏng tự động):** Tác nhân tự động (Timer/Bot) phát sinh luồng xe và sự kiện thời gian.
3. **Database (Hệ quản trị CSDL SQLite):** Hệ thống lưu trữ phụ trợ để lưu phiên gửi xe.

### Danh sách Use Cases chính:
- **UC1: Check-in Xe (Nhận xe vào bãi):**
  - Actor: Operator, Simulation System.
  - Luồng: Nhập biển số -> Hệ thống quét ô đỗ còn trống tối ưu theo `canFit` -> Đổi trạng thái ô sang CÓ XE -> Tạo `ParkingSession`.
- **UC2: Check-out Xe (Trả xe & Thu cước):**
  - Actor: Operator, Simulation System.
  - Luồng: Chọn ô đỗ/biển số -> Tính thời gian đỗ -> Áp dụng `FeeStrategy` tính tiền -> Đổi trạng thái ô sang TRỐNG -> Đóng `ParkingSession`.
- **UC3: Điều khiển Mô phỏng (Simulation Control):**
  - Actor: Operator.
  - Luồng: Bắt đầu (Start), Tạm dừng (Pause), Đổi tốc độ (1x, 2x, 5x), Đổi cơ chế tính cước (Hourly vs Flat).
- **UC4: Giám sát & Báo cáo thống kê (View Metrics & Stats):**
  - Actor: Operator.
  - Luồng: Xem tỷ lệ lấp đầy trực tiếp, tổng doanh thu, số xe bị từ chối do hết chỗ, xem nhật ký thời gian thực.

```mermaid
flowchart LR
    Operator((Nhân viên Vận hành))
    SimSystem((Động cơ Mô phỏng))

    subgraph "Hệ thống Quản lý Bãi đỗ xe (Smart Parking)"
        UC1(["UC1: Check-in xe vào bãi"])
        UC2(["UC2: Check-out & Thu phí"])
        UC3(["UC3: Điều khiển mô phỏng (Start/Pause/Speed)"])
        UC4(["UC4: Giám sát trực quan & Thống kê"])
        UC5(["UC5: Đổi chiến lược tính phí (Strategy)"])
        
        UC1 -.->|<<include>>| UC6(["Tìm ô đỗ tối ưu (canFit)"])
        UC2 -.->|<<include>>| UC7(["Tính cước theo FeeStrategy"])
    end

    Operator --> UC1
    Operator --> UC2
    Operator --> UC3
    Operator --> UC4
    Operator --> UC5

    SimSystem --> UC1
    SimSystem --> UC2
```

---

## 2. Biểu đồ Lớp (Class Diagram - Tầng Model & Phân Tầng Hệ Thống)

Mô hình hóa chi tiết cấu trúc các lớp theo đúng 4 trụ cột OOP (Slide 03, 06, 07):

```mermaid
classDiagram
    direction TB

    class SpotSize {
        <<enumeration>>
        SMALL
        MEDIUM
        LARGE
        -int level
        -String displayName
        +canFit(SpotSize required) boolean
        +getLevel() int
        +getDisplayName() String
    }

    class Vehicle {
        <<abstract>>
        -String licensePlate
        +Vehicle(String licensePlate)
        +getLicensePlate() String
        +requiredSize()* SpotSize
        +getVehicleType()* String
        +equals(Object o) boolean
        +hashCode() int
        +toString() String
    }

    class Motorcycle {
        +Motorcycle(String licensePlate)
        +requiredSize() SpotSize
        +getVehicleType() String
    }

    class Car {
        +Car(String licensePlate)
        +requiredSize() SpotSize
        +getVehicleType() String
    }

    class Truck {
        +Truck(String licensePlate)
        +requiredSize() SpotSize
        +getVehicleType() String
    }

    class ParkingSpot {
        -String spotId
        -int floor
        -SpotSize size
        -boolean occupied
        -Vehicle currentVehicle
        +ParkingSpot(String spotId, int floor, SpotSize size)
        +getSpotId() String
        +getFloor() int
        +getSize() SpotSize
        +isOccupied() boolean
        +getCurrentVehicle() Vehicle
        +canFit(Vehicle vehicle) boolean
        +occupy(Vehicle vehicle) void
        +release() Vehicle
        +toString() String
    }

    class ParkingSession {
        -long sessionId
        -String licensePlate
        -String vehicleType
        -String spotId
        -LocalDateTime entryTime
        -LocalDateTime exitTime
        -Long fee
        +ParkingSession(long sessionId, String licensePlate, String vehicleType, String spotId, LocalDateTime entryTime)
        +isActive() boolean
        +completeSession(LocalDateTime exitTime, long fee) void
        +getDurationMinutes(LocalDateTime atTime) long
        +getFee() Long
    }

    Vehicle <|-- Motorcycle : Kế thừa (Inheritance)
    Vehicle <|-- Car : Kế thừa (Inheritance)
    Vehicle <|-- Truck : Kế thừa (Inheritance)

    ParkingSpot o-- SpotSize : Thành phần (Aggregation)
    ParkingSpot --> Vehicle : Chứa tham chiếu (0..1)
    ParkingSession ..> ParkingSpot : Liên kết phiên gửi
```

---

## 3. Hướng dẫn Nhóm trưởng vẽ trên phần mềm Astah (Slide 00, 02-2)
1. Mở phần mềm **Astah Community / Professional**.
2. Tạo file mới: `Parking_Simulator_Design.asta`.
3. **Use Case Diagram:**
   - Chọn `Diagram` -> `Use Case Diagram`.
   - Kéo 2 Actor (`Operator`, `Simulation System`) và 5 Use Cases như trên.
   - Nối quan hệ `Association`, `<<include>>` vào Use Case con.
4. **Class Diagram:**
   - Chọn `Diagram` -> `Class Diagram`.
   - Tạo các Class `Vehicle` (bật cờ `Abstract`), `Motorcycle`, `Car`, `Truck`.
   - Dùng mũi tên `Generalization` (tam giác rỗng) từ 3 lớp con chỉ về `Vehicle`.
   - Tạo enum `SpotSize` và class `ParkingSpot`. Dùng quan hệ `Aggregation` hoặc `Association`.
5. Lưu file `.asta` vào thư mục `docs/` để nộp cùng mã nguồn cho thầy Lê Đức Hậu.
