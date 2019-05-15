package com.example.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.json.JSONObject;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

@WebService(
        endpointInterface = "com.example.services.MessageService",
        serviceName = "MessageService",
        name = "MessageService",
        portName = "MessageService")
public class MessageServiceImpl implements MessageService {
    private static TokenValidator tokenValidator = null;

    @Resource
    WebServiceContext webServiceContext;

    @WebMethod
    public void saveMessage(final String id, final String name, final String message) throws InvalidTokenException {
        final String token = getJwtToken();

        if (token != null) {
            parseJwtToken(token);
        } else {
            throw new InvalidTokenException();
        }
    }

    private String getJwtToken() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        Map headers = (Map) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        ArrayList authorization = (ArrayList) headers.get("Authorization");

        if (authorization != null) {
            String token = (String) authorization.get(0);

            return token.replace("Bearer ", "");
        }

        return null;
    }

    private void parseJwtToken(final String token) throws InvalidTokenException {
        tryLoadPublicKey();

        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (tokenValidator.validate(jwt)) {
                JWTClaimsSet claims = jwt.getJWTClaimsSet();
                System.out.println("aud: " + claims.getAudience());
                System.out.println("iss: " + claims.getIssuer());
                System.out.println("exp: " + claims.getExpirationTime());
                System.out.println("nbf: " + claims.getNotBeforeTime());

                if (!claims.getAudience().contains("Example.Scope")) {
                    throw new InvalidTokenException();
                }
            } else {
                throw new InvalidTokenException();
            }

        } catch (JOSEException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void tryLoadPublicKey() {
        if (tokenValidator == null) {
            try {
                ApplicationConfig applicationConfig = new ApplicationConfig();
                HttpResponse<JsonNode> httpResponse = Unirest.get(applicationConfig.getAuthority()).asJson();
                JSONObject object = httpResponse.getBody().getObject();
                String jwk = object.getJSONArray("keys").getJSONObject(0).toString();

                tokenValidator = TokenValidator.buildFromJwk(jwk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
