package com.binbang.backend.chat.controller;

import com.binbang.backend.chat.dto.request.ChatMessageRequest;
import com.binbang.backend.chat.dto.response.ChatMessageResponse;
import com.binbang.backend.chat.dto.response.ChatRoomResponse;
import com.binbang.backend.chat.service.ChatService;
import com.binbang.backend.member.repository.MemberRepository;
import com.binbang.backend.reservation.repository.ReservationRepository;
import com.binbang.backend.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET  /api/chat/reservations/{reservationId}/room : 채팅방 생성/조회
 * - GET  /api/chat/rooms/{chatRoomId} : 채팅방 조회
 * - POST /api/chat/rooms/{chatRoomId}/messages : 메시지 전송
 * - GET  /api/chat/rooms/{chatRoomId}/messages : 메시지 목록 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    /**
     * 채팅방 생성 또는 조회 (예약 기반)
     * <p>
     * GET /api/chat/reservations/{reservationId}/room
     *
     * @param reservationId 예약 ID
     * @param email         인증된 사용자
     * @return 채팅방 정보
     */
    @GetMapping("/reservations/{reservationId}/room")
    public ResponseEntity<ChatRoomResponse> getOrCreateChatRoom(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal String email
    ) {
        log.info("채팅방 조회/생성 요청: reservationId={}, email={}", reservationId, email);

        // email로 memberId 조회 필요
        // 임시로 1L 사용 (실제로는 MemberService에서 조회)
        Long memberId = 1L;
        //Long memberId = memberRepository.findByEmail(email); // TODO: email로 memberId 조회

        ChatRoomResponse response = chatService.getOrCreateChatRoom(reservationId, memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 조회
     * <p>
     * GET /api/chat/rooms/{chatRoomId}
     *
     * @param chatRoomId 채팅방 ID
     * @param email      인증된 사용자
     * @return 채팅방 정보
     */
    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal String email
    ) {
        Long memberId = 1L; // TODO: email로 memberId 조회

        ChatRoomResponse response = chatService.getChatRoom(chatRoomId, memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 메시지 전송
     * <p>
     * POST /api/chat/rooms/{chatRoomId}/messages
     *
     * @param chatRoomId 채팅방 ID
     * @param request    메시지 내용
     * @param email      인증된 사용자
     * @return 저장된 메시지
     */
    @PostMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal String email
    ) {
        Long memberId = 1L; // TODO: email로 memberId 조회

        log.info("메시지 전송 요청: chatRoomId={}, email={}, content={}",
                chatRoomId, email, request.getContent());

        // request에 chatRoomId 설정
        request.setChatRoomId(chatRoomId);

        ChatMessageResponse response = chatService.sendMessage(chatRoomId, memberId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 메시지 목록 조회
     * <p>
     * GET /api/chat/rooms/{chatRoomId}/messages?page=0&size=50
     *
     * @param chatRoomId 채팅방 ID
     * @param page       페이지 번호 (기본값: 0)
     * @param size       페이지 크기 (기본값: 50)
     * @param email      인증된 사용자
     * @return 메시지 목록
     */
    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal String email
    ) {
        Long memberId = 1L; // TODO: email로 memberId 조회

        Pageable pageable = PageRequest.of(page, size);
        List<ChatMessageResponse> messages = chatService.getChatMessages(
                chatRoomId, memberId, pageable
        );

        return ResponseEntity.ok(messages);
    }
}