package jp.co.sugitest.wifiremocon;

/**
 * Created by Sugi on 2017/01/02.
 */
public class Define {
    // Wifi接続状態
    public enum WifiStatus {
        WIFISTATUS_CONNECT,
        WIFISTATUS_DISCONNECT
    }

    // Wifi接続のメッセージ受信ID
    public static final int WIFICON_REVMSG = 0x0001;

    // ラジコンへ送る送信ID
    public enum RemoteID {
        REMOTEID_ENGINESTART(1),
        REMOTEID_ENGINEBACK(2),
        REMOTEID_ENGINESTOP(3),
        REMOTEID_SHIFTHI(4),
        REMOTEID_SHIFTNEUTRAL(5),
        REMOTEID_SHIFTLOW(6),
        REMOTEID_STEERINGRIGHT(7),
        REMOTEID_STEERINGNEUTRAL(8),
        REMOTEID_STEERINGLEFT(9);

        private final int id;

        private RemoteID(final int id) {
            this.id = id;
        }

        public int getInt() {
            return this.id;
        }
    }
}
