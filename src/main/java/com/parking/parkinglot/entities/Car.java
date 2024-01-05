package com.parking.parkinglot.entities;

import jakarta.persistence.*;

@Entity
public class Car {
    private User user;
    private Long id;

    String licensePlate;
    String parkingSpot;
    private CarPhoto photo;


    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licencePlate) {
        this.licensePlate = licencePlate;
    }

    public String getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(String parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    @OneToOne(mappedBy = "car",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public CarPhoto getPhoto() {
        return photo;
    }

    public void setPhoto(CarPhoto photo) {
        this.photo = photo;
    }
}
