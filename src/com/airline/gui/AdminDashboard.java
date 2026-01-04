package com.airline.gui;

import com.airline.MainApp;
import com.airline.model.*;
import com.airline.model.enums.FlightStatus;
import com.airline.service.ReportGenerator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.time.LocalTime;
import java.util.List;

/**
 * Admin/Personel dashboard ekranı.
 * Uçuş yönetimi, rezervasyon görüntüleme ve rapor oluşturma işlemleri.
 */
public class AdminDashboard {

    private Stage stage;
    private User user;
    private TableView<Flight> flightTable;
    private TableView<Reservation> reservationTable;
    private TextArea reportArea;
    private Label statusLabel;
    private ProgressBar progressBar;

    public AdminDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        stage.setTitle("Yönetim Paneli - " + user.getUsername());

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Üst menü
        mainLayout.setTop(createHeader());

        // Tab panel
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab flightsTab = new Tab("✈ Uçuş Yönetimi", createFlightsPane());
        Tab reservationsTab = new Tab("📋 Rezervasyonlar", createReservationsPane());
        Tab reportsTab = new Tab("📊 Raporlar", createReportsPane());
        Tab simulationTab = new Tab("🔄 Simülasyon", createSimulationPane());

        tabPane.getTabs().addAll(flightsTab, reservationsTab, reportsTab, simulationTab);
        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 1100, 750);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1a237e;");

        Label titleLabel = new Label("✈ Havayolu Yönetim Paneli");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("👤 " + user.getUsername() + " (" + user.getRole().getDescription() + ")");
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

    private VBox createFlightsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Uçuş Yönetimi");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Butonlar
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("➕ Yeni Uçuş Ekle");
        addButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        addButton.setOnAction(e -> showAddFlightDialog());

        Button editButton = new Button("✏ Düzenle");
        editButton.setOnAction(e -> showEditFlightDialog());

        Button deleteButton = new Button("🗑 Sil");
        deleteButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteFlight());

        Button refreshButton = new Button("🔄 Yenile");
        refreshButton.setOnAction(e -> loadFlights());

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);

        flightTable = createFlightTable();

        pane.getChildren().addAll(titleLabel, buttonBox, flightTable);
        VBox.setVgrow(flightTable, Priority.ALWAYS);

        loadFlights();

        return pane;
    }

    @SuppressWarnings("unchecked")
    private TableView<Flight> createFlightTable() {
        TableView<Flight> table = new TableView<>();

        TableColumn<Flight, String> numCol = new TableColumn<>("Uçuş No");
        numCol.setCellValueFactory(new PropertyValueFactory<>("flightNum"));
        numCol.setPrefWidth(80);

        TableColumn<Flight, String> depCol = new TableColumn<>("Kalkış");
        depCol.setCellValueFactory(new PropertyValueFactory<>("departurePlace"));
        depCol.setPrefWidth(100);

        TableColumn<Flight, String> arrCol = new TableColumn<>("Varış");
        arrCol.setCellValueFactory(new PropertyValueFactory<>("arrivalPlace"));
        arrCol.setPrefWidth(100);

        TableColumn<Flight, LocalDate> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Flight, String> timeCol = new TableColumn<>("Saat");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("hour"));
        timeCol.setPrefWidth(70);

        TableColumn<Flight, Integer> durationCol = new TableColumn<>("Süre(dk)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setPrefWidth(70);

        TableColumn<Flight, String> planeCol = new TableColumn<>("Uçak");
        planeCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPlane().getPlaneModel()));
        planeCol.setPrefWidth(100);

        TableColumn<Flight, Integer> capacityCol = new TableColumn<>("Kapasite");
        capacityCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getPlane().getCapacity()).asObject());
        capacityCol.setPrefWidth(70);

        TableColumn<Flight, Integer> availableCol = new TableColumn<>("Boş");
        availableCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getAvailableSeatCount()).asObject());
        availableCol.setPrefWidth(50);

        TableColumn<Flight, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().getDescription()));
        statusCol.setPrefWidth(90);

        table.getColumns().addAll(numCol, depCol, arrCol, dateCol, timeCol, 
                durationCol, planeCol, capacityCol, availableCol, statusCol);

        return table;
    }

    private void loadFlights() {
        List<Flight> flights = MainApp.getFlightManager().getAllFlights();
        flightTable.setItems(FXCollections.observableArrayList(flights));
    }

    private void showAddFlightDialog() {
        Dialog<Flight> dialog = new Dialog<>();
        dialog.setTitle("Yeni Uçuş Ekle");
        dialog.setHeaderText("Uçuş bilgilerini girin");

        ButtonType addButtonType = new ButtonType("Ekle", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField flightNumField = new TextField();
        flightNumField.setPromptText("TK001");
        TextField departureField = new TextField();
        departureField.setPromptText("İstanbul");
        TextField arrivalField = new TextField();
        arrivalField.setPromptText("Ankara");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        TextField hourField = new TextField();
        hourField.setPromptText("10:30");
        TextField durationField = new TextField();
        durationField.setPromptText("60");
        TextField planeIdField = new TextField();
        planeIdField.setPromptText("TC-001");
        TextField planeModelField = new TextField();
        planeModelField.setPromptText("Boeing 737");

        grid.add(new Label("Uçuş No:"), 0, 0);
        grid.add(flightNumField, 1, 0);
        grid.add(new Label("Kalkış:"), 0, 1);
        grid.add(departureField, 1, 1);
        grid.add(new Label("Varış:"), 0, 2);
        grid.add(arrivalField, 1, 2);
        grid.add(new Label("Tarih:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Saat (HH:mm):"), 0, 4);
        grid.add(hourField, 1, 4);
        grid.add(new Label("Süre (dk):"), 0, 5);
        grid.add(durationField, 1, 5);
        grid.add(new Label("Uçak ID:"), 0, 6);
        grid.add(planeIdField, 1, 6);
        grid.add(new Label("Uçak Model:"), 0, 7);
        grid.add(planeModelField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String[] timeParts = hourField.getText().split(":");
                    LocalTime time = LocalTime.of(
                            Integer.parseInt(timeParts[0]), 
                            Integer.parseInt(timeParts[1]));
                    
                    Plane plane = new Plane(planeIdField.getText(), planeModelField.getText(), 500);
                    
                    return MainApp.getFlightManager().createFlight(
                            flightNumField.getText(),
                            departureField.getText(),
                            arrivalField.getText(),
                            datePicker.getValue(),
                            time,
                            Integer.parseInt(durationField.getText()),
                            plane);
                } catch (Exception e) {
                    showAlert("Hata", "Geçersiz bilgi: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(flight -> {
            if (flight != null) {
                showAlert("Başarılı", "Uçuş eklendi: " + flight.getFlightNum());
                loadFlights();
            }
        });
    }

    private void showEditFlightDialog() {
        Flight selected = flightTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen düzenlemek istediğiniz uçuşu seçin!");
            return;
        }

        Dialog<Flight> dialog = new Dialog<>();
        dialog.setTitle("Uçuş Düzenle");
        dialog.setHeaderText("Uçuş: " + selected.getFlightNum());

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker(selected.getDate());
        TextField hourField = new TextField(selected.getHour().toString());
        TextField durationField = new TextField(String.valueOf(selected.getDuration()));
        ComboBox<FlightStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(FlightStatus.values()));
        statusCombo.setValue(selected.getStatus());

        grid.add(new Label("Tarih:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Saat:"), 0, 1);
        grid.add(hourField, 1, 1);
        grid.add(new Label("Süre (dk):"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Durum:"), 0, 3);
        grid.add(statusCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String[] timeParts = hourField.getText().split(":");
                    selected.setDate(datePicker.getValue());
                    selected.setHour(LocalTime.of(
                            Integer.parseInt(timeParts[0]), 
                            Integer.parseInt(timeParts[1])));
                    selected.setDuration(Integer.parseInt(durationField.getText()));
                    selected.setStatus(statusCombo.getValue());
                    MainApp.getFlightManager().updateFlight(selected);
                    return selected;
                } catch (Exception e) {
                    showAlert("Hata", "Geçersiz bilgi: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(flight -> {
            if (flight != null) {
                showAlert("Başarılı", "Uçuş güncellendi!");
                loadFlights();
            }
        });
    }

    private void deleteFlight() {
        Flight selected = flightTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen silmek istediğiniz uçuşu seçin!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Onay");
        confirm.setHeaderText("Uçuş Silme");
        confirm.setContentText("Bu uçuşu silmek istediğinizden emin misiniz?\n" + selected.getFlightNum());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                MainApp.getFlightManager().deleteFlight(selected.getFlightNum());
                showAlert("Başarılı", "Uçuş silindi.");
                loadFlights();
            }
        });
    }

    private VBox createReservationsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Tüm Rezervasyonlar");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Button refreshButton = new Button("🔄 Yenile");
        refreshButton.setOnAction(e -> loadReservations());

        reservationTable = createReservationTable();

        pane.getChildren().addAll(titleLabel, refreshButton, reservationTable);
        VBox.setVgrow(reservationTable, Priority.ALWAYS);

        loadReservations();

        return pane;
    }

    @SuppressWarnings("unchecked")
    private TableView<Reservation> createReservationTable() {
        TableView<Reservation> table = new TableView<>();

        TableColumn<Reservation, String> codeCol = new TableColumn<>("Kod");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("reservationCode"));
        codeCol.setPrefWidth(100);

        TableColumn<Reservation, String> passengerCol = new TableColumn<>("Yolcu");
        passengerCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPassenger().getFullName()));
        passengerCol.setPrefWidth(150);

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

        TableColumn<Reservation, String> seatCol = new TableColumn<>("Koltuk");
        seatCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSeat().getSeatNum() + " (" + 
                        cellData.getValue().getSeat().getSeatClass() + ")"));
        seatCol.setPrefWidth(100);

        TableColumn<Reservation, String> dateCol = new TableColumn<>("Rez. Tarihi");
        dateCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFormattedDate()));
        dateCol.setPrefWidth(130);

        TableColumn<Reservation, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().getDescription()));
        statusCol.setPrefWidth(90);

        table.getColumns().addAll(codeCol, passengerCol, flightCol, routeCol, seatCol, dateCol, statusCol);

        return table;
    }

    private void loadReservations() {
        List<Reservation> reservations = MainApp.getReservationManager().getAllReservations();
        reservationTable.setItems(FXCollections.observableArrayList(reservations));
    }

    private VBox createReportsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Raporlar (Asenkron)");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Rapor butonları
        HBox buttonBox = new HBox(10);

        Button occupancyButton = new Button("📊 Doluluk Raporu");
        occupancyButton.setOnAction(e -> generateReport(ReportGenerator.ReportType.OCCUPANCY));

        Button revenueButton = new Button("💰 Gelir Raporu");
        revenueButton.setOnAction(e -> generateReport(ReportGenerator.ReportType.REVENUE));

        Button reservationButton = new Button("📋 Rezervasyon Raporu");
        reservationButton.setOnAction(e -> generateReport(ReportGenerator.ReportType.RESERVATION));

        Button fullButton = new Button("📑 Tam Rapor");
        fullButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;");
        fullButton.setOnAction(e -> generateReport(ReportGenerator.ReportType.FULL));

        buttonBox.getChildren().addAll(occupancyButton, revenueButton, reservationButton, fullButton);

        // Durum göstergesi
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Hazır");
        statusLabel.setFont(Font.font("Arial", 12));

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setVisible(false);

        statusBox.getChildren().addAll(statusLabel, progressBar);

        // Rapor alanı
        reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setFont(Font.font("Consolas", 12));
        reportArea.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00;");

        pane.getChildren().addAll(titleLabel, buttonBox, statusBox, reportArea);
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        return pane;
    }

    private void generateReport(ReportGenerator.ReportType type) {
        statusLabel.setText("Rapor hazırlanıyor...");
        progressBar.setVisible(true);
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        reportArea.setText("Lütfen bekleyin...\n\nRapor oluşturuluyor...");

        // Ayrı thread'de rapor oluştur (Senaryo 2)
        ReportGenerator generator = new ReportGenerator(
                MainApp.getFlightManager(),
                MainApp.getReservationManager(),
                type);

        generator.onReportComplete(result -> {
            // GUI thread'inde güncelle
            Platform.runLater(() -> {
                reportArea.setText(result);
                statusLabel.setText("Rapor hazır!");
                progressBar.setVisible(false);
            });
        });

        // Thread başlat
        Thread reportThread = new Thread(generator);
        reportThread.setDaemon(true);
        reportThread.start();
    }

    private VBox createSimulationPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Eşzamanlı Koltuk Rezervasyonu Simülasyonu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label descLabel = new Label("Bu simülasyon, 90 yolcunun aynı anda 180 koltuğa\n" +
                "rezervasyon yapmaya çalıştığı senaryoyu gösterir.");

        // Simülasyon kontrolleri
        HBox controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox syncCheckBox = new CheckBox("Synchronized (Thread-Safe)");
        syncCheckBox.setSelected(true);

        Button startButton = new Button("▶ Simülasyonu Başlat");
        startButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 14px;");

        controlBox.getChildren().addAll(syncCheckBox, startButton);

        // Simülasyon paneli
        SeatSimulationPanel simulationPanel = new SeatSimulationPanel();

        startButton.setOnAction(e -> {
            simulationPanel.runSimulation(syncCheckBox.isSelected());
        });

        pane.getChildren().addAll(titleLabel, descLabel, controlBox, simulationPanel);
        VBox.setVgrow(simulationPanel, Priority.ALWAYS);

        return pane;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
