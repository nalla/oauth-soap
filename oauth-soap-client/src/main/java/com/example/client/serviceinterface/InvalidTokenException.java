package com.example.client.serviceinterface;

import javax.xml.ws.WebFault;

@WebFault(name = "InvalidTokenException", targetNamespace = "http://services.example.com/")
public class InvalidTokenException extends Exception {
    private FaultInfo faultInfo;

    public InvalidTokenException(String message, FaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public InvalidTokenException(String message, FaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public FaultInfo getFaultInfo() {
        return faultInfo;
    }

}
