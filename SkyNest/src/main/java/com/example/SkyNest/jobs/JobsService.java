package com.example.SkyNest.jobs;

import com.example.SkyNest.firebase.service.FirebaseService;
import com.example.SkyNest.model.entity.hotel.HotelBooking;
import com.example.SkyNest.model.entity.hotel.Room;
import com.example.SkyNest.model.entity.userDetails.Notification;
import com.example.SkyNest.model.repository.hotel.HotelBookingRepository;
import com.example.SkyNest.model.repository.hotel.RoomRepository;
import com.example.SkyNest.model.repository.userDetails.NotificationRepo;
import com.example.SkyNest.myEnum.RoomStatus;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class JobsService {

    private final FirebaseService firebaseService;
    private final HotelBookingRepository hotelBookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationRepo notificationRepo;

    public JobsService(FirebaseService firebaseService,
    HotelBookingRepository hotelBookingRepository,
    RoomRepository roomRepository,
    NotificationRepo notificationRepo ){
        this.firebaseService = firebaseService;
        this.hotelBookingRepository = hotelBookingRepository;
        this.roomRepository = roomRepository;
        this.notificationRepo = notificationRepo;
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
            if (booking.getUser().getFcmToken()==null||booking.getUser().getFcmToken().isBlank()){
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

    @Scheduled(cron = "0 15 0 * * *",zone = "Asia/Damascus")
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

}
