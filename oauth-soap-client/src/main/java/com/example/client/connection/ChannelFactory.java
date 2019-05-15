package com.example.client.connection;

import com.example.client.ApplicationConfig;
import com.example.client.serviceinterface.MessageService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import javax.net.ssl.*;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.util.*;

public class ChannelFactory {
    private ApplicationConfig applicationConfig;
    private MessageService messageService = null;
    private ZonedDateTime lastCreateDate = null;
    private Integer expirationTime;

    public ChannelFactory(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    private static SSLContext loadCertificate() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        ArrayList<X509TrustManager> trustManagers = new ArrayList<>();
        trustManagers.add(getTrustManager(getKeyStore(System.getProperty("java.home") + "/lib/security/cacerts")));
        trustManagers.add(getTrustManager(getKeyStore("keystore.jks")));
        TrustManager[] finalTrustManagers = {new CompositeX509TrustManager(trustManagers)};
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, finalTrustManagers, null);
        context.getSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        return context;
    }

    private static KeyStore getKeyStore(String location) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        File file = new File(location);
        InputStream stream;

        if (file.exists()) {
            stream = new FileInputStream(location);
        } else {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
        }

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(stream, "changeit".toCharArray());

        if (stream != null) {
            stream.close();
        }

        return keyStore;
    }

    private static X509TrustManager getTrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keystore);
        return (X509TrustManager) factory.getTrustManagers()[0];
    }

    public MessageService instance() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        checkRenewal();

        if (this.messageService != null) {
            return this.messageService;
        }

        String url = applicationConfig.getUrl();
        QName qname = new QName("http://services.example.com/", "MessageService");
        Service service = Service.create(new URL(url), qname);
        MessageService messageService = service.getPort(MessageService.class);
        BindingProvider bindingProvider = (BindingProvider) messageService;

        if (url.startsWith("https://")) {
            SSLContext sslContext = loadCertificate();
            String SSL_SOCKET_FACTORY = "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";
            bindingProvider.getRequestContext().put(SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
        }

        bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, getRequestHeaders());

        this.messageService = messageService;

        return messageService;
    }

    private long getTokenAge() {
        return ZonedDateTime.now().toEpochSecond() - lastCreateDate.toEpochSecond();
    }

    private int getExpirationTime() {
        return expirationTime;
    }

    private void checkRenewal() {
        if (lastCreateDate == null || expirationTime == 0) {
            return;
        }

        if (getTokenAge() > getExpirationTime()) {
            messageService = null;
        }
    }

    private Map<String, List<String>> getRequestHeaders() {
        TokenInfo tokenInfo = getTokenInfo();
        Map<String, List<String>> headers = new HashMap<>();

        if (tokenInfo != null) {
            lastCreateDate = ZonedDateTime.now();
            expirationTime = tokenInfo.expiresIn / 2;
            String headerValue = tokenInfo.tokenType + " " + tokenInfo.accessToken;
            headers.put("Authorization", Collections.singletonList(headerValue));
        }

        return headers;
    }

    private TokenInfo getTokenInfo() {
        try {
            TokenInfo tokenInfo;
            final HttpResponse<TokenInfo> response = Unirest.post(applicationConfig.getAuthority())
                    .field("grant_type", "client_credentials")
                    .field("client_id", applicationConfig.getClientId())
                    .field("client_secret", applicationConfig.getClientSecret())
                    .field("scope", applicationConfig.getScope())
                    .asObject(TokenInfo.class);

            int status = response.getStatus();

            switch (status) {
                case 200:
                    tokenInfo = response.getBody();

                    System.out.println("Retrieved access token:" + tokenInfo.accessToken);
                    System.out.println("The received token will expire in " + tokenInfo.expiresIn + " seconds.");
                    System.out.println("The received token is of type " + tokenInfo.tokenType + ".");

                    return tokenInfo;

                case 400:
                    tokenInfo = response.getBody();
                    System.out.println("Error: " + tokenInfo.error);

                    return null;

                default:
                    System.out.println("Got status code " + status + " when trying to get access token.");

                    return null;
            }

        } catch (UnirestException e) {
            e.printStackTrace();

            return null;
        }
    }
}
