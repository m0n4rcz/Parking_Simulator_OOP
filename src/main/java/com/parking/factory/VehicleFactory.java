package com.parking.factory;

import com.parking.model.Car;
import com.parking.model.Motorcycle;
import com.parking.model.SpotSize;
import com.parking.model.Truck;
import com.parking.model.Vehicle;

import java.util.Random;

/**
 * Factory Pattern chịu trách nhiệm sinh ngẫu nhiên phương tiện và biển số xe.
 */
public class VehicleFactory {
    private static final Random RANDOM = new Random();
    private static final String[] PROVINCE_CODES = {"29", "30", "31", "15", "43", "51", "59", "99"};
    private static final String LETTERS = "ABCDEFGHKLMNPSTUVXYZ";

    private VehicleFactory() {
        // Ngăn khởi tạo instance cho lớp tiện ích Factory
    }

    /**
     * Sinh ngẫu nhiên một phương tiện theo tỷ lệ:
     * 50% Motorcycle, 40% Car, 10% Truck.
     */
    public static Vehicle randomVehicle() {
        int roll = RANDOM.nextInt(100); // 0 -> 99
        if (roll < 50) {
            return new Motorcycle(randomLicensePlate(SpotSize.SMALL));
        } else if (roll < 90) {
            return new Car(randomLicensePlate(SpotSize.MEDIUM));
        } else {
            return new Truck(randomLicensePlate(SpotSize.LARGE));
        }
    }

    /**
     * Sinh biển số xe ngẫu nhiên theo định dạng Việt Nam tương ứng với kích cỡ ô đỗ.
     * - SMALL (Xe máy): VD 29B1-482.19
     * - MEDIUM (Ô tô): VD 30A-591.04
     * - LARGE (Xe tải): VD 29H-812.67
     */
    public static String randomLicensePlate(SpotSize size) {
        String province = PROVINCE_CODES[RANDOM.nextInt(PROVINCE_CODES.length)];
        int firstPart = 100 + RANDOM.nextInt(900);   // 100 - 999
        int secondPart = RANDOM.nextInt(100);        // 00 - 99

        if (size == null) {
            size = SpotSize.MEDIUM;
        }

        switch (size) {
            case SMALL:
                char motoLetter = LETTERS.charAt(RANDOM.nextInt(LETTERS.length()));
                int motoDigit = 1 + RANDOM.nextInt(9);
                return String.format("%s%c%d-%03d.%02d", province, motoLetter, motoDigit, firstPart, secondPart);
            case LARGE:
                char[] truckSeries = {'C', 'D', 'H', 'K', 'R'};
                char truckLetter = truckSeries[RANDOM.nextInt(truckSeries.length)];
                return String.format("%s%c-%03d.%02d", province, truckLetter, firstPart, secondPart);
            case MEDIUM:
            default:
                char[] carSeries = {'A', 'E', 'F', 'G', 'K', 'L'};
                char carLetter = carSeries[RANDOM.nextInt(carSeries.length)];
                return String.format("%s%c-%03d.%02d", province, carLetter, firstPart, secondPart);
        }
    }
}