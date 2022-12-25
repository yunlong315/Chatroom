package com.example.chatroom.model;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 实现订阅和发布机制的类。
 */
public class Notifications {

    public final static String EVENT_MODEL_UPDATE = "modelUpdate";
    /**
     * 以下情况发出该广播：
     * 1.当前用户创建了聊天室
     * 2.加入了某个聊天室
     */
    public final static String EVENT_MODEL_UPDATE_ChatList = "chatListUpdate";
    /**
     * 以下情况发出该广播：
     * 1.添加别人为好友
     * 2.别人添加自己为好友
     */
    public final static String EVENT_MODEL_UPDATE_FriendsList = "friendsListUpdate";
    /**
     * 以下情况发出该广播：
     * 已有的某个聊天室更新。
     */
    public final static String EVENT_MODEL_UPDATE_ONE_CHATROOM = "updateOneChatroom";
    /**
     * 以下情况发出该广播：
     * 收到新的消息。
     */
    public final static String EVENT_MODEL_UPDATE_MESSAGE = "updateMessage";
    /**
     * 以下情况发出该广播：
     * 已有的某个用户更新。
     */
    public final static String EVENT_MODEL_UPDATE_ONE_USER = "updateOneUser";
    /**
     * 以下情况发出该广播：
     * 发送消息后收到反馈。
     */
    public final static String EVENT_MODEL_UPDATE_SENDED = "updateSended";
    /**
     * 完成了只返回成功与否的操作。
     */
    public final static String EVENT_MODEL_OPERATION_DONE = "operationDone";
    private static final Notifications instance = new Notifications();
    private final Map<String, List<SubscriberObject>> subscribers = new LinkedHashMap<>();

    /**
     * 发布事件。
     *
     * @param event-事件
     */
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

    /**
     * 订阅。
     *
     * @param event-事件
     * @param subscriber-订阅者
     * @param cb-处理事件的方法
     */
    public void subscribe(String event, Object subscriber, Consumer<String> cb) {

        if (!instance.subscribers.containsKey(event)) {
            List<SubscriberObject> slist = new ArrayList<>();
            instance.subscribers.put(event, slist);
        }

        List<SubscriberObject> subscriberList = instance.subscribers.get(event);

        subscriberList.add(new SubscriberObject(subscriber, cb));
    }

    /**
     * 取消订阅。
     *
     * @param event-事件
     * @param subscriber-订阅者
     */
    public void unsubscribe(String event, Object subscriber) {

        List<SubscriberObject> subscriberList = instance.subscribers.get(event);

        if (subscriberList == null) {
            subscriberList.remove(subscriber);
        }
    }

    /**
     * 订阅者类。
     */
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
