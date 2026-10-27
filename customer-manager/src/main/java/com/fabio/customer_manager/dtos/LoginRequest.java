package com.fabio.customer_manager.dtos;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
