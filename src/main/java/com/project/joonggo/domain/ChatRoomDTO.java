package com.project.joonggo.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private List<ChatCommentVO> comments;
    private UserVO otherUser;
}
