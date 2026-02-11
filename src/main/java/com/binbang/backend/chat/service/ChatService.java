package com.binbang.backend.chat.service;

import com.binbang.backend.chat.repository.ChatMessageRepository;
import com.binbang.backend.chat.repository.ChatRoomRepository;
import com.binbang.backend.global.service.WebSocketService;
import com.binbang.backend.member.repository.MemberRepository;
import com.binbang.backend.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 채팅 서비스
 *
 * 역할:
 * - 채팅방 생성/조회
 * - 메시지 전송/조회
 * - 읽음 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final WebSocketService webSocketService;

    

}
