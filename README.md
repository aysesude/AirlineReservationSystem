# ✈️ Havayolu Rezervasyon ve Yönetim Sistemi

**BLM2012 Object Oriented Programming - 2025-2026 Güz Dönemi Projesi**

## 📋 Proje Hakkında

Bu proje, Java programlama dili kullanılarak geliştirilmiş kapsamlı bir Havayolu Rezervasyon ve Yönetim Sistemidir. Proje, OOP prensiplerini (Encapsulation, Inheritance, Polymorphism, Abstraction), Multithreading ve JUnit testlerini içermektedir.

## 🛠️ Gereksinimler

- **JDK 17** veya üzeri
- **JavaFX 21** (lib klasöründe mevcut)

## 🚀 Kurulum ve Çalıştırma

### Terminal ile (Önerilen)

```bash
# Projeyi derle
./compile.sh

# Uygulamayı çalıştır
./run.sh
```

### VS Code ile

1. **Extension Pack for Java** eklentisini yükleyin
2. Projeyi VS Code'da açın: `File` → `Open Folder`
3. `F5` tuşuna basın veya `Run` → `Start Debugging`

### IntelliJ IDEA ile

1. `File` → `Open` → Proje klasörünü seçin
2. JavaFX SDK'yı `lib` klasöründen ayarlayın
3. `MainApp.java` → Sağ tık → `Run`

## 🔐 Giriş Bilgileri

| Kullanıcı | Şifre | Rol |
|-----------|-------|-----|
| admin | admin123 | Yönetici |

Yeni kullanıcı kaydı giriş ekranından yapılabilir.

## 📁 Proje Yapısı

```
AirlineReservationSystem/
├── src/
│   ├── module-info.java
│   └── com/airline/
│       ├── MainApp.java              # Ana uygulama
│       ├── Launcher.java             # JAR için launcher
│       ├── model/                    # Model sınıfları
│       │   ├── enums/               # Enum tipleri
│       │   │   ├── SeatClass.java
│       │   │   ├── FlightStatus.java
│       │   │   ├── ReservationStatus.java
│       │   │   ├── TicketStatus.java
│       │   │   └── UserRole.java
│       │   ├── Seat.java
│       │   ├── Plane.java
│       │   ├── Route.java
│       │   ├── Flight.java
│       │   ├── Passenger.java
│       │   ├── Reservation.java
│       │   ├── Ticket.java
│       │   ├── Baggage.java
│       │   ├── User.java            # Abstract sınıf
│       │   ├── Customer.java
│       │   ├── Staff.java
│       │   └── Admin.java
│       ├── manager/                  # Yönetici sınıfları
│       │   ├── FlightManager.java
│       │   ├── SeatManager.java
│       │   ├── ReservationManager.java
│       │   └── UserManager.java
│       ├── service/                  # Servis sınıfları
│       │   ├── PriceCalculator.java
│       │   ├── FlightSearchEngine.java
│       │   └── ReportGenerator.java
│       ├── util/
│       │   └── FileManager.java
│       ├── gui/                      # JavaFX GUI
│       │   ├── LoginScreen.java
│       │   ├── CustomerDashboard.java
│       │   ├── AdminDashboard.java
│       │   └── SeatSimulationPanel.java
│       └── test/                     # JUnit Testleri
│           ├── PriceCalculatorTest.java
│           ├── FlightSearchEngineTest.java
│           └── SeatManagerTest.java
├── lib/                              # JavaFX ve JUnit kütüphaneleri
├── compile.sh                        # Derleme scripti
├── run.sh                            # Çalıştırma scripti
└── README.md
```

## ✅ Proje Gereksinimleri

### OOP Prensipleri
- ✅ **Encapsulation**: Tüm sınıflarda private alanlar ve getter/setter metodları
- ✅ **Inheritance**: User → Customer, Staff → Admin kalıtım hiyerarşisi
- ✅ **Polymorphism**: `getPermissions()` metodu her sınıfta farklı davranış
- ✅ **Abstraction**: `User` abstract sınıfı

### Multithreading
- ✅ **Senaryo 1**: Eşzamanlı koltuk rezervasyonu (synchronized vs unsynchronized karşılaştırma)
- ✅ **Senaryo 2**: Asenkron rapor oluşturma (ReportGenerator - Runnable)

### JUnit 5 Testleri
- ✅ **PriceCalculatorTest**: Fiyat hesaplama testleri
- ✅ **FlightSearchEngineTest**: Uçuş arama testleri
- ✅ **SeatManagerTest**: Koltuk yönetimi testleri

### GUI Ekranları
- ✅ **Login Screen**: Kullanıcı girişi ve kayıt
- ✅ **Customer Dashboard**: Uçuş arama, rezervasyon yapma
- ✅ **Admin Dashboard**: Uçuş yönetimi, raporlar, simülasyon

### Dosya İşlemleri
- ✅ **FileManager**: Serialization ile veri kaydetme/yükleme

## 📊 Modüller

### 1. Flight Management Module
- `Plane`: Uçak bilgileri ve koltuk matrisi
- `Flight`: Uçuş detayları
- `Seat`: Koltuk bilgileri
- `Route`: Rota bilgileri

### 2. Reservation & Ticketing Module
- `Passenger`: Yolcu bilgileri
- `Reservation`: Rezervasyon detayları
- `Ticket`: Bilet bilgileri
- `Baggage`: Bagaj bilgileri

### 3. Services & Managers
- `FlightManager`: Uçuş CRUD işlemleri
- `SeatManager`: Koltuk yönetimi
- `ReservationManager`: Rezervasyon yönetimi (thread-safe)
- `PriceCalculator`: Fiyat hesaplama
- `FlightSearchEngine`: Uçuş arama
- `ReportGenerator`: Rapor oluşturma (async)

## 🧪 Testleri Çalıştırma

Testler `src/com/airline/test/` klasöründe bulunmaktadır. IDE üzerinden veya JUnit test runner ile çalıştırabilirsiniz.

## 📦 Derleme

```bash
# Projeyi derle
./compile.sh
```

Derlenmiş .class dosyaları `out/` klasöründe oluşturulacaktır.

## 👥 Geliştirici

- **Grup No**: 3
- **Öğrenciler**: Ayşe Sude Cami, Zeynep Feryat Gözüngül

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.
