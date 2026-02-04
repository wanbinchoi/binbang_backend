package com.binbang.backend.global.service;

import com.binbang.backend.reservation.entity.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * 호스트에게 새 예약 알림 이메일 발송
     *
     * @param reservation 예약 정보
     */
    @Async
    public void sendNewReservationNotification(Reservation reservation){
        try{
            String hostEmail = reservation.getAccommodation().getMember().getEmail();
            String subject = "[binbang] 새로운 예약이 접수되었습니다 - "+reservation.getAccommodation().getName();
            String content = buildNewReservationEmailContent(reservation);

            sendEmail(hostEmail,subject,content);

            log.info("예약 알람 이메일 발송 완료 : host email = {}",hostEmail);
        } catch (Exception e) {
            log.info("예약 알람 이메일 발송 실패 : error = {}",e.getMessage());
        }
    }

    /**
     * 게스트에게 예약 확인 이메일 발송
     *
     * @param reservation 예약 정보
     */
    @Async
    public void sendReservationConfirmation(Reservation reservation) {
        try {
            String guestEmail = reservation.getMember().getEmail();
            String subject = "[빈방] 예약이 완료되었습니다 - " + reservation.getAccommodation().getName();
            String content = buildReservationConfirmationEmailContent(reservation);

            sendEmail(guestEmail, subject, content);

            log.info("예약 확인 이메일 발송 완료: guestEmail={}, reservationId={}",
                    guestEmail, reservation.getReservationId());
        } catch (Exception e) {
            log.error("예약 확인 이메일 발송 실패: reservationId={}, error={}",
                    reservation.getReservationId(), e.getMessage(), e);
        }
    }

    /**
     * 예약 취소 알림 이메일 발송
     *
     * @param reservation 예약 정보
     */
    @Async
    public void sendCancellationNotification(Reservation reservation) {
        try {
            // 호스트에게 발송
            String hostEmail = reservation.getAccommodation().getMember().getEmail();
            String hostSubject = "[빈방] 예약이 취소되었습니다 - " + reservation.getAccommodation().getName();
            String hostContent = buildCancellationEmailContent(reservation, true);
            sendEmail(hostEmail, hostSubject, hostContent);

            // 게스트에게 발송
            String guestEmail = reservation.getMember().getEmail();
            String guestSubject = "[빈방] 예약 취소가 완료되었습니다 - " + reservation.getAccommodation().getName();
            String guestContent = buildCancellationEmailContent(reservation, false);
            sendEmail(guestEmail, guestSubject, guestContent);

            log.info("예약 취소 이메일 발송 완료: reservationId={}", reservation.getReservationId());
        } catch (Exception e) {
            log.error("예약 취소 이메일 발송 실패: reservationId={}, error={}",
                    reservation.getReservationId(), e.getMessage(), e);
        }
    }

    /**
     * 실제 이메일 발송 메소드
     */
    private void sendEmail(String to, String subject, String content) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setFrom("info@binbang.com");

        mailSender.send(message);
    }

    /**
     * 호스트용 새 예약 알림 이메일 본문 생성
     */
    private String buildNewReservationEmailContent(Reservation reservation){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");

        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Malgun Gothic', sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px; }
                        .content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; border-radius: 5px; }
                        .info-row { margin: 10px 0; padding: 10px; background-color: white; border-radius: 3px; }
                        .label { font-weight: bold; color: #555; }
                        .value { color: #333; margin-left: 10px; }
                        .footer { margin-top: 20px; text-align: center; color: #777; font-size: 12px; }
                        .highlight { color: #4CAF50; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>🎉 새로운 예약이 접수되었습니다!</h2>
                        </div>
                        
                        <div class="content">
                            <p>안녕하세요, <strong>%s</strong>님!</p>
                            <p>'<strong>%s</strong>'에 새로운 예약이 접수되었습니다.</p>
                            
                            <h3>📋 예약 정보</h3>
                            
                            <div class="info-row">
                                <span class="label">예약자:</span>
                                <span class="value">%s (%s)</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">전화번호:</span>
                                <span class="value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">체크인:</span>
                                <span class="value">%s %s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">체크아웃:</span>
                                <span class="value">%s %s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">투숙 인원:</span>
                                <span class="value">%d명</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">숙박 일수:</span>
                                <span class="value">%d박</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">총 금액:</span>
                                <span class="value highlight">%,d원</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">예약 시각:</span>
                                <span class="value">%s</span>
                            </div>
                        </div>
                        
                        <div class="footer">
                            <p>감사합니다.</p>
                            <p><strong>빈방</strong> 팀 드림</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                // 호스트 정보
                reservation.getAccommodation().getMember().getName(),
                reservation.getAccommodation().getName(),

                // 예약자 정보
                reservation.getMember().getName(),
                reservation.getMember().getEmail(),
                reservation.getMember().getPhone() != null ? reservation.getMember().getPhone() : "정보 없음",

                // 예약 날짜
                reservation.getCheckInDate().format(dateFormatter),
                reservation.getAccommodation().getCheckInTime().format(timeFormatter),
                reservation.getCheckOutDate().format(dateFormatter),
                reservation.getAccommodation().getCheckOutTime().format(timeFormatter),

                // 인원 및 일수
                reservation.getPersonnel(),
                java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate()),

                // 금액 및 예약 시각
                reservation.getTotalPrice(),
                reservation.getReservedAt().format(datetimeFormatter)
        );
    }

    /**
     * 게스트용 예약 확인 이메일 본문 생성
     */
    private String buildReservationConfirmationEmailContent(Reservation reservation) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Malgun Gothic', sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; border-radius: 5px; }
                        .content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; border-radius: 5px; }
                        .info-row { margin: 10px 0; padding: 10px; background-color: white; border-radius: 3px; }
                        .label { font-weight: bold; color: #555; }
                        .value { color: #333; margin-left: 10px; }
                        .footer { margin-top: 20px; text-align: center; color: #777; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>✅ 예약이 완료되었습니다!</h2>
                        </div>
                        
                        <div class="content">
                            <p>안녕하세요, <strong>%s</strong>님!</p>
                            <p>예약이 성공적으로 완료되었습니다.</p>
                            
                            <h3>🏠 숙소 정보</h3>
                            
                            <div class="info-row">
                                <span class="label">숙소명:</span>
                                <span class="value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">주소:</span>
                                <span class="value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">체크인:</span>
                                <span class="value">%s %s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">체크아웃:</span>
                                <span class="value">%s %s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">총 금액:</span>
                                <span class="value">%,d원</span>
                            </div>
                            
                            <h3>📞 호스트 연락처</h3>
                            
                            <div class="info-row">
                                <span class="label">호스트:</span>
                                <span class="value">%s</span>
                            </div>
                            
                            <div class="info-row">
                                <span class="label">전화번호:</span>
                                <span class="value">%s</span>
                            </div>
                        </div>
                        
                        <div class="footer">
                            <p>즐거운 여행 되세요!</p>
                            <p><strong>빈방</strong> 팀 드림</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                // 게스트 정보
                reservation.getMember().getName(),

                // 숙소 정보
                reservation.getAccommodation().getName(),
                reservation.getAccommodation().getAddress(),

                // 날짜
                reservation.getCheckInDate().format(dateFormatter),
                reservation.getAccommodation().getCheckInTime().format(timeFormatter),
                reservation.getCheckOutDate().format(dateFormatter),
                reservation.getAccommodation().getCheckOutTime().format(timeFormatter),

                // 금액
                reservation.getTotalPrice(),

                // 호스트 정보
                reservation.getAccommodation().getMember().getName(),
                reservation.getAccommodation().getMember().getPhone() != null ?
                        reservation.getAccommodation().getMember().getPhone() : "정보 없음"
        );
    }

    /**
     * 예약 취소 이메일 본문 생성
     */
    private String buildCancellationEmailContent(Reservation reservation, boolean isForHost) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        if (isForHost) {
            // 호스트용
            return String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body { font-family: 'Malgun Gothic', sans-serif; line-height: 1.6; color: #333; }
                            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                            .header { background-color: #F44336; color: white; padding: 20px; text-align: center; border-radius: 5px; }
                            .content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; border-radius: 5px; }
                            .info-row { margin: 10px 0; padding: 10px; background-color: white; border-radius: 3px; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h2>❌ 예약이 취소되었습니다</h2>
                            </div>
                            
                            <div class="content">
                                <p>안녕하세요, <strong>%s</strong>님!</p>
                                <p>'<strong>%s</strong>'의 예약이 취소되었습니다.</p>
                                
                                <div class="info-row">
                                    취소된 기간: %s ~ %s
                                </div>
                                <div class="info-row">
                                    예약자: %s
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """,
                    reservation.getAccommodation().getMember().getName(),
                    reservation.getAccommodation().getName(),
                    reservation.getCheckInDate().format(dateFormatter),
                    reservation.getCheckOutDate().format(dateFormatter),
                    reservation.getMember().getName()
            );
        } else {
            // 게스트용
            return String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body { font-family: 'Malgun Gothic', sans-serif; line-height: 1.6; color: #333; }
                            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                            .header { background-color: #F44336; color: white; padding: 20px; text-align: center; border-radius: 5px; }
                            .content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; border-radius: 5px; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h2>✅ 예약 취소가 완료되었습니다</h2>
                            </div>
                            
                            <div class="content">
                                <p>안녕하세요, <strong>%s</strong>님!</p>
                                <p>'<strong>%s</strong>'의 예약 취소가 완료되었습니다.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """,
                    reservation.getMember().getName(),
                    reservation.getAccommodation().getName()
            );
        }
    }

}
