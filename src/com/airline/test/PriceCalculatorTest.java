package com.airline.test;

import com.airline.model.Seat;
import com.airline.model.enums.SeatClass;
import com.airline.service.PriceCalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PriceCalculator sınıfı için JUnit 5 testleri.
 * Fiyat hesaplama işlemlerinin doğruluğunu test eder.
 */
public class PriceCalculatorTest {

    private PriceCalculator calculator;
    private Seat economySeat;
    private Seat businessSeat;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator(500.0, 0.18); // 500 TL baz, %18 vergi
        economySeat = new Seat("15A", SeatClass.ECONOMY, 500);
        businessSeat = new Seat("3A", SeatClass.BUSINESS, 500);
    }

    @Test
    @DisplayName("Economy sınıfı fiyat hesaplama testi")
    void testCalculateEconomyPrice() {
        double price = calculator.calculateEconomyPrice(500);
        assertEquals(500.0, price, 0.01, "Economy fiyatı baz fiyata eşit olmalı");
    }

    @Test
    @DisplayName("Business sınıfı fiyat hesaplama testi")
    void testCalculateBusinessPrice() {
        double price = calculator.calculateBusinessPrice(500);
        assertEquals(1250.0, price, 0.01, "Business fiyatı baz fiyatın 2.5 katı olmalı");
    }

    @Test
    @DisplayName("Vergi hesaplama testi")
    void testCalculateTax() {
        double tax = calculator.calculateTax(1000);
        assertEquals(180.0, tax, 0.01, "1000 TL için %18 vergi = 180 TL olmalı");
    }

    @Test
    @DisplayName("Vergi dahil toplam fiyat testi")
    void testCalculateTotalWithTax() {
        double total = calculator.calculateTotalWithTax(1000);
        assertEquals(1180.0, total, 0.01, "1000 TL + %18 vergi = 1180 TL olmalı");
    }

    @Test
    @DisplayName("Economy koltuk için tam fiyat hesaplama testi")
    void testCalculatePriceForEconomySeat() {
        double price = calculator.calculatePrice(economySeat);
        // 500 * 1.0 (economy) + %18 vergi = 590 TL
        assertEquals(590.0, price, 0.01, "Economy koltuk fiyatı vergi dahil 590 TL olmalı");
    }

    @Test
    @DisplayName("Business koltuk için tam fiyat hesaplama testi")
    void testCalculatePriceForBusinessSeat() {
        double price = calculator.calculatePrice(businessSeat);
        // 500 * 2.5 (business) + %18 vergi = 1475 TL
        assertEquals(1475.0, price, 0.01, "Business koltuk fiyatı vergi dahil 1475 TL olmalı");
    }

    @Test
    @DisplayName("İndirim uygulama testi")
    void testApplyDiscount() {
        double discountedPrice = calculator.applyDiscount(1000, 20);
        assertEquals(800.0, discountedPrice, 0.01, "%20 indirimle 1000 TL = 800 TL olmalı");
    }

    @Test
    @DisplayName("Sıfır indirim testi")
    void testApplyZeroDiscount() {
        double price = calculator.applyDiscount(1000, 0);
        assertEquals(1000.0, price, 0.01, "%0 indirimle fiyat değişmemeli");
    }

    @Test
    @DisplayName("Tam indirim testi")
    void testApplyFullDiscount() {
        double price = calculator.applyDiscount(1000, 100);
        assertEquals(0.0, price, 0.01, "%100 indirimle fiyat 0 olmalı");
    }

    @Test
    @DisplayName("Negatif fiyat için exception testi")
    void testNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateTax(-100);
        }, "Negatif fiyat için exception fırlatılmalı");
    }

    @Test
    @DisplayName("Geçersiz indirim oranı için exception testi")
    void testInvalidDiscountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.applyDiscount(1000, 150);
        }, "150% indirim için exception fırlatılmalı");

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.applyDiscount(1000, -10);
        }, "Negatif indirim için exception fırlatılmalı");
    }

    @Test
    @DisplayName("Null koltuk için exception testi")
    void testNullSeatThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculatePrice(null);
        }, "Null koltuk için exception fırlatılmalı");
    }

    @Test
    @DisplayName("Bagaj ek ücreti hesaplama testi")
    void testCalculateBaggageFee() {
        double fee = calculator.calculateBaggageFee(10, 50);
        assertEquals(500.0, fee, 0.01, "10 kg fazla * 50 TL/kg = 500 TL olmalı");
    }

    @Test
    @DisplayName("Fiyat yuvarlama testi")
    void testRoundPrice() {
        double rounded = calculator.roundPrice(123.456);
        assertEquals(123.46, rounded, 0.001, "123.456 -> 123.46 olarak yuvarlanmalı");

        rounded = calculator.roundPrice(123.454);
        assertEquals(123.45, rounded, 0.001, "123.454 -> 123.45 olarak yuvarlanmalı");
    }

    @Test
    @DisplayName("Sınıfa göre fiyat hesaplama testi")
    void testCalculatePriceByClass() {
        double economyPrice = calculator.calculatePriceByClass(SeatClass.ECONOMY);
        double businessPrice = calculator.calculatePriceByClass(SeatClass.BUSINESS);

        assertTrue(businessPrice > economyPrice, 
                "Business fiyatı Economy'den yüksek olmalı");
        assertEquals(590.0, economyPrice, 0.01);
        assertEquals(1475.0, businessPrice, 0.01);
    }
}
