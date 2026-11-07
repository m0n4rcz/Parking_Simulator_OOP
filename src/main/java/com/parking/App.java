package com.parking;

/**
 * Điểm khởi chạy trung tâm (Bootstrap / Wiring) của toàn bộ ứng dụng.
 * Do NGUYỄN NHẬT ANH (Trưởng nhóm - Software Architect) phụ trách.
 * 
 * Luồng kết nối các tầng khi khởi động:
 * 1. Tầng Data:
 *    SessionRepository repo = new SqliteSessionRepository();
 *    repo.initDatabase();
 *    repo.seedDefaultSpotsIfEmpty();
 * 
 * 2. Khởi tạo SimClock an toàn (tránh lùi giờ làm âm tiền):
 *    LocalDateTime initialTime = LocalDateTime.now();
 *    LocalDateTime maxEntry = repo.findMaxActiveEntryTime().orElse(null);
 *    if (maxEntry != null && maxEntry.isAfter(initialTime)) initialTime = maxEntry;
 *    SimClock simClock = new SimClock(initialTime);
 * 
 * 3. Tầng Service & Engine:
 *    FeeStrategy feeStrategy = new HourlyFee();
 *    ParkingService service = new ParkingService(repo, feeStrategy); // Bỏ stats khỏi constructor
 *    Stats stats = new Stats();
 *    service.setStats(stats);
 *    service.addListener(stats); // Stats chỉ nhận cập nhật duy nhất qua Observer sự kiện
 * 
 * 4. Tầng UI & Controller:
 *    SimulationController simController = new SimulationController(service, simClock);
 *    MainFrame mainFrame = new MainFrame(service, simController, simClock);
 *    service.addListener(mainFrame);
 * 
 * 5. Khởi chạy giao diện:
 *    mainFrame.setVisible(true);
 */
public class App {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  HỆ THỐNG QUẢN LÝ VÀ MÔ PHỎNG BÃI ĐỖ XE THÔNG MINH");
        System.out.println("  Smart Parking Management & Simulation System (HUST OOP)");
        System.out.println("==========================================================");
        System.out.println("Nền tảng Tầng Model và các Interface kiến trúc đã sẵn sàng!");
        System.out.println("Đang chờ các thành viên hoàn tất module của mình:");
        System.out.println(" - Đỗ Anh Minh: Business Logic (ParkingService, FeeStrategy)");
        System.out.println(" - Đồng Minh Tuân: Database (SqliteSessionRepository)");
        System.out.println(" - Trần Ngọc Tân: Simulation (SimClock, Stats, SimulationController)");
        System.out.println(" - Trần Quang Thắng: GUI (MainFrame, FloorGridPanel)");
        System.out.println("----------------------------------------------------------");
        System.out.println("Chạy 'run_model_test.bat' để kiểm tra 33 tests tầng Model.");
    }
}
