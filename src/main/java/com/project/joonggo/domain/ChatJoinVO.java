package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatJoinVO {
    private int roomId;             // 채팅방 id
    private int userNum;            // 참여자 num. id는 String 임.
}
