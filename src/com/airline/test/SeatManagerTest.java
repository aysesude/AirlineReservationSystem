package com.airline.test;

import com.airline.manager.SeatManager;
import com.airline.model.Plane;
import com.airline.model.Seat;
import com.airline.model.enums.SeatClass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SeatManager sınıfı için JUnit 5 testleri.
 * Koltuk yönetimi işlemlerinin doğruluğunu test eder.
 */
public class SeatManagerTest {

    private SeatManager seatManager;
    private Plane testPlane;

    @BeforeEach
    void setUp() {
        // 30 sıra, 6 koltuk, 5 business sıra, 500 TL baz fiyat
        testPlane = new Plane("TEST-001", "Boeing 737", 30, 6, 5, 500);
        seatManager = new SeatManager(testPlane);
    }

    @Test
    @DisplayName("Başlangıçta tüm koltuklar boş olmalı")
    void testInitialSeatsEmpty() {
        int emptyCount = seatManager.emptySeatsCount();
        int totalCapacity = testPlane.getCapacity();

        assertEquals(totalCapacity, emptyCount,
                "Başlangıçta tüm koltuklar boş olmalı");
        assertEquals(180, emptyCount, "30x6 = 180 koltuk olmalı");
    }

    @Test
    @DisplayName("Koltuk rezervasyonu sonrası boş koltuk sayısı azalmalı")
    void testEmptySeatsCountDecreasesAfterReservation() {
        int initialCount = seatManager.emptySeatsCount();

        // Bir koltuk rezerve et
        boolean reserved = seatManager.reserveSeat("10A");

        assertTrue(reserved, "Rezervasyon başarılı olmalı");
        assertEquals(initialCount - 1, seatManager.emptySeatsCount(),
                "Boş koltuk sayısı 1 azalmalı");
    }

    @Test
    @DisplayName("Birden fazla rezervasyon sonrası koltuk sayısı testi")
    void testMultipleReservations() {
        int initialCount = seatManager.emptySeatsCount();

        seatManager.reserveSeat("10A");
        seatManager.reserveSeat("10B");
        seatManager.reserveSeat("10C");

        assertEquals(initialCount - 3, seatManager.emptySeatsCount(),
                "Boş koltuk sayısı 3 azalmalı");
        assertEquals(3, seatManager.reservedSeatsCount(),
                "3 koltuk dolu olmalı");
    }

