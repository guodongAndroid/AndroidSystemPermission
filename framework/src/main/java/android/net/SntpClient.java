package android.net;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by john.wick on 2025/5/27
 */
public class SntpClient {

    public boolean requestTime(String host, int timeout) {
        return true;
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    public boolean requestTime(String host, int timeout, Network network) {
        return true;
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public boolean requestTime(String host, int port, int timeout, Network network) {
        return true;
    }

    public boolean requestTime(InetAddress address, int port, int timeout) {
        return true;
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    public boolean requestTime(InetAddress address, int port, int timeout, Network network) {
        return true;
    }

    /**
     * Returns the offset calculated to apply to the client clock to arrive at {@link #getNtpTime()}
     */
    @VisibleForTesting
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public long getClockOffset() {
        return -1;
    }

    /**
     * Returns the time computed from the NTP transaction.
     *
     * @return time value computed from NTP server response.
     */
    public long getNtpTime() {
        return -1;
    }

    /**
     * Returns the reference clock value (value of SystemClock.elapsedRealtime())
     * corresponding to the NTP time.
     *
     * @return reference clock corresponding to the NTP time.
     */
    public long getNtpTimeReference() {
        return -1;
    }

    /**
     * Returns the round trip time of the NTP transaction
     *
     * @return round trip time in milliseconds.
     */
    public long getRoundTripTime() {
        return -1;
    }

    /**
     * Returns the address of the NTP server used in the NTP transaction
     */
    @Nullable
    public InetSocketAddress getServerSocketAddress() {
        return null;
    }
}
