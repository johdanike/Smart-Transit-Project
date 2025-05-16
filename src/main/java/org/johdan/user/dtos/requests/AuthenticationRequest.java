package org.johdan.user.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationRequest {
    private String email;
    private String password;
}
