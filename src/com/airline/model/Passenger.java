package com.airline.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Bir yolcuyu temsil eder.
 * Kişisel bilgiler ve iletişim bilgilerini içerir.
 */
public class Passenger implements Serializable {
    private static final long serialVersionUID = 1L;

    private String passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passportNo;
    private String tcNo; // TC Kimlik No (opsiyonel)

    /**
     * Yeni bir yolcu oluşturur.
     */
    public Passenger(String firstName, String lastName, String email, String phone) {
        this.passengerId = generatePassengerId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    /**
     * ID ile birlikte yolcu oluşturur.
     */
    public Passenger(String passengerId, String firstName, String lastName, 
                     String email, String phone) {
        this.passengerId = passengerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Tüm bilgilerle yolcu oluşturur.
     */
    public Passenger(String firstName, String lastName, String email, 
                     String phone, String passportNo, String tcNo) {
        this.passengerId = generatePassengerId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passportNo = passportNo;
        this.tcNo = tcNo;
    }

    private String generatePassengerId() {
        return "PSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Yolcunun tam adını döndürür.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * İletişim bilgilerini formatlanmış olarak döndürür.
     */
    public String getContactInfo() {
        return String.format("Email: %s, Tel: %s", email, phone);
    }

    // Getter ve Setter metodları
    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getTcNo() {
        return tcNo;
    }

    public void setTcNo(String tcNo) {
        this.tcNo = tcNo;
    }

    @Override
    public String toString() {
        return String.format("Passenger{id='%s', name='%s %s', email='%s'}",
                passengerId, firstName, lastName, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return passengerId != null && passengerId.equals(passenger.passengerId);
    }

    @Override
    public int hashCode() {
        return passengerId != null ? passengerId.hashCode() : 0;
    }
}
