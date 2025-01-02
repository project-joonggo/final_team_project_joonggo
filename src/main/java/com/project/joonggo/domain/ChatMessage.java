package com.project.joonggo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String message;
    private String timestamp;
    private String content;
    private String sender;
    private String userAddress;
}
