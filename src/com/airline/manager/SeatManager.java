package com.airline.manager;

import com.airline.model.Plane;
import com.airline.model.Seat;
import com.airline.model.enums.SeatClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Koltuk yönetimi işlemlerini gerçekleştirir.
 * Koltuk düzeni oluşturma, boş koltuk sayısı hesaplama ve koltuk rezervasyonu yapar.
 * JUnit testleri için kullanılacak ana sınıflardan biridir.
 */
public class SeatManager {

    private Plane plane;

    /**
     * Belirli bir uçak için SeatManager oluşturur.
     * @param plane Yönetilecek uçak
     */
    public SeatManager(Plane plane) {
        this.plane = plane;
    }

    /**
     * Yeni koltuk düzeni oluşturur.
     * Bu metod Plane içinde zaten çağrılıyor, 
     * ancak manuel olarak yeniden oluşturmak için kullanılabilir.
     */
    public void createSeatLayout() {
        if (plane != null) {
            plane.resetAllSeats();
        }
    }

    /**
     * Boş koltuk sayısını döndürür.
     * @return Boş koltuk sayısı
     */
    public int emptySeatsCount() {
        if (plane == null) {
            return 0;
        }
        return plane.getAvailableSeatCount();
    }

    /**
     * Dolu koltuk sayısını döndürür.
     * @return Dolu koltuk sayısı
     */
    public int reservedSeatsCount() {
        if (plane == null) {
            return 0;
        }
        return plane.getReservedSeatCount();
    }

    /**
     * Belirtilen koltuğu rezerve eder.
     * @param seatNum Koltuk numarası (örn: "15A")
     * @return Rezervasyon başarılı ise true
     * @throws IllegalArgumentException Koltuk bulunamazsa
     */
    public boolean reserveSeat(String seatNum) throws IllegalArgumentException {
        if (plane == null) {
            throw new IllegalArgumentException("Uçak tanımlanmamış!");
        }
        
        Seat seat = plane.getSeat(seatNum);
        if (seat == null) {
            throw new IllegalArgumentException("Koltuk bulunamadı: " + seatNum);
        }
        
        if (seat.isReserved()) {
            return false; // Koltuk zaten rezerve
        }
        
        seat.reserve();
        return true;
    }

    /**
     * Belirtilen koltuğu serbest bırakır.
     * @param seatNum Koltuk numarası
     * @return İşlem başarılı ise true
     * @throws IllegalArgumentException Koltuk bulunamazsa
     */
    public boolean releaseSeat(String seatNum) throws IllegalArgumentException {
        if (plane == null) {
            throw new IllegalArgumentException("Uçak tanımlanmamış!");
        }
        
        Seat seat = plane.getSeat(seatNum);
        if (seat == null) {
            throw new IllegalArgumentException("Koltuk bulunamadı: " + seatNum);
        }
        
        if (!seat.isReserved()) {
            return false; // Koltuk zaten boş
        }
        
        seat.release();
        return true;
    }

    /**
     * Belirtilen numaralı koltuğu döndürür.
     * @param seatNum Koltuk numarası
     * @return Koltuk nesnesi
     * @throws IllegalArgumentException Koltuk bulunamazsa
     */
    public Seat getSeat(String seatNum) throws IllegalArgumentException {
        if (plane == null) {
            throw new IllegalArgumentException("Uçak tanımlanmamış!");
        }
        
        Seat seat = plane.getSeat(seatNum);
        if (seat == null) {
            throw new IllegalArgumentException("Koltuk bulunamadı: " + seatNum);
        }
        
        return seat;
    }

    /**
     * Tüm boş koltukları döndürür.
     * @return Boş koltuklar listesi
     */
    public List<Seat> getAvailableSeats() {
        if (plane == null) {
            return new ArrayList<>();
        }
        return plane.getAvailableSeats();
    }

    /**
     * Belirli bir sınıftaki boş koltukları döndürür.
     * @param seatClass Koltuk sınıfı (ECONOMY veya BUSINESS)
     * @return Belirtilen sınıftaki boş koltuklar
     */
    public List<Seat> getSeatsByClass(SeatClass seatClass) {
        if (plane == null) {
            return new ArrayList<>();
        }
        return plane.getAvailableSeatsByClass(seatClass);
    }

    /**
     * Tüm koltukları döndürür.
     * @return Tüm koltuklar listesi
     */
    public List<Seat> getAllSeats() {
        if (plane == null) {
            return new ArrayList<>();
        }
        return plane.getAllSeats();
    }

    /**
     * Koltuğun müsait olup olmadığını kontrol eder.
     * @param seatNum Koltuk numarası
     * @return Müsait ise true
     */
    public boolean isSeatAvailable(String seatNum) {
        try {
            Seat seat = getSeat(seatNum);
            return !seat.isReserved();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Doluluk oranını hesaplar.
     * @return Doluluk oranı (0-100 arası)
     */
    public double getOccupancyRate() {
        if (plane == null || plane.getCapacity() == 0) {
            return 0;
        }
        return ((double) reservedSeatsCount() / plane.getCapacity()) * 100;
    }

    /**
     * Rastgele bir boş koltuk seçer.
     * @return Rastgele boş koltuk veya null
     */
    public Seat getRandomAvailableSeat() {
        List<Seat> availableSeats = getAvailableSeats();
        if (availableSeats.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * availableSeats.size());
        return availableSeats.get(randomIndex);
    }

    /**
     * Yönetilen uçağı döndürür.
     */
    public Plane getPlane() {
        return plane;
    }

    /**
     * Yönetilecek uçağı ayarlar.
     */
    public void setPlane(Plane plane) {
        this.plane = plane;
    }
}
