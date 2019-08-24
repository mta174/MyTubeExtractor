package com.mta.playtube.extractor.subscription;

import java.io.Serializable;

public class SubscriptionItem implements Serializable {
    private final int serviceId;
    private final String url, name;

    public SubscriptionItem(int serviceId, String url, String name) {
        this.serviceId = serviceId;
        this.url = url;
        this.name = name;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()));
        buffer.append("[name=").append(name).append(" > ").append(serviceId).append(":").append(url).append("]");
        return buffer.toString();
    }
}
