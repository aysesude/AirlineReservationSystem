package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Seat;
import com.airline.model.enums.SeatClass;

/**
 * Bilet fiyatı hesaplama işlemlerini gerçekleştirir.
 * JUnit testleri için kullanılacak ana sınıflardan biridir.
 */
public class PriceCalculator {

    private double basePrice;       // Baz fiyat (TL)
    private double taxRate;         // Vergi oranı (örn: 0.18 = %18)
    private double businessMultiplier;  // Business sınıf çarpanı
    private double economyMultiplier;   // Economy sınıf çarpanı

    // Varsayılan değerler
    private static final double DEFAULT_BASE_PRICE = 500.0;
    private static final double DEFAULT_TAX_RATE = 0.18;
    private static final double DEFAULT_BUSINESS_MULTIPLIER = 2.5;
    private static final double DEFAULT_ECONOMY_MULTIPLIER = 1.0;

    /**
     * Varsayılan değerlerle PriceCalculator oluşturur.
     */
    public PriceCalculator() {
        this.basePrice = DEFAULT_BASE_PRICE;
        this.taxRate = DEFAULT_TAX_RATE;
        this.businessMultiplier = DEFAULT_BUSINESS_MULTIPLIER;
        this.economyMultiplier = DEFAULT_ECONOMY_MULTIPLIER;
    }

    /**
     * Özel değerlerle PriceCalculator oluşturur.
     */
    public PriceCalculator(double basePrice, double taxRate) {
        this.basePrice = basePrice;
        this.taxRate = taxRate;
        this.businessMultiplier = DEFAULT_BUSINESS_MULTIPLIER;
        this.economyMultiplier = DEFAULT_ECONOMY_MULTIPLIER;
    }

    /**
     * Koltuk ve uçuş bilgisine göre fiyat hesaplar.
     * @param seat Koltuk
     * @param flight Uçuş
     * @return Hesaplanan fiyat (vergi dahil)
     */
    public double calculatePrice(Seat seat, Flight flight) {
        if (seat == null) {
            throw new IllegalArgumentException("Koltuk bilgisi boş olamaz!");
        }

        double price;
        if (seat.getSeatClass() == SeatClass.BUSINESS) {
            price = calculateBusinessPrice(basePrice);
        } else {
            price = calculateEconomyPrice(basePrice);
        }

        // Uçuş süresine göre ek ücret (her saat için %5 ekleme)
        if (flight != null) {
            int hours = flight.getDuration() / 60;
            price += price * (hours * 0.05);
        }

        return calculateTotalWithTax(price);
    }

    /**
     * Sadece koltuk bilgisine göre fiyat hesaplar.
     * @param seat Koltuk
     * @return Hesaplanan fiyat (vergi dahil)
     */
    public double calculatePrice(Seat seat) {
        return calculatePrice(seat, null);
    }

    /**
     * Business sınıfı fiyatını hesaplar.
     * @param base Baz fiyat
     * @return Business fiyatı (vergi hariç)
     */
    public double calculateBusinessPrice(double base) {
        if (base < 0) {
            throw new IllegalArgumentException("Baz fiyat negatif olamaz!");
        }
        return base * businessMultiplier;
    }

    /**
     * Economy sınıfı fiyatını hesaplar.
     * @param base Baz fiyat
     * @return Economy fiyatı (vergi hariç)
     */
    public double calculateEconomyPrice(double base) {
        if (base < 0) {
            throw new IllegalArgumentException("Baz fiyat negatif olamaz!");
        }
        return base * economyMultiplier;
    }

    /**
     * İndirim uygular.
     * @param price Orijinal fiyat
     * @param discountPercent İndirim yüzdesi (0-100 arası)
     * @return İndirimli fiyat
     */
    public double applyDiscount(double price, double discountPercent) {
        if (price < 0) {
            throw new IllegalArgumentException("Fiyat negatif olamaz!");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("İndirim oranı 0-100 arasında olmalıdır!");
        }
        return price * (1 - discountPercent / 100);
    }

    /**
     * Vergi miktarını hesaplar.
     * @param price Fiyat (vergi hariç)
     * @return Vergi miktarı
     */
    public double calculateTax(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Fiyat negatif olamaz!");
        }
        return price * taxRate;
    }

    /**
     * Vergi dahil toplam fiyatı hesaplar.
     * @param price Fiyat (vergi hariç)
     * @return Toplam fiyat (vergi dahil)
     */
    public double calculateTotalWithTax(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Fiyat negatif olamaz!");
        }
        return price + calculateTax(price);
    }

    /**
     * Sınıfa göre fiyat hesaplar.
     * @param seatClass Koltuk sınıfı
     * @return Hesaplanan fiyat (vergi dahil)
     */
    public double calculatePriceByClass(SeatClass seatClass) {
        double price;
        if (seatClass == SeatClass.BUSINESS) {
            price = calculateBusinessPrice(basePrice);
        } else {
            price = calculateEconomyPrice(basePrice);
        }
        return calculateTotalWithTax(price);
    }

    /**
     * Bagaj ek ücreti hesaplar.
     * @param extraKg Fazla kilo miktarı
     * @param pricePerKg Kilo başına ücret
     * @return Toplam ek ücret
     */
    public double calculateBaggageFee(double extraKg, double pricePerKg) {
        if (extraKg < 0 || pricePerKg < 0) {
            throw new IllegalArgumentException("Değerler negatif olamaz!");
        }
        return extraKg * pricePerKg;
    }

    /**
     * Fiyatı yuvarlar (2 ondalık basamak).
     * @param price Yuvarlanacak fiyat
     * @return Yuvarlanmış fiyat
     */
    public double roundPrice(double price) {
        return Math.round(price * 100.0) / 100.0;
    }

    // Getter ve Setter metodları
    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Baz fiyat negatif olamaz!");
        }
        this.basePrice = basePrice;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        if (taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException("Vergi oranı 0-1 arasında olmalıdır!");
        }
        this.taxRate = taxRate;
    }

    public double getBusinessMultiplier() {
        return businessMultiplier;
    }

    public void setBusinessMultiplier(double businessMultiplier) {
        this.businessMultiplier = businessMultiplier;
    }

    public double getEconomyMultiplier() {
        return economyMultiplier;
    }

    public void setEconomyMultiplier(double economyMultiplier) {
        this.economyMultiplier = economyMultiplier;
    }
}
