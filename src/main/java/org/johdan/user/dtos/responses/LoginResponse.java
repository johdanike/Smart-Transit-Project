package org.johdan.user.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String userName;
    private Boolean isLoggedIn;
    private String message;
    private String accessToken;
    private String refreshToken;
}
