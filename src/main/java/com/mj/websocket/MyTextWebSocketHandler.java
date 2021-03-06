package com.mj.websocket;

import com.mj.event.Event;
import com.mj.event.EventFactory;
import com.mj.players.Player;
import com.mj.room.Room;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyTextWebSocketHandler extends TextWebSocketHandler {
    private BiMap<WebSocketSession, Player> playerMaps = Maps.synchronizedBiMap(HashBiMap.create());
    private BiMap<WebSocketSession, Room> roomMaps = Maps.synchronizedBiMap(HashBiMap.create());

    @Autowired
    EventFactory eventExecutor;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // TODO: when someone leaves the room, terminate the game for all others as well
        playerMaps.remove(session);
        System.out.println("players --: " + playerMaps.values().toString());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        String payload = textMessage.getPayload();
        System.out.println("Message received: " + payload);

//        EventFactory eventExecutor = new EventFactory();
        Event event = eventExecutor.execute(session, payload, playerMaps, roomMaps);
        event.execute();
    }
}
