package com.example.client.serviceinterface;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

@WebService(name = "MessageService", targetNamespace = "http://services.example.com/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MessageService {

    @WebMethod
    @Action(input = "http://services.example.com/MessageService/saveMessageRequest", output = "http://services.example.com/MessageService/saveMessageResponse", fault = {
            @FaultAction(className = InvalidTokenException.class, value = "http://services.example.com/MessageService/saveMessage/Fault/InvalidTokenException")
    })
    void saveMessage(
            @WebParam(name = "id", partName = "id") String id,
            @WebParam(name = "name", partName = "name") String name,
            @WebParam(name = "message", partName = "message") String message)
            throws InvalidTokenException;

}
