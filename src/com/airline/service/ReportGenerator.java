package com.airline.service;

import com.airline.manager.FlightManager;
import com.airline.manager.ReservationManager;
import com.airline.model.Flight;
import com.airline.model.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Rapor oluşturma işlemlerini gerçekleştirir.
 * Runnable interface'ini implement ederek ayrı thread'de çalışır.
 * Bu sayede GUI thread'i bloke olmaz (Senaryo 2).
 */
public class ReportGenerator implements Runnable {

    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private Consumer<String> onComplete; // Rapor tamamlandığında çağrılacak callback
    private Consumer<Integer> onProgress; // İlerleme güncellemesi için callback
    private String reportResult;
    private ReportType reportType;
    private volatile boolean cancelled = false;

    /**
     * Rapor türleri
     */
    public enum ReportType {
        OCCUPANCY,      // Doluluk oranı raporu
        REVENUE,        // Gelir raporu
        RESERVATION,    // Rezervasyon raporu
        FULL            // Tam rapor (hepsi)
    }

    /**
     * ReportGenerator oluşturur.
     */
    public ReportGenerator(FlightManager flightManager, ReservationManager reservationManager) {
        this.flightManager = flightManager;
        this.reservationManager = reservationManager;
        this.reportType = ReportType.OCCUPANCY;
    }

    /**
     * Rapor türü ile oluşturur.
     */
    public ReportGenerator(FlightManager flightManager, ReservationManager reservationManager, 
                           ReportType reportType) {
        this.flightManager = flightManager;
        this.reservationManager = reservationManager;
        this.reportType = reportType;
    }

    @Override
    public void run() {
        try {
            switch (reportType) {
                case OCCUPANCY:
                    reportResult = generateOccupancyReport();
                    break;
                case REVENUE:
                    reportResult = generateRevenueReport();
                    break;
                case RESERVATION:
                    reportResult = generateReservationReport();
                    break;
                case FULL:
                    reportResult = generateFullReport();
                    break;
            }

            // Rapor tamamlandı, callback'i çağır
            if (onComplete != null && !cancelled) {
                onComplete.accept(reportResult);
            }
        } catch (Exception e) {
            reportResult = "Rapor oluşturulurken hata: " + e.getMessage();
            if (onComplete != null) {
                onComplete.accept(reportResult);
            }
        }
    }

