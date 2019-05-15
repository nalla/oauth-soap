package com.example.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

class TokenValidator {
    private RSAPublicKey publicKey;

    private TokenValidator(final RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    static TokenValidator buildFromJwk(final String json) throws JOSEException, ParseException {
        final RSAKey rsaKey = RSAKey.parse(json);

        return new TokenValidator((RSAPublicKey)rsaKey.toKeyPair().getPublic());
    }

    boolean validate(final SignedJWT jwt) throws JOSEException {
        final JWSVerifier verifier = new RSASSAVerifier(this.publicKey);

        return jwt.verify(verifier);
    }
}
