package org.johdan.user.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterUserResponse {
    private String username;
    private String message;
}
