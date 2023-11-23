package kr.mybrary.userservice.global.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SnsTopic {

    FOLLOW("follow");

    private final String topicName;
}
