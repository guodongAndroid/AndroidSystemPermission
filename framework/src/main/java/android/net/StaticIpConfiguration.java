/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.net;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that describes static IP configuration.
 * <p>
 * This class is different from LinkProperties because it represents
 * configuration intent. The general contract is that if we can represent
 * a configuration here, then we should be able to configure it on a network.
 * The intent is that it closely match the UI we have for configuring networks.
 * <p>
 * In contrast, LinkProperties represents current state. It is much more
 * expressive. For example, it supports multiple IP addresses, multiple routes,
 * stacked interfaces, and so on. Because LinkProperties is so expressive,
 * using it to represent configuration intent as well as current state causes
 * problems. For example, we could unknowingly save a configuration that we are
 * not in fact capable of applying, or we could save a configuration that the
 * UI cannot display, which has the potential for malicious code to hide
 * hostile or unexpected configuration from the user: see, for example,
 * http://b/12663469 and http://b/16893413 .
 *
 * @hide
 */
@SuppressLint("NewApi")
public class StaticIpConfiguration implements Parcelable {
    public LinkAddress ipAddress;
    public InetAddress gateway;
    public final ArrayList<InetAddress> dnsServers;
    public String domains;

    public StaticIpConfiguration() {
        dnsServers = new ArrayList<InetAddress>();
    }

    public StaticIpConfiguration(StaticIpConfiguration source) {
        this();
    }

    public void clear() {
        ipAddress = null;
        gateway = null;
        dnsServers.clear();
        domains = null;
    }

    /**
     * Returns the network routes specified by this object. Will typically include a
     * directly-connected route for the IP address's local subnet and a default route. If the
     * default gateway is not covered by the directly-connected route, it will also contain a host
     * route to the gateway as well. This configuration is arguably invalid, but it used to work
     * in K and earlier, and other OSes appear to accept it.
     */
    public List<RouteInfo> getRoutes(String iface) {
        List<RouteInfo> routes = new ArrayList<>(3);
        return routes;
    }

    /**
     * Returns a LinkProperties object expressing the data in this object. Note that the information
     * contained in the LinkProperties will not be a complete picture of the link's configuration,
     * because any configuration information that is obtained dynamically by the network (e.g.,
     * IPv6 configuration) will not be included.
     */
    public LinkProperties toLinkProperties(String iface) {
        return new LinkProperties();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("IP address ");
        if (ipAddress != null) str.append(ipAddress).append(" ");

        str.append("Gateway ");
        if (gateway != null) str.append(gateway.getHostAddress()).append(" ");

        str.append(" DNS servers: [");
        for (InetAddress dnsServer : dnsServers) {
            str.append(" ").append(dnsServer.getHostAddress());
        }

        str.append(" ] Domains ");
        if (domains != null) str.append(domains);
        return str.toString();
    }

    public int hashCode() {
        int result = 13;
        result = 47 * result + (ipAddress == null ? 0 : ipAddress.hashCode());
        result = 47 * result + (gateway == null ? 0 : gateway.hashCode());
        result = 47 * result + (domains == null ? 0 : domains.hashCode());
        result = 47 * result + dnsServers.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        return obj instanceof StaticIpConfiguration;
    }

    /**
     * Implement the Parcelable interface
     */
    public static Creator<StaticIpConfiguration> CREATOR =
            new Creator<StaticIpConfiguration>() {
                public StaticIpConfiguration createFromParcel(Parcel in) {
                    StaticIpConfiguration s = new StaticIpConfiguration();
                    readFromParcel(s, in);
                    return s;
                }

                public StaticIpConfiguration[] newArray(int size) {
                    return new StaticIpConfiguration[size];
                }
            };

    /**
     * Implement the Parcelable interface
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Implement the Parcelable interface
     */
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected static void readFromParcel(StaticIpConfiguration s, Parcel in) {
    }
}