package com.airline.gui;

import com.airline.MainApp;
import com.airline.manager.FlightManager;
import com.airline.manager.ReservationManager;
import com.airline.model.*;
import com.airline.model.enums.SeatClass;
import com.airline.service.FlightSearchEngine;
import com.airline.service.PriceCalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Müşteri dashboard ekranı.
 * Uçuş arama, rezervasyon yapma ve rezervasyon yönetimi işlemleri.
 */
public class CustomerDashboard {

    private Stage stage;
    private Customer customer;
    private FlightSearchEngine searchEngine;
    private PriceCalculator priceCalculator;
    private TableView<Flight> flightTable;
    private TableView<Reservation> reservationTable;
    private ComboBox<String> departureCombo;
    private ComboBox<String> arrivalCombo;
    private DatePicker datePicker;

    public CustomerDashboard(Stage stage, Customer customer) {
        this.stage = stage;
        this.customer = customer;
        this.searchEngine = new FlightSearchEngine(MainApp.getFlightManager());
        this.priceCalculator = new PriceCalculator();
    }

    public void show() {
        stage.setTitle("Havayolu Rezervasyon - " + customer.getUsername());

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Üst menü
        mainLayout.setTop(createHeader());

        // Tab panel
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab searchTab = new Tab("🔍 Uçuş Ara", createSearchPane());
        Tab reservationsTab = new Tab("📋 Rezervasyonlarım", createReservationsPane());

        tabPane.getTabs().addAll(searchTab, reservationsTab);
        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1a237e;");

        Label titleLabel = new Label("✈ Havayolu Rezervasyon Sistemi");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Hoş geldiniz, " +
                (customer.getPassenger() != null ? customer.getPassenger().getFullName() : customer.getUsername()));
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.WHITE);

        Button logoutButton = new Button("Çıkış");
        logoutButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            MainApp.getUserManager().logout();
            new LoginScreen(stage).show();
        });

        header.getChildren().addAll(titleLabel, spacer, userLabel, new Label("  "), logoutButton);
        return header;
    }

    private VBox createSearchPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        // Arama formu
        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Kalkış şehri
        VBox depBox = new VBox(5);
        depBox.getChildren().add(new Label("Nereden"));
        departureCombo = new ComboBox<>();
        departureCombo.setPromptText("Şehir seçin");
        departureCombo.setPrefWidth(150);
        depBox.getChildren().add(departureCombo);

        // Varış şehri
        VBox arrBox = new VBox(5);
        arrBox.getChildren().add(new Label("Nereye"));
        arrivalCombo = new ComboBox<>();
        arrivalCombo.setPromptText("Şehir seçin");
        arrivalCombo.setPrefWidth(150);
        arrBox.getChildren().add(arrivalCombo);

        // Şehirleri yükle (ComboBox'lar oluşturulduktan sonra)
        loadCities();

        // Tarih
        VBox dateBox = new VBox(5);
        dateBox.getChildren().add(new Label("Tarih"));
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(150);
        dateBox.getChildren().add(datePicker);

        // Ara butonu
        Button searchButton = new Button("🔍 Ara");
        searchButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 20;");
        searchButton.setOnAction(e -> searchFlights());

        // Tüm uçuşları göster
        Button showAllButton = new Button("Tümünü Göster");
        showAllButton.setOnAction(e -> showAllFlights());

        searchBox.getChildren().addAll(depBox, arrBox, dateBox, searchButton, showAllButton);

        // Uçuş tablosu
        flightTable = createFlightTable();

        // Rezervasyon butonu
        Button reserveButton = new Button("✈ Seçili Uçuşu Rezerve Et");
        reserveButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 30;");
        reserveButton.setOnAction(e -> makeReservation());

        HBox buttonBox = new HBox(reserveButton);
        buttonBox.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(searchBox, flightTable, buttonBox);
        VBox.setVgrow(flightTable, Priority.ALWAYS);

        // Başlangıçta tüm uçuşları göster
        showAllFlights();

        return pane;
    }

    private void loadCities() {
        List<String> departures = searchEngine.getAllDepartureCities();
        List<String> arrivals = searchEngine.getAllArrivalCities();

        departureCombo.setItems(FXCollections.observableArrayList(departures));
        arrivalCombo.setItems(FXCollections.observableArrayList(arrivals));
    }

    @SuppressWarnings("unchecked")
    private TableView<Flight> createFlightTable() {
        TableView<Flight> table = new TableView<>();

        TableColumn<Flight, String> numCol = new TableColumn<>("Uçuş No");
        numCol.setCellValueFactory(new PropertyValueFactory<>("flightNum"));
        numCol.setPrefWidth(80);

        TableColumn<Flight, String> depCol = new TableColumn<>("Kalkış");
        depCol.setCellValueFactory(new PropertyValueFactory<>("departurePlace"));
        depCol.setPrefWidth(120);

        TableColumn<Flight, String> arrCol = new TableColumn<>("Varış");
        arrCol.setCellValueFactory(new PropertyValueFactory<>("arrivalPlace"));
        arrCol.setPrefWidth(120);

        TableColumn<Flight, LocalDate> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Flight, String> timeCol = new TableColumn<>("Saat");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("hour"));
        timeCol.setPrefWidth(80);

        TableColumn<Flight, String> durationCol = new TableColumn<>("Süre");
        durationCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFormattedDuration()));
        durationCol.setPrefWidth(80);

        TableColumn<Flight, Integer> seatsCol = new TableColumn<>("Boş Koltuk");
        seatsCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getAvailableSeatCount()).asObject());
        seatsCol.setPrefWidth(90);

        TableColumn<Flight, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().getDescription()));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(numCol, depCol, arrCol, dateCol, timeCol, durationCol, seatsCol, statusCol);
        table.setPlaceholder(new Label("Uçuş bulunamadı"));

        return table;
    }

    private void searchFlights() {
        String departure = departureCombo.getValue();
        String arrival = arrivalCombo.getValue();
        LocalDate date = datePicker.getValue();

        if (departure == null || arrival == null) {
            showAlert("Uyarı", "Lütfen kalkış ve varış şehirlerini seçin!");
            return;
        }

        List<Flight> results = searchEngine.searchFlights(departure, arrival, date);
        flightTable.setItems(FXCollections.observableArrayList(results));

        if (results.isEmpty()) {
            showAlert("Bilgi", "Arama kriterlerine uygun uçuş bulunamadı.");
        }
    }

    private void showAllFlights() {
        List<Flight> flights = searchEngine.getAvailableFlights();
        flightTable.setItems(FXCollections.observableArrayList(flights));
    }

    private void makeReservation() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            showAlert("Uyarı", "Lütfen bir uçuş seçin!");
            return;
        }

        if (selectedFlight.getAvailableSeatCount() == 0) {
            showAlert("Hata", "Bu uçuşta boş koltuk bulunmamaktadır!");
            return;
        }

        // Koltuk seçim dialogu
        showSeatSelectionDialog(selectedFlight);
    }

    private void showSeatSelectionDialog(Flight flight) {
        Dialog<Seat> dialog = new Dialog<>();
        dialog.setTitle("Koltuk Seçimi");
        dialog.setHeaderText(flight.getFlightSummary());

        ButtonType reserveButtonType = new ButtonType("Rezerve Et", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Sınıf seçimi
        Label classLabel = new Label("Sınıf Seçin:");
        ComboBox<SeatClass> classCombo = new ComboBox<>();
        classCombo.setItems(FXCollections.observableArrayList(SeatClass.values()));
        classCombo.setValue(SeatClass.ECONOMY);

        // Koltuk listesi
        Label seatLabel = new Label("Koltuk Seçin:");
        ListView<Seat> seatList = new ListView<>();
        seatList.setPrefHeight(200);

        // Fiyat gösterimi
        Label priceLabel = new Label("Fiyat: -");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Sınıf değiştiğinde koltukları güncelle
        classCombo.setOnAction(e -> {
            SeatClass selected = classCombo.getValue();
            List<Seat> seats = flight.getPlane().getAvailableSeatsByClass(selected);
            seatList.setItems(FXCollections.observableArrayList(seats));
        });

        // Koltuk seçildiğinde fiyatı göster
        seatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                double price = priceCalculator.calculatePrice(newVal, flight);
                priceLabel.setText(String.format("Fiyat: %.2f TL", price));
            }
        });

        // Başlangıçta economy koltukları göster
        List<Seat> economySeats = flight.getPlane().getAvailableSeatsByClass(SeatClass.ECONOMY);
        seatList.setItems(FXCollections.observableArrayList(economySeats));

        content.getChildren().addAll(classLabel, classCombo, seatLabel, seatList, priceLabel);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == reserveButtonType) {
                return seatList.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(seat -> {
            if (seat != null) {
                try {
                    Passenger passenger = customer.getPassenger();
                    if (passenger == null) {
                        passenger = new Passenger(customer.getUsername(), "",
                                customer.getEmail(), "");
                        customer.setPassenger(passenger);
                    }

                    Reservation reservation = MainApp.getReservationManager()
                            .makeReservation(flight, passenger, seat);
                    customer.addReservation(reservation);

                    double price = priceCalculator.calculatePrice(seat, flight);
                    MainApp.getReservationManager().createTicket(reservation, price);

                    showAlert("Başarılı",
                            "Rezervasyon tamamlandı!\nRezernasyon Kodu: " + reservation.getReservationCode());

                    // Tabloyu güncelle
                    showAllFlights();

                } catch (Exception e) {
                    showAlert("Hata", "Rezervasyon yapılamadı: " + e.getMessage());
                }
            }
        });
    }

    private VBox createReservationsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Rezervasyonlarım");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        reservationTable = createReservationTable();

        // Yenile butonu
        Button refreshButton = new Button("🔄 Yenile");
        refreshButton.setOnAction(e -> loadReservations());

        // İptal butonu
        Button cancelButton = new Button("❌ Seçili Rezervasyonu İptal Et");
        cancelButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> cancelReservation());

        HBox buttonBox = new HBox(15, refreshButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(titleLabel, reservationTable, buttonBox);
        VBox.setVgrow(reservationTable, Priority.ALWAYS);

        loadReservations();

        return pane;
    }

    @SuppressWarnings("unchecked")
    private TableView<Reservation> createReservationTable() {
        TableView<Reservation> table = new TableView<>();

        TableColumn<Reservation, String> codeCol = new TableColumn<>("Rezervasyon Kodu");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("reservationCode"));
        codeCol.setPrefWidth(130);

        TableColumn<Reservation, String> flightCol = new TableColumn<>("Uçuş");
        flightCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFlight().getFlightNum()));
        flightCol.setPrefWidth(80);

        TableColumn<Reservation, String> routeCol = new TableColumn<>("Rota");
        routeCol.setCellValueFactory(cellData -> {
            Flight f = cellData.getValue().getFlight();
            return new javafx.beans.property.SimpleStringProperty(
                    f.getDeparturePlace() + " → " + f.getArrivalPlace());
        });
        routeCol.setPrefWidth(180);

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFlight().getDate().toString()));
        dateCol.setPrefWidth(100);

        TableColumn<Reservation, String> seatCol = new TableColumn<>("Koltuk");
        seatCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSeat().getSeatNum()));
        seatCol.setPrefWidth(70);

        TableColumn<Reservation, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().getDescription()));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(codeCol, flightCol, routeCol, dateCol, seatCol, statusCol);
        table.setPlaceholder(new Label("Henüz rezervasyonunuz bulunmamaktadır"));

        return table;
    }

    private void loadReservations() {
        List<Reservation> reservations;
        if (customer.getPassenger() != null) {
            reservations = MainApp.getReservationManager()
                    .getReservationsByPassenger(customer.getPassenger().getPassengerId());
        } else {
            reservations = customer.getReservationHistory();
        }
        // Sadece aktif rezervasyonları göster (iptal edilmişleri filtrele)
        List<Reservation> activeReservations = reservations.stream()
                .filter(Reservation::isActive)
                .collect(java.util.stream.Collectors.toList());
        reservationTable.setItems(FXCollections.observableArrayList(activeReservations));
    }

    private void cancelReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen iptal etmek istediğiniz rezervasyonu seçin!");
            return;
        }

        if (!selected.isActive()) {
            showAlert("Bilgi", "Bu rezervasyon zaten iptal edilmiş veya tamamlanmış.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Onay");
        confirm.setHeaderText("Rezervasyon İptali");
        confirm.setContentText("Bu rezervasyonu iptal etmek istediğinizden emin misiniz?\n" +
                "Rezervasyon Kodu: " + selected.getReservationCode());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean cancelled = MainApp.getReservationManager()
                        .cancelReservation(selected.getReservationCode());
                if (cancelled) {
                    showAlert("Başarılı", "Rezervasyon iptal edildi.");
                    loadReservations();
                    showAllFlights();
                } else {
                    showAlert("Hata", "Rezervasyon iptal edilemedi.");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
