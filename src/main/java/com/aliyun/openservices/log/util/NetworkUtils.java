package com.aliyun.openservices.log.util;

import com.aliyun.openservices.log.common.Consts;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public final class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * Checks if the specified string is a valid IP address.
     *
     * @param str the string to check
     * @return true if the string validates as an IP address
     */
    public static boolean isIPAddr(String str) {
        return InetAddressValidator.getInstance().isValid(str);
    }

    /**
     * Get the IP address of current machine.
     *
     * @return An IP address or {@code null} if unable to get the IP address
     */
    public static String getLocalMachineIP() {
        final InetAddressValidator validator = InetAddressValidator.getInstance();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (!ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();
                    if (!address.isLinkLocalAddress() && address.getHostAddress() != null) {
                        String ipAddress = address.getHostAddress();
                        if (ipAddress.equals(Consts.CONST_LOCAL_IP)) {
                            continue;
                        }
                        if (validator.isValidInet4Address(ipAddress)) {
                            return ipAddress;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            // swallow it
        }
        return null;
    }
}
