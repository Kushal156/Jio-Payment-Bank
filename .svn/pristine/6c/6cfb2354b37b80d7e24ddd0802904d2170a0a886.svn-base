package com.jpb.Config;

import org.springframework.stereotype.Component;

import com.jpb.DTO.AuthTokenResponseDTO;
import com.jpb.DTO.TokenStore;

@Component
public class TokenManager {

    private TokenStore tokenStore;

    public synchronized void saveToken(AuthTokenResponseDTO response) {

        TokenStore store = new TokenStore();

        //Access Token
        store.setAccessToken(response.getSession().getAccessToken().getTokenValue());
        long accessExpiry = System.currentTimeMillis() +
                (response.getSession().getAccessToken().getExpiresIn() * 1000);
        store.setAccessTokenExpiry(accessExpiry);

        //Refresh Token
        store.setRefreshToken(response.getSession().getRefreshToken().getTokenValue());
        long refreshExpiry = System.currentTimeMillis() +
                (response.getSession().getRefreshToken().getExpiresIn() * 1000);
        store.setRefreshTokenExpiry(refreshExpiry);

        //App Identifier
        store.setAppIdentifierToken(response.getSession().getAppIdentifierToken());

        this.tokenStore = store;
    }

    //Getters
    public String getAccessToken() {
        return tokenStore != null ? tokenStore.getAccessToken() : null;
    }

    public String getRefreshToken() {
        return tokenStore != null ? tokenStore.getRefreshToken() : null;
    }

    public String getAppIdentifierToken() {
        return tokenStore != null ? tokenStore.getAppIdentifierToken() : null;
    }

    //Validations
    public boolean isAccessTokenValid() {
        return tokenStore != null &&
                System.currentTimeMillis() < tokenStore.getAccessTokenExpiry();
    }

    public boolean isRefreshTokenValid() {
        return tokenStore != null &&
                System.currentTimeMillis() < tokenStore.getRefreshTokenExpiry();
    }

    public void clear() {
        tokenStore = null;
    }
}