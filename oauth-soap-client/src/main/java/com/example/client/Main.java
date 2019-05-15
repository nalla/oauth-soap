package com.example.client;

import com.example.client.connection.ChannelFactory;
import com.example.client.connection.JacksonObjectMapper;
import com.example.client.serviceinterface.InvalidTokenException;
import com.example.client.serviceinterface.MessageService;
import kong.unirest.Unirest;

public class Main {
    public static void main(String[] args) throws Exception {
        Unirest.config().setObjectMapper(new JacksonObjectMapper());

        ApplicationConfig applicationConfig = new ApplicationConfig();
        ChannelFactory channelFactory = new ChannelFactory(applicationConfig);
        MessageService messageService = channelFactory.instance();
        String id = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        String name = "example";
        String message = "<?xml version=\"1.0\" encoding=\"utf-8\"?><example><data><foo>1</foo><bar>1</bar></data></example>";

        boolean loop = args != null && args.length > 0 && args[0].equals("--loop");

        if (loop) {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    messageService.saveMessage(id, name, message);
                } catch (InvalidTokenException e) {
                    System.out.println(e.getFaultInfo().getMessage());
                }
                Thread.sleep(1000);
            }
        } else {
            try {
                messageService.saveMessage(id, name, message);
            } catch (InvalidTokenException e) {
                System.out.println(e.getFaultInfo().getMessage());
            }
        }
    }
}
