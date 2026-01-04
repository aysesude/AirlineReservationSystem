package com.airline;

import com.airline.gui.LoginScreen;
import com.airline.manager.FlightManager;
import com.airline.manager.ReservationManager;
import com.airline.manager.UserManager;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Havayolu Rezervasyon Sistemi ana uygulama sınıfı.
 * JavaFX Application'dan türetilir ve uygulamayı başlatır.
 */
public class MainApp extends Application {

    // Singleton manager instances
    private static FlightManager flightManager;
    private static ReservationManager reservationManager;
    private static UserManager userManager;

    @Override
    public void start(Stage primaryStage) {
        // Manager'ları başlat
        initializeManagers();

        // Login ekranını göster
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();
    }

    /**
     * Tüm manager sınıflarını başlatır.
     */
    private void initializeManagers() {
        flightManager = new FlightManager();
        reservationManager = new ReservationManager();
        userManager = new UserManager();

        // Eğer hiç uçuş yoksa örnek veriler oluştur
        if (flightManager.getAllFlights().isEmpty()) {
            flightManager.createSampleData();
        }
    }

    /**
     * FlightManager singleton instance'ını döndürür.
     */
    public static FlightManager getFlightManager() {
        if (flightManager == null) {
            flightManager = new FlightManager();
        }
        return flightManager;
    }

    /**
     * ReservationManager singleton instance'ını döndürür.
     */
    public static ReservationManager getReservationManager() {
        if (reservationManager == null) {
            reservationManager = new ReservationManager();
        }
        return reservationManager;
    }

    /**
     * UserManager singleton instance'ını döndürür.
     */
    public static UserManager getUserManager() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    /**
     * Uygulamayı başlatır.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
