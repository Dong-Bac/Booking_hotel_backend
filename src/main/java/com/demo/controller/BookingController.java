package com.demo.controller;


import com.demo.exception.InvalidBookingRequestException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.BookedRoom;
import com.demo.model.Room;
import com.demo.response.BookingResponse;
import com.demo.response.RoomResponse;
import com.demo.service.BookingService;
import com.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private RoomService roomService;


    @GetMapping("/all-bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookedRoom> bookedRooms=bookingService.getAllBookings();
        List<BookingResponse> bookingResponses=new ArrayList<>();
        for( BookedRoom bookedRoom : bookedRooms){
            BookingResponse bookingResponse=getBookingResponse(bookedRoom);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(
            @PathVariable Long roomId,
            @RequestBody BookedRoom bookingRequest
    ){
        try {
            String confirmationCode=bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully, Your booking confirmation code is: "+confirmationCode);
        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookedRoom booking= bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse=getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email){
        List<BookedRoom> bookedRooms=bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> bookingResponses=new ArrayList<>();
        for(BookedRoom booking: bookedRooms){
            BookingResponse bookingResponse=getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }
//    @GetMapping("/user/{userId}/bookings")
//    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable Long userId){
//        List<BookedRoom> bookedRooms=bookingService.getBookingsByUserId(userId);
//        List<BookingResponse> bookingResponses=new ArrayList<>();
//        for(BookedRoom booking: bookedRooms){
//            BookingResponse bookingResponse=getBookingResponse(booking);
//            bookingResponses.add(bookingResponse);
//        }
//        return ResponseEntity.ok(bookingResponses);
//    }

    @DeleteMapping("/booking/{bookingId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking){
        Room theroom=roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room=new RoomResponse(
                theroom.getId(),
                theroom.getRoomType(),
                theroom.getRoomPrice()
        );
        return new BookingResponse(
                booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getGuestFullName(),
                booking.getGuestEmail(), booking.getNumOfAdults(),
                booking.getNumOfChildren(), booking.getTotalNumofGuest(),
                booking.getBookingConfirmationCode(), room
        );
    }


}
