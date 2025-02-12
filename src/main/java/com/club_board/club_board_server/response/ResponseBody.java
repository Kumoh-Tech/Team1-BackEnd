package com.club_board.club_board_server.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@JsonTypeInfo( // 다형성 처리, 어떤 하위 클래스로 역직렬화 할지
        use=JsonTypeInfo.Id.CUSTOM, // NAME을 사용하여 각 하위 클래스에 대해 이름을 지정
        include = JsonTypeInfo.As.EXISTING_PROPERTY, // 타입 정보를 JSON 속성으로 포함시킨다.
        property = "success",
        visible = true)   // property이름 : success

@JsonTypeIdResolver(BooleanTypeIdResolver.class)
public sealed  abstract class ResponseBody<T> permits SuccessResponseBody, FailedResponseBody{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;
    private boolean success;
}