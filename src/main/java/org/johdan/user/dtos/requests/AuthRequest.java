package org.johdan.user.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    public String usernameOrEmail;
    public String password;
}
