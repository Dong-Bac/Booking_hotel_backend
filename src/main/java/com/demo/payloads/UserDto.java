package com.demo.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String homeTown;
    private String age;
    private String phone;
    private String email;
    private String password;
    private List<BookedRoomDto> bookings;
    private Collection<RoleDto> roles=new HashSet<>();

}