    @Test
    @DisplayName("Olmayan koltuk numarası için exception testi")
    void testNonExistentSeatThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.reserveSeat("99Z");
        }, "Olmayan koltuk için exception fırlatılmalı");
    }

    @Test
    @DisplayName("Geçersiz koltuk numarası formatı için exception testi")
    void testInvalidSeatFormatThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.reserveSeat("INVALID");
        }, "Geçersiz format için exception fırlatılmalı");
    }

    @Test
    @DisplayName("Null koltuk numarası için exception testi")
    void testNullSeatThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.getSeat(null);
        }, "Null koltuk için exception fırlatılmalı");
    }

    @Test
    @DisplayName("Zaten rezerve edilmiş koltuk için false dönmeli")
    void testReserveAlreadyReservedSeat() {
        seatManager.reserveSeat("15A");
        boolean secondReserve = seatManager.reserveSeat("15A");

        assertFalse(secondReserve, "Zaten rezerve koltuk için false dönmeli");
    }

    @Test
    @DisplayName("Koltuk serbest bırakma testi")
    void testReleaseSeat() {
        seatManager.reserveSeat("20A");
        int afterReserve = seatManager.emptySeatsCount();

        boolean released = seatManager.releaseSeat("20A");

        assertTrue(released, "Serbest bırakma başarılı olmalı");
        assertEquals(afterReserve + 1, seatManager.emptySeatsCount(),
                "Boş koltuk sayısı 1 artmalı");
    }

    @Test
    @DisplayName("Zaten boş koltuk için release false dönmeli")
    void testReleaseEmptySeat() {
        boolean released = seatManager.releaseSeat("25A");

        assertFalse(released, "Boş koltuk için release false dönmeli");
    }

    @Test
    @DisplayName("Koltuk bilgisi getirme testi")
    void testGetSeat() {
        Seat seat = seatManager.getSeat("5A");

        assertNotNull(seat, "Koltuk null olmamalı");
        assertEquals("5A", seat.getSeatNum());
        assertEquals(SeatClass.BUSINESS, seat.getClass_(),
                "5. sıra business olmalı");
    }

    @Test
    @DisplayName("Boş koltukları listeleme testi")
    void testGetAvailableSeats() {
        seatManager.reserveSeat("1A");
        seatManager.reserveSeat("1B");

        List<Seat> availableSeats = seatManager.getAvailableSeats();

        assertEquals(178, availableSeats.size(), "178 boş koltuk olmalı");

        for (Seat seat : availableSeats) {
            assertFalse(seat.isReserveStatus(), "Listede rezerve koltuk olmamalı");
        }
    }

    @Test
    @DisplayName("Sınıfa göre koltuk filtreleme testi")
    void testGetSeatsByClass() {
        List<Seat> businessSeats = seatManager.getSeatsByClass(SeatClass.BUSINESS);
        List<Seat> economySeats = seatManager.getSeatsByClass(SeatClass.ECONOMY);

        // 5 sıra business * 6 koltuk = 30 business koltuk
        assertEquals(30, businessSeats.size(), "30 business koltuk olmalı");

        // 25 sıra economy * 6 koltuk = 150 economy koltuk
        assertEquals(150, economySeats.size(), "150 economy koltuk olmalı");

        for (Seat seat : businessSeats) {
            assertEquals(SeatClass.BUSINESS, seat.getClass_());
        }

        for (Seat seat : economySeats) {
            assertEquals(SeatClass.ECONOMY, seat.getClass_());
        }
    }

    @Test
    @DisplayName("Koltuk müsaitlik kontrolü testi")
    void testIsSeatAvailable() {
        assertTrue(seatManager.isSeatAvailable("10A"), "Başlangıçta müsait olmalı");

        seatManager.reserveSeat("10A");

        assertFalse(seatManager.isSeatAvailable("10A"), "Rezerve sonrası müsait olmamalı");
    }

    @Test
    @DisplayName("Doluluk oranı hesaplama testi")
    void testGetOccupancyRate() {
        assertEquals(0.0, seatManager.getOccupancyRate(), 0.01,
                "Başlangıçta doluluk %0 olmalı");

        // 18 koltuk rezerve et (%10)
        for (int i = 1; i <= 3; i++) {
            for (char c = 'A'; c <= 'F'; c++) {
                seatManager.reserveSeat(i + String.valueOf(c));
            }
        }

        assertEquals(10.0, seatManager.getOccupancyRate(), 0.01,
                "18/180 = %10 doluluk olmalı");
    }

    @Test
    @DisplayName("Rastgele boş koltuk seçme testi")
    void testGetRandomAvailableSeat() {
        Seat randomSeat = seatManager.getRandomAvailableSeat();

        assertNotNull(randomSeat, "Rastgele koltuk null olmamalı");
        assertFalse(randomSeat.isReserveStatus(), "Seçilen koltuk boş olmalı");
    }

    @Test
    @DisplayName("Tüm koltuklar dolu iken rastgele koltuk null dönmeli")
    void testGetRandomAvailableSeatWhenFull() {
        // Tüm koltukları rezerve et
        for (Seat seat : seatManager.getAllSeats()) {
            seat.reserve();
        }

        Seat randomSeat = seatManager.getRandomAvailableSeat();

        assertNull(randomSeat, "Tüm koltuklar dolu iken null dönmeli");
    }

    @Test
    @DisplayName("Tüm koltukları listeleme testi")
    void testGetAllSeats() {
        List<Seat> allSeats = seatManager.getAllSeats();

        assertEquals(180, allSeats.size(), "Toplam 180 koltuk olmalı");
    }

    @Test
    @DisplayName("Koltuk düzenini sıfırlama testi")
    void testCreateSeatLayout() {
        // Bazı koltukları rezerve et
        seatManager.reserveSeat("5A");
        seatManager.reserveSeat("10B");

        // Düzeni sıfırla
        seatManager.createSeatLayout();

        assertEquals(180, seatManager.emptySeatsCount(),
                "Sıfırlama sonrası tüm koltuklar boş olmalı");
    }
}
