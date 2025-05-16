package org.johdan.user.dtos.responses;

import lombok.Getter;

@Getter
public class AuthenticationResponse {
    private final String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

}
