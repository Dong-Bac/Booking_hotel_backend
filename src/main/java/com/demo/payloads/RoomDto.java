package com.demo.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RoomDto {
    private Long id;
    private String roomName;
    private String roomType;
    private BigDecimal roomPrice;
    private Boolean isBooked=false;
    private Blob photo;
    private List<BookedRoomDto> bookings;
}
