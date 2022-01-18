package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.config.redispubsub.RedisPublisher;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomCarpoolInfoDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomListResponseDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomResponseDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final CarpoolRepository carpoolRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatUserInfoRepository chatUserInfoRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final RedisPublisher redisPublisher;

    //내가 참여한 모든 채팅방 목록 조회 메소드
    public List<ChatRoomListResponseDto> getAllRooms(User user) {
        Long userId = user.getId();

        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByUserId(userId);
        List<ChatRoomListResponseDto> chatRoomListResponseDtoList = new ArrayList<>();
        for (ChatUserInfo chatUserInfo : chatUserInfoList) {
            ChatRoom chatRoom = chatUserInfo.getChatRoom();
            Long otherId = chatUserInfo.getOtherId();
            Long roomId = chatRoom.getId();
            ChatMessage chatMessage = chatMessageRepository.findById(chatRoom.getLastMessageId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 메세지가 존재하지 않습니다.")
            );

            int notVerifiedMsgCnt = chatMessageRepository.findAllByChatRoomIdAndReadMsgAndUserId(roomId, false, otherId).size();

            String otherNick;
            String otherProfileImg;
            Optional<User> other = userRepository.findById(otherId);
            if(other.isPresent()){
                otherNick = other.get().getNickname();
                otherProfileImg = other.get().getProfileImg();
            } else{
                otherNick = "알 수 없음";
                otherProfileImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
            }

            chatRoomListResponseDtoList.add(generateChatRoomListResponseDto(roomId, chatMessage, notVerifiedMsgCnt, otherNick, otherProfileImg));
        }

        chatRoomListResponseDtoList.sort(ChatRoomListResponseDto::compareTo);
        return chatRoomListResponseDtoList;
    }


    // 유저가 참여한 특정 채팅방 조회 메소드
    public ChatRoomResponseDto getRoom(Long roomId, User user) {
        Long userId = user.getId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다")
        );

        // 채팅방에 있는 모든 유저 정보 가져오기
        ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(userId, chatRoom.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 채팅방 정보가 존재하지 않습니다")
        );

        Optional<User> opponent = userRepository.findById(chatUserInfo.getOtherId());
        String opponentnick;
        if (opponent.isPresent()) {
            opponentnick = opponent.get().getNickname();
        } else {
            opponentnick = "알 수 없음";
        }

        return generateChatRoomResponseDto(chatRoom, opponentnick);
    }


    // 채팅방 생성 메소드
    @Transactional
    public ChatRoomResponseDto createChatRoom(Long carpoolId, User sender) {

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 카풀 게시물은 존재하지 않습니다")
        );

        if (sender.getAgeRange() == null || sender.getGender() == null) {
            throw new IllegalArgumentException("추가 동의 항목이 필요합니다.");
        } else if (sender.getPhoneNum() == null) {
            throw new IllegalArgumentException("전화번호 인증이 필요한 서비스입니다.");
        }

        Long writerId = carpool.getUserId();
        User writer = userRepository.findById(writerId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 없습니다")
        );

        Long senderId = sender.getId();
        if (writerId.equals(senderId)) {
            throw new IllegalArgumentException("채팅은 다른 유저와만 가능합니다");
        }

        String writerNickname = writer.getNickname();
        String writerPhone = writer.getPhoneNum();

        ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndOtherIdAndChatRoomCarpoolId(writerId, senderId, carpoolId);

        //채팅방이 존재한다면
        if (chatUserInfo != null) {
            ChatRoom existedChatRoom = chatUserInfo.getChatRoom();
            return generateChatRoomResponseDto(existedChatRoom, writerNickname);
        } else { //존재하지 않는다면 방을 만들어준다.
            // 방 생성 알림 메세지 글 작성자한테 전송하기
            String msg = carpool.getTitle() + "게시글에 대한 채팅이 왔습니다! 확인하세요 :)";
            messageService.createChatRoomAlert(writerPhone, msg);

            ChatRoom chatRoom = new ChatRoom(carpoolId);
            chatRoomRepository.save(chatRoom);

            //방 생성시 첫 메시지 강제전송
            ChatMessage initMsg = new ChatMessage(ChatMessage.MessageType.ENTER, chatRoom, sender.getId(), ":)");
            chatMessageRepository.save(initMsg);
            initMsg.setIsRead(true);
            chatRoom.setLastMessageId(initMsg.getId());

            //sender 정보
            ChatUserInfo chatUserInfoSender = new ChatUserInfo(sender.getId(), writer.getId(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoSender);

            //카풀 작성자 정보
            ChatUserInfo chatUserInfoWriter = new ChatUserInfo(writer.getId(), sender.getId(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoWriter);

            return generateChatRoomResponseDto(chatRoom, writerNickname);
        }
    }

    // 해당 채팅방에서 카풀 게시물 정보 조회 메소드
    public ChatRoomCarpoolInfoDto getCarpoolInChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다")
        );
        Carpool carpool = carpoolRepository.findById(chatRoom.getCarpoolId()).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다")
        );
        return generateChatRoomCarpoolInfoDto(carpool);
    }

    // 채팅방 나가기
    @Transactional
    public void exitChatRoom(Long roomId, User user) {
        Long userId = user.getId();
        ChatUserInfo userChatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 채팅방 정보가 존재하지 않습니다")
        );

        chatUserInfoRepository.deleteByUserIdAndChatRoomId(userId, roomId);
        Optional<ChatUserInfo> otherChatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(userChatUserInfo.getOtherId(), roomId);
        if (!otherChatUserInfo.isPresent()) {
            chatMessageRepository.deleteAllByChatRoomId(roomId);
            chatRoomRepository.deleteById(roomId);
        } else {
            String otherNick = user.getNickname();
            ChatMessage quitMessage = new ChatMessage(ChatMessage.MessageType.QUIT, userChatUserInfo.getChatRoom(), userId, otherNick + "님이 채팅방을 나갔습니다.");
            chatMessageRepository.save(quitMessage);

            Long quitMsgId = quitMessage.getId();
            ChatRoom chatRoom = otherChatUserInfo.get().getChatRoom();
            chatRoom.setLastMessageId(quitMsgId);
            chatRoom.setActive(false);

            ChatMessageResponseDto chatMessageResponseDto = ChatMessageResponseDto.builder()
                    .roomId(roomId)
                    .sender(otherNick)
                    .senderImg(user.getProfileImg())
                    .receiverId(userChatUserInfo.getOtherId())
                    .messageId(quitMsgId)
                    .message(quitMessage.getMessage())
                    .type(quitMessage.getType())
                    .build();
            redisPublisher.publish(chatMessageResponseDto); // 채팅방 나감 알림
        }
    }

    // 채팅방 생성
    private ChatRoomResponseDto generateChatRoomResponseDto(ChatRoom chatRoom, String nickName) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .roomName(nickName)
                .longRoomId(chatRoom.getId())
                .build();
    }

    // 채팅방 목록 조회
    private ChatRoomListResponseDto generateChatRoomListResponseDto(
            Long roomId,
            ChatMessage chatMessage,
            int notVerifiedMsgCnt,
            String otherNick,
            String otherProfileImg
    ) {
        return ChatRoomListResponseDto.builder()
                .roomId(roomId)
                .roomName(otherNick)
                .lastMsg(chatMessage.getMessage())
                .lastMsgTime(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .notVerifiedMsgCnt(notVerifiedMsgCnt)
                .userProfile(otherProfileImg)
                .build();
    }

    // 해당 채팅방에서 게시물 정보 조회
    private ChatRoomCarpoolInfoDto generateChatRoomCarpoolInfoDto(Carpool carpool) {
        return ChatRoomCarpoolInfoDto.builder()
                .title(carpool.getTitle())
                .startLocation(carpool.getStartLocation())
                .endLocation(carpool.getEndLocation())
                .date(carpool.getDate())
                .time(carpool.getTime())
                .memberNum(carpool.getMemberNum())
                .price(carpool.getPrice())
                .notice(carpool.getNotice())
                .build();
    }
}
