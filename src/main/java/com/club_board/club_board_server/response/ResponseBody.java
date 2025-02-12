package com.club_board.club_board_server.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public sealed  abstract class ResponseBody<T> permits SuccessResponseBody, FailedResponseBody{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;
    private boolean success;
}