package com.example.SkyNest.model.entity.hotel;

import com.example.SkyNest.model.entity.userDetails.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double longitude;
    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false,name = "avgRating")
    private double avgRating;

    @Column(nullable = false)
    private int ratingCount;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private HotelCard hotelCard;
    @OneToMany(mappedBy = "hotel")
    private List<HotelImage> hotelImageList;

    @OneToMany(mappedBy = "hotel" ,cascade = CascadeType.REMOVE)
    private List<PlaceNearTheHotel> placeNearTheHotelList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<HotelImage> getHotelImageList() {
        return hotelImageList;
    }

    public void setHotelImageList(List<HotelImage> hotelImageList) {
        this.hotelImageList = hotelImageList;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<PlaceNearTheHotel> getPlaceNearTheHotelList() {
        return placeNearTheHotelList;
    }

    public void setPlaceNearTheHotelList(List<PlaceNearTheHotel> placeNearTheHotelList) {
        this.placeNearTheHotelList = placeNearTheHotelList;
    }

    public HotelCard getHotelCard() {
        return hotelCard;
    }

    public void setHotelCard(HotelCard hotelCard) {
        this.hotelCard = hotelCard;
    }
}
