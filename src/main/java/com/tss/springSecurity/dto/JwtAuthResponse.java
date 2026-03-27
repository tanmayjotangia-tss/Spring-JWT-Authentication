package com.tss.springSecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
