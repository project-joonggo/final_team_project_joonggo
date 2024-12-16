package com.project.joonggo.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomVO {
    private int roomId;
    private int userNum;
    private String roomName;
    private String roomStatus;
}
