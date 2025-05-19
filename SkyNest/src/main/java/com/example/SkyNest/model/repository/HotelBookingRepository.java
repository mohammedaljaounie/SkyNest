package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.HotelBooking;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking,Long> {
}
