package com.airline.manager;

import com.airline.model.Flight;
import com.airline.model.Plane;
import com.airline.model.Route;
import com.airline.util.FileManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Uçuş yönetimi işlemlerini gerçekleştirir.
 * Uçuş oluşturma, güncelleme, silme ve arama işlemleri yapar.
 */
public class FlightManager {

    private static final String FLIGHTS_FILE = "flights.dat";
    private static final String PLANES_FILE = "planes.dat";
    private static final String ROUTES_FILE = "routes.dat";

    private List<Flight> flights;
    private List<Plane> planes;
    private List<Route> routes;

    /**
     * FlightManager oluşturur ve verileri dosyadan yükler.
     */
    public FlightManager() {
        this.flights = new ArrayList<>();
        this.planes = new ArrayList<>();
        this.routes = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Yeni bir uçuş oluşturur.
     */
    public Flight createFlight(String flightNum, Route route, LocalDate date,
                               LocalTime hour, int duration, Plane plane) {
        // Aynı numarada uçuş var mı kontrol et
        if (getFlightByNumber(flightNum) != null) {
            throw new IllegalArgumentException("Bu uçuş numarası zaten mevcut: " + flightNum);
        }

        Flight flight = new Flight(flightNum, route, date, hour, duration, plane);
        flights.add(flight);
        
        // Uçak ve rotayı da listeye ekle (eğer yoksa)
        if (!planes.contains(plane)) {
            planes.add(plane);
        }
        if (route != null && !routes.contains(route)) {
            routes.add(route);
        }
        
        saveToFile();
        return flight;
    }

    /**
     * Basit uçuş oluşturma (rota olmadan).
     */
    public Flight createFlight(String flightNum, String departure, String arrival,
                               LocalDate date, LocalTime hour, int duration, Plane plane) {
        Route route = new Route(departure, departure.substring(0, 3).toUpperCase(),
                               arrival, arrival.substring(0, 3).toUpperCase());
        return createFlight(flightNum, route, date, hour, duration, plane);
    }

    /**
     * Uçuşu günceller.
     */
    public boolean updateFlight(Flight flight) {
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getFlightNum().equals(flight.getFlightNum())) {
                flights.set(i, flight);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    /**
     * Uçuşu siler.
     */
    public boolean deleteFlight(String flightNum) {
        boolean removed = flights.removeIf(f -> f.getFlightNum().equals(flightNum));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * Uçuş numarasına göre uçuş arar.
     */
    public Flight getFlightByNumber(String flightNum) {
        for (Flight flight : flights) {
            if (flight.getFlightNum().equalsIgnoreCase(flightNum)) {
                return flight;
            }
        }
        return null;
    }

    /**
     * Tüm uçuşları döndürür.
     */
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    /**
     * Aktif (henüz kalkmamış) uçuşları döndürür.
     */
    public List<Flight> getActiveFlights() {
        return flights.stream()
                .filter(f -> !f.isExpired())
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir tarihteki uçuşları döndürür.
     */
    public List<Flight> getFlightsByDate(LocalDate date) {
        return flights.stream()
                .filter(f -> f.getDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Kalkış yerine göre uçuşları filtreler.
     */
    public List<Flight> getFlightsByDeparture(String departure) {
        return flights.stream()
                .filter(f -> f.getDeparturePlace().equalsIgnoreCase(departure))
                .collect(Collectors.toList());
    }

    /**
     * Varış yerine göre uçuşları filtreler.
     */
    public List<Flight> getFlightsByArrival(String arrival) {
        return flights.stream()
                .filter(f -> f.getArrivalPlace().equalsIgnoreCase(arrival))
                .collect(Collectors.toList());
    }

    /**
     * Yeni uçak ekler.
     */
    public void addPlane(Plane plane) {
        if (!planes.contains(plane)) {
            planes.add(plane);
            saveToFile();
        }
    }

    /**
     * Tüm uçakları döndürür.
     */
    public List<Plane> getAllPlanes() {
        return new ArrayList<>(planes);
    }

    /**
     * Uçak ID'sine göre uçak arar.
     */
    public Plane getPlaneById(String planeId) {
        for (Plane plane : planes) {
            if (plane.getPlaneId().equals(planeId)) {
                return plane;
            }
        }
        return null;
    }

    /**
     * Yeni rota ekler.
     */
    public void addRoute(Route route) {
        if (!routes.contains(route)) {
            routes.add(route);
            saveToFile();
        }
    }

    /**
     * Tüm rotaları döndürür.
     */
    public List<Route> getAllRoutes() {
        return new ArrayList<>(routes);
    }

    /**
     * Verileri dosyaya kaydeder.
     */
    public void saveToFile() {
        FileManager.saveList(flights, FLIGHTS_FILE);
        FileManager.saveList(planes, PLANES_FILE);
        FileManager.saveList(routes, ROUTES_FILE);
    }

    /**
     * Verileri dosyadan yükler.
     */
    public void loadFromFile() {
        List<Flight> loadedFlights = FileManager.loadList(FLIGHTS_FILE);
        List<Plane> loadedPlanes = FileManager.loadList(PLANES_FILE);
        List<Route> loadedRoutes = FileManager.loadList(ROUTES_FILE);

        if (loadedFlights != null && !loadedFlights.isEmpty()) {
            this.flights = loadedFlights;
        }
        if (loadedPlanes != null && !loadedPlanes.isEmpty()) {
            this.planes = loadedPlanes;
        }
        if (loadedRoutes != null && !loadedRoutes.isEmpty()) {
            this.routes = loadedRoutes;
        }
    }

    /**
     * Uçuş sayısını döndürür.
     */
    public int getFlightCount() {
        return flights.size();
    }

    /**
     * Bugünkü uçuşları döndürür.
     */
    public List<Flight> getTodaysFlights() {
        return getFlightsByDate(LocalDate.now());
    }

    /**
     * Örnek veriler oluşturur (test için).
     */
    public void createSampleData() {
        // Örnek uçaklar
        Plane plane1 = new Plane("TC-001", "Boeing 737-800", 500);
        Plane plane2 = new Plane("TC-002", "Airbus A320", 450);
        Plane plane3 = new Plane("TC-003", "Boeing 777", 600);

        planes.add(plane1);
        planes.add(plane2);
        planes.add(plane3);

        // Örnek rotalar
        Route route1 = new Route("İstanbul", "İstanbul Havalimanı", "IST",
                                 "Ankara", "Esenboğa Havalimanı", "ESB");
        Route route2 = new Route("İstanbul", "İstanbul Havalimanı", "IST",
                                 "İzmir", "Adnan Menderes Havalimanı", "ADB");
        Route route3 = new Route("Ankara", "Esenboğa Havalimanı", "ESB",
                                 "Antalya", "Antalya Havalimanı", "AYT");
        Route route4 = new Route("İstanbul", "İstanbul Havalimanı", "IST",
                                 "Antalya", "Antalya Havalimanı", "AYT");
        Route route5 = new Route("İzmir", "Adnan Menderes Havalimanı", "ADB",
                                 "Trabzon", "Trabzon Havalimanı", "TZX");

        routes.add(route1);
        routes.add(route2);
        routes.add(route3);
        routes.add(route4);
        routes.add(route5);

        // Örnek uçuşlar (bugün ve yarın için)
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusDays(7);

        // Bugünkü uçuşlar
        flights.add(new Flight("TK101", route1, today, LocalTime.of(8, 30), 60, plane1));
        flights.add(new Flight("TK102", route2, today, LocalTime.of(10, 0), 75, plane2));
        flights.add(new Flight("TK103", route4, today, LocalTime.of(14, 30), 90, plane1));

        // Yarınki uçuşlar
        flights.add(new Flight("TK201", route1, tomorrow, LocalTime.of(7, 0), 60, plane1));
        flights.add(new Flight("TK202", route3, tomorrow, LocalTime.of(9, 30), 75, plane2));
        flights.add(new Flight("TK203", route2, tomorrow, LocalTime.of(12, 0), 75, plane3));
        flights.add(new Flight("TK204", route5, tomorrow, LocalTime.of(16, 0), 105, plane1));

        // Gelecek hafta uçuşları
        flights.add(new Flight("TK301", route1, nextWeek, LocalTime.of(6, 0), 60, plane1));
        flights.add(new Flight("TK302", route4, nextWeek, LocalTime.of(11, 0), 90, plane2));
        flights.add(new Flight("TK303", route2, nextWeek, LocalTime.of(15, 30), 75, plane3));

        saveToFile();
    }

    /**
     * Tüm verileri temizler.
     */
    public void clearAllData() {
        flights.clear();
        planes.clear();
        routes.clear();
        saveToFile();
    }
}
