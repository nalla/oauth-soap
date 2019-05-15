package com.example.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MessageService {
    @WebMethod
    void saveMessage(
            @WebParam(name = "id") final String id,
            @WebParam(name = "name") final String name,
            @WebParam(name = "message") final String message) throws InvalidTokenException;
}
