package com.example.client.connection;

import com.fasterxml.jackson.annotation.JsonProperty;

class TokenInfo {
    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("expires_in")
    int expiresIn;

    @JsonProperty("error")
    String error;
}
