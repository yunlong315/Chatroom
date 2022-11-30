package com.example.chatroom.model;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Notifications {

    public final static String EVENT_MODEL_UPDATE = "modelUpdate";
    public final static String EVENT_MODEL_UPDATE_ChatList = "chatListUpdate";
    public final static String EVENT_MODEL_UPDATE_FriendsList = "friendsListUpdate";
    public final static String EVENT_MODEL_ADD_ChatRoom = "addChatRoom";
    public final static String EVENT_MODEL_ADD_Friend = "addFriend";

    private final Map<String, List<SubscriberObject>> subscribers = new LinkedHashMap<>();

    private static Notifications instance = new Notifications();

    public void publish(String event) {

        Platform.runLater(() -> {
            List<SubscriberObject> subscriberList = instance.subscribers.get(event);

            if (subscriberList != null) {

                subscriberList.forEach(
                        subscriberObject -> subscriberObject.getCb().accept(event)
                );

                // event ends after last subscriber gets callback
            }
        });
    }

    public void subscribe(String event, Object subscriber, Consumer<String> cb) {

        if (!instance.subscribers.containsKey(event)) {
            List<SubscriberObject> slist = new ArrayList<>();
            instance.subscribers.put(event, slist);
        }

        List<SubscriberObject> subscriberList = instance.subscribers.get(event);

        subscriberList.add(new SubscriberObject(subscriber, cb));
    }

    public void unsubscribe(String event, Object subscriber) {

        List<SubscriberObject> subscriberList = instance.subscribers.get(event);

        if (subscriberList == null) {
            subscriberList.remove(subscriber);
        }
    }

    static class SubscriberObject {

        private final Object subscriber;
        private final Consumer<String> cb;

        public SubscriberObject(Object subscriber,
                                Consumer<String> cb) {
            this.subscriber = subscriber;
            this.cb = cb;
        }

        public Object getSubscriber() {
            return subscriber;
        }

        public Consumer<String> getCb() {
            return cb;
        }

        @Override
        public int hashCode() {
            return subscriber.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return subscriber.equals(obj);
        }
    }
}
