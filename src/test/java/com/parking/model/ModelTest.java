package com.parking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Tầng Model - Nhóm Trưởng")
public class ModelTest {

    private Vehicle motorcycle;
    private Vehicle car;
    private Vehicle truck;

    private ParkingSpot smallSpot;
    private ParkingSpot mediumSpot;
    private ParkingSpot largeSpot;

    @BeforeEach
    void setUp() {
        motorcycle = new Motorcycle("29B1-12345");
        car = new Car("30A-99999");
        truck = new Truck("29C-55555");

        smallSpot = new ParkingSpot("F1-S01", 1, SpotSize.SMALL);
        mediumSpot = new ParkingSpot("F2-M01", 2, SpotSize.MEDIUM);
        largeSpot = new ParkingSpot("F3-L01", 3, SpotSize.LARGE);
    }

    @Test
    @DisplayName("Kiểm tra tính Đa hình của các loại xe")
    void testVehiclePolymorphism() {
        assertEquals(SpotSize.SMALL, motorcycle.requiredSize());
        assertEquals(SpotSize.MEDIUM, car.requiredSize());
        assertEquals(SpotSize.LARGE, truck.requiredSize());

        assertEquals("MOTORCYCLE", motorcycle.getVehicleType());
        assertEquals("CAR", car.getVehicleType());
        assertEquals("TRUCK", truck.getVehicleType());
    }

    @Test
    @DisplayName("Kiểm tra tính Đóng gói và Validation của Vehicle")
    void testVehicleValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Car(""));
        assertThrows(IllegalArgumentException.class, () -> new Motorcycle("   "));
        assertThrows(IllegalArgumentException.class, () -> new Truck(null));
    }

    @Test
    @DisplayName("Kiểm tra ma trận canFit của ParkingSpot")
    void testCanFitMatrix() {
        // Motorcycle fits in all spot sizes
        assertTrue(smallSpot.canFit(motorcycle));
        assertTrue(mediumSpot.canFit(motorcycle));
        assertTrue(largeSpot.canFit(motorcycle));

        // Car fits in MEDIUM and LARGE
        assertFalse(smallSpot.canFit(car));
        assertTrue(mediumSpot.canFit(car));
        assertTrue(largeSpot.canFit(car));

        // Truck only fits in LARGE
        assertFalse(smallSpot.canFit(truck));
        assertFalse(mediumSpot.canFit(truck));
        assertTrue(largeSpot.canFit(truck));
    }

    @Test
    @DisplayName("Kiểm tra Occupy và Release của ParkingSpot")
    void testOccupyAndRelease() {
        mediumSpot.occupy(car);
        assertTrue(mediumSpot.isOccupied());
        assertFalse(mediumSpot.canFit(motorcycle));

        // Cannot double occupy
        assertThrows(IllegalStateException.class, () -> mediumSpot.occupy(motorcycle));

        Vehicle departed = mediumSpot.release();
        assertFalse(mediumSpot.isOccupied());
        assertEquals(car, departed);

        // Cannot release empty spot
        assertThrows(IllegalStateException.class, () -> mediumSpot.release());
    }

    @Test
    @DisplayName("Kiểm tra vòng đời của ParkingSession")
    void testParkingSessionLifecycle() {
        LocalDateTime entry = LocalDateTime.of(2026, 11, 7, 8, 0);
        ParkingSession session = new ParkingSession(1L, "30A-99999", "CAR", "F2-M01", entry);

        assertTrue(session.isActive());
        assertEquals(60, session.getDurationMinutes(entry.plusMinutes(60)));

        LocalDateTime exit = entry.plusHours(2);
        session.completeSession(exit, 30000L);

        assertFalse(session.isActive());
        assertEquals(30000L, session.getFee());
        assertEquals(120, session.getDurationMinutes(null));
    }
}
