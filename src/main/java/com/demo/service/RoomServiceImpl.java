package com.demo.service;

import com.demo.exception.InternalServerException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.Room;
import com.demo.repository.RoomRepository;
import com.zaxxer.hikari.util.FastList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws SQLException, IOException {
        Room room=new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if (!file.isEmpty()){
            byte[] photoBytes=file.getBytes();//chuyển  nội dung của "MultipartFile" thành một mảng byte;
            Blob photoBlob= new SerialBlob(photoBytes);//lưu trữ dữ liệu nhị phân trong cơ sở dữ liệu
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes(){
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRoom() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> room=roomRepository.findById(roomId);
        if(room.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room not found");
        }

        Blob photoBlod=room.get().getPhoto();
        if(photoBlod!=null){
            return photoBlod.getBytes(1,(int) photoBlod.length());
        }

        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {

        Optional<Room> room=roomRepository.findById(roomId);
        if(room.isPresent()){
            roomRepository.delete(room.get());
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        Room room=roomRepository.findById(roomId).get();

        if(roomType!=null) room.setRoomType(roomType);
        if(roomPrice!=null) room.setRoomPrice(roomPrice);
        if (photoBytes!=null && photoBytes.length>0){
            try{
                room.setPhoto(new SerialBlob(photoBytes));
            } catch (SerialException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new InternalServerException("Fail updating room");
            }
        }
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }
}
