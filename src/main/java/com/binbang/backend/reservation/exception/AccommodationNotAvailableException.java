package com.binbang.backend.reservation.exception;

import com.binbang.backend.global.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class AccommodationNotAvailableException extends CustomException {
    public AccommodationNotAvailableException(Long accommodationId, LocalDate checkIn, LocalDate checkOut){
        super(HttpStatus.CONFLICT,
                String.format("해당 기간(%s ~ %s)에 숙소(ID: %d)를 예약할 수 없습니다",
                        checkIn, checkOut, accommodationId));
    }

    public AccommodationNotAvailableException(){
        super(HttpStatus.CONFLICT,
                "해당 기간에 숙소를 예약할 수 없습니다");
    }
}
