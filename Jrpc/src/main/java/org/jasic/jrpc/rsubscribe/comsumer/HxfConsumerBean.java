package org.jasic.jrpc.rsubscribe.comsumer;

import org.jasic.jrpc.rsubscribe.Subscriber;

/**
 * Author Jasic
 * Date 14-12-16.
 * <p/>
 * 山寨版本hsf Consumer been
 */
public class HxfConsumerBean {
    public String name;
    public String version;
    private Subscriber subscriber;

    public HxfConsumerBean() {

    }

    public HxfConsumerBean setName0(String name) {
        this.name = name;
        return this;
    }

    public HxfConsumerBean setVersion0(String version) {
        this.version = version;
        return this;
    }

    public HxfConsumerBean setSubscriber0(Subscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public void init() {
        subscriber.subscribe(name);
    }
}

