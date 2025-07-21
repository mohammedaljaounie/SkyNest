package com.example.SkyNest.jobs;

import com.example.SkyNest.firebase.service.FirebaseService;
import com.example.SkyNest.model.entity.flight.Flight;
import com.example.SkyNest.model.entity.flight.FlightBooking;
import com.example.SkyNest.model.entity.hotel.HotelBooking;
import com.example.SkyNest.model.entity.hotel.Room;
import com.example.SkyNest.model.entity.userDetails.Notification;
import com.example.SkyNest.model.repository.flight.FlightBookingRepo;
import com.example.SkyNest.model.repository.flight.FlightRepo;
import com.example.SkyNest.model.repository.hotel.HotelBookingRepository;
import com.example.SkyNest.model.repository.hotel.RoomRepository;
import com.example.SkyNest.model.repository.userDetails.NotificationRepo;
import com.example.SkyNest.myEnum.RoomStatus;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobsService {

    private final FirebaseService firebaseService;
    private final HotelBookingRepository hotelBookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationRepo notificationRepo;
    private final FlightBookingRepo flightBookingRepo;

    public JobsService(FirebaseService firebaseService,
    HotelBookingRepository hotelBookingRepository,
    RoomRepository roomRepository,
    NotificationRepo notificationRepo,
                       FlightBookingRepo flightBookingRepo ){
        this.firebaseService = firebaseService;
        this.hotelBookingRepository = hotelBookingRepository;
        this.roomRepository = roomRepository;
        this.notificationRepo = notificationRepo;
        this.flightBookingRepo = flightBookingRepo;
    }

    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Damascus")
    @Transactional
    public void updateRoomStatusToEmpty() throws Exception {
        LocalDate currentDate = LocalDate.now().minusDays(1);
        List<HotelBooking> hotelBookings = this.hotelBookingRepository.bringBookingsThatWillStart(currentDate, StatusEnumForBooking.Activated);

        for (HotelBooking booking  : hotelBookings ){

            for (Room room : booking.getRooms()){
                room.setStatus(RoomStatus.EMPTY);
            }

            this.roomRepository.saveAll(booking.getRooms());
            if (booking.getUser().getFcmToken()==null){
                System.out.println("this user is not  have fcm token to send notification");
            }else {
                String title = "SkyNest";
                String body = "Hello, your reservation at " + booking.getHotel().getName() + " Hotel has ended today.\n" +
                        " We wish you a pleasant stay and thank you for using our application.";
                this.firebaseService.sendNotification(title,body
                        , booking.getUser().getFcmToken());
                this.notificationRepo.save(new Notification(title+"\n"+body, booking.getUser()));
            }

        }
    }

    @Scheduled(cron = "0 10 0 * * *",zone = "Asia/Damascus")
    @Transactional
    public void updateRoomStatusToFull() throws Exception {
        List<HotelBooking> hotelBookings = this.hotelBookingRepository.bringInBookingsThatWillExpire(LocalDate.now(), StatusEnumForBooking.Activated);
        for (HotelBooking booking : hotelBookings){
            System.out.println(hotelBookings.size());
            for (Room room : booking.getRooms()){

                room.setStatus(RoomStatus.BOOKING);
            }
            this.roomRepository.saveAll(booking.getRooms());
            if (booking.getUser().getFcmToken()==null||booking.getUser().getFcmToken().isBlank()){
                System.out.println("this user is not have fcm token to send notification");
            }else {
                String title = "SkyNest";
                String body = "Hello, your reservation at " + booking.getHotel().getName() + " Hotel has started today.\n" +
                        " We wish you a pleasant stay and thank you for using our application.";

                this.firebaseService.sendNotification(title, body, booking.getUser().getFcmToken());
                this.notificationRepo.save(new Notification(title+"\n"+body, booking.getUser()));
            }

        }

    }


    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Damascus")
    @Transactional
    public void sentNotificationToRememberStartFlight()throws Exception{
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime afterFourAndHalf = now.plusMinutes(270);
        LocalDateTime afterFive = now.plusHours(5);
        List<FlightBooking> flightList = this.flightBookingRepo.findFlightsWithinTimeWindow(afterFourAndHalf,afterFive,StatusEnumForBooking.Activated);
        for (FlightBooking flight : flightList){
            String fcmToken = flight.getUser().getFcmToken();
            if (fcmToken!=null){
                String body = "Hello "+flight.getUser().getFullName()+"\nYour flight from "+flight.getFlight().getAirport().getName()+"Airport is approaching.\n Pack your bags and hurry to the airport within half an hour to complete the procedures.";
                this.firebaseService.sendNotification("SkyNest",body,fcmToken);
                this.notificationRepo.save(new Notification("SkyNest\n"+body,flight.getUser()));
            }


        }

    }


    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Damascus")
    @Transactional
    public void sentNotificationToRememberArriveFlight()throws Exception{
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime afterFourAndHalf = now.plusMinutes(20);
        LocalDateTime afterFive = now.plusHours(15);
        List<FlightBooking> flightList = this.flightBookingRepo.findFlightsWithinTimeWindowArrived(afterFourAndHalf,afterFive,StatusEnumForBooking.Activated);
        for (FlightBooking flight : flightList){
            String fcmToken = flight.getUser().getFcmToken();
            if (fcmToken!=null){
                String body = "Hi " + flight.getUser().getFullName() +
                        ",\nWe hope you arrived safely at " + flight.getFlight().getAirport().getName() +
                        " Airport. We'd love your feedback on your experience ✈️.";
                this.firebaseService.sendNotification("SkyNest",body,fcmToken);
                this.notificationRepo.save(new Notification("SkyNest\n"+body,flight.getUser()));
            }


        }

    }



}