    /**
     * Doluluk oranı raporu oluşturur.
     */
    public String generateOccupancyReport() {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("              UÇUŞ DOLULUK ORANI RAPORU\n");
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("Oluşturulma: ").append(getCurrentDateTime()).append("\n\n");

        List<Flight> flights = flightManager.getAllFlights();
        
        if (flights.isEmpty()) {
            report.append("Henüz kayıtlı uçuş bulunmamaktadır.\n");
            return report.toString();
        }

        double totalOccupancy = 0;
        int flightCount = 0;

        for (Flight flight : flights) {
            if (cancelled) return "Rapor iptal edildi.";
            
            // Simüle edilmiş gecikme (uzun işlem)
            simulateDelay(100);
            
            double occupancy = calculateOccupancyRate(flight);
            totalOccupancy += occupancy;
            flightCount++;

            report.append(String.format("%-10s | %-15s → %-15s | Doluluk: %6.2f%%\n",
                    flight.getFlightNum(),
                    flight.getDeparturePlace(),
                    flight.getArrivalPlace(),
                    occupancy));

            // İlerleme güncellemesi
            if (onProgress != null) {
                int progress = (flightCount * 100) / flights.size();
                onProgress.accept(progress);
            }
        }

        report.append("───────────────────────────────────────────────────────────\n");
        double avgOccupancy = flightCount > 0 ? totalOccupancy / flightCount : 0;
        report.append(String.format("ORTALAMA DOLULUK ORANI: %.2f%%\n", avgOccupancy));
        report.append("Toplam Uçuş Sayısı: ").append(flightCount).append("\n");
        report.append("═══════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    /**
     * Gelir raporu oluşturur.
     */
    public String generateRevenueReport() {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("                    GELİR RAPORU\n");
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("Oluşturulma: ").append(getCurrentDateTime()).append("\n\n");

        List<Reservation> reservations = reservationManager.getAllReservations();
        
        if (reservations.isEmpty()) {
            report.append("Henüz kayıtlı rezervasyon bulunmamaktadır.\n");
            return report.toString();
        }

        double totalRevenue = 0;
        int confirmedCount = 0;
        int cancelledCount = 0;

        for (Reservation res : reservations) {
            if (cancelled) return "Rapor iptal edildi.";
            
            simulateDelay(50);

            double price = res.getSeat().getCalculatedPrice();
            
            switch (res.getStatus()) {
                case CONFIRMED:
                case COMPLETED:
                    totalRevenue += price;
                    confirmedCount++;
                    break;
                case CANCELLED:
                    cancelledCount++;
                    break;
                default:
                    break;
            }
        }

        report.append(String.format("Onaylanan Rezervasyon  : %d\n", confirmedCount));
        report.append(String.format("İptal Edilen          : %d\n", cancelledCount));
        report.append(String.format("Toplam Rezervasyon    : %d\n", reservations.size()));
        report.append("───────────────────────────────────────────────────────────\n");
        report.append(String.format("TOPLAM GELİR          : %.2f TL\n", totalRevenue));
        report.append("═══════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    /**
     * Rezervasyon raporu oluşturur.
     */
    public String generateReservationReport() {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("                 REZERVASYON RAPORU\n");
        report.append("═══════════════════════════════════════════════════════════\n");
        report.append("Oluşturulma: ").append(getCurrentDateTime()).append("\n\n");

        List<Reservation> reservations = reservationManager.getAllReservations();
        
        if (reservations.isEmpty()) {
            report.append("Henüz kayıtlı rezervasyon bulunmamaktadır.\n");
            return report.toString();
        }

        for (Reservation res : reservations) {
            if (cancelled) return "Rapor iptal edildi.";
            
            simulateDelay(30);

            report.append(String.format("Kod: %s | %s | %s → %s | Koltuk: %s | %s\n",
                    res.getReservationCode(),
                    res.getPassenger().getFullName(),
                    res.getFlight().getDeparturePlace(),
                    res.getFlight().getArrivalPlace(),
                    res.getSeat().getSeatNum(),
                    res.getStatus().getDescription()));
        }

        report.append("───────────────────────────────────────────────────────────\n");
        report.append("Toplam Rezervasyon: ").append(reservations.size()).append("\n");
        report.append("═══════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    /**
     * Tam rapor (tüm raporları birleştirir).
     */
    public String generateFullReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("\n\n");
        report.append(generateOccupancyReport());
        report.append("\n\n");
        report.append(generateRevenueReport());
        report.append("\n\n");
        report.append(generateReservationReport());
        
        return report.toString();
    }

    /**
     * Bir uçuşun doluluk oranını hesaplar.
     * @param flight Uçuş
     * @return Doluluk oranı (0-100)
     */
    public double calculateOccupancyRate(Flight flight) {
        if (flight == null || flight.getPlane() == null) {
            return 0;
        }
        return flight.getOccupancyRate();
    }

    /**
     * Rapor tamamlandığında çağrılacak callback'i ayarlar.
     */
    public void onReportComplete(Consumer<String> callback) {
        this.onComplete = callback;
    }

    /**
     * İlerleme güncellemesi callback'ini ayarlar.
     */
    public void onProgressUpdate(Consumer<Integer> callback) {
        this.onProgress = callback;
    }

    /**
     * Raporu iptal eder.
     */
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Şu anki tarih ve saati formatlanmış olarak döndürür.
     */
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    /**
     * Yapay gecikme ekler (uzun işlem simülasyonu).
     */
    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Rapor sonucunu döndürür.
     */
    public String getReportResult() {
        return reportResult;
    }

    /**
     * Rapor türünü ayarlar.
     */
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    /**
     * Rapor türünü döndürür.
     */
    public ReportType getReportType() {
        return reportType;
    }
}
