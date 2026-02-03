package com.binbang.backend.reservation.exception;

import com.binbang.backend.global.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class InvalidReservationDateException extends CustomException {
    public InvalidReservationDateException(LocalDate checkIn, LocalDate checkOut){
        super(HttpStatus.CONFLICT,
                String.format("기존 예약과 중복되는 예약입니다. 체크인: %s, 체크아웃 :%s",checkIn,checkOut));
    }

    public InvalidReservationDateException(LocalDate checkIn){
        super(HttpStatus.CONFLICT,
                String.format("과거 날짜로 체크인할 수 없습니다. 체크인: %s",checkIn));
    }
}
