package com.example.SkyNest.model.entity.flight;

import jakarta.persistence.*;

@Entity
@Table(name = "airport_card")
public class AirportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalBalance;

    @OneToOne
    @JoinColumn(name = "airport_id",referencedColumnName = "id")
    private Airport airport;

    public AirportCard(){

    }
    public AirportCard(double totalBalance,Airport airport){
        this.totalBalance = totalBalance;

        this.airport = airport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }
}
