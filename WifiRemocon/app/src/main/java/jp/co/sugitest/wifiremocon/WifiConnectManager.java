package jp.co.sugitest.wifiremocon;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Sugi on 2017/01/02.
 */
public class WifiConnectManager {

    // シングルトンインスタンス
    private static WifiConnectManager mSelf = null;

    // ソケット通信スレッド
    private static Thread mThread = null;
    // ソケット通信用クラス
    private static Socket mSocket = null;

    private static InputStream mInputStream = null;
    private static OutputStream mOutputStream = null;
    private static byte[] mRead = new byte[1024];
    private static byte[] mWrite = new byte[1];

    public static WifiConnectManager getInstance() {
        if(mSelf == null) {
            mSelf = new WifiConnectManager();
        }

        return mSelf;
    }

    public static void startConnect(final String ip, final int port) {
        if(mSocket != null && mSocket.isConnected()) {
            return;
        }

        // 別スレッドを起こしてソケット通信を行う
        mThread = new Thread() {
            public void run() {
                int size = 0;
                byte[] buf = new byte[1024];
                try {
                    mSocket = new Socket(ip, port);
                    if(mSocket != null && mSocket.isConnected()) {
                        mInputStream = mSocket.getInputStream();
                        mOutputStream = mSocket.getOutputStream();

                        Log.v("connect","connect success");
                    } else {
                        Log.e("connect", "connect error");
                    }

                    while(mSocket != null && mSocket.isConnected()) {
                        size = mInputStream.read(buf);
                        if(size<=0) {
                            continue;
                        }

                        System.arraycopy(buf, 0, mRead, 0, size);
                        mHandler.obtainMessage(Define.WIFICON_REVMSG, mRead).sendToTarget();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        return;
    }

    public static void stopConnect() {
        if(mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        mInputStream = null;
        mOutputStream = null;
        mSocket = null;
    }

    public static boolean sendData(Define.RemoteID data) {
        boolean ret = false;
        if(Define.WifiStatus.WIFISTATUS_CONNECT == getStatus()) {
            try {
                mWrite[0] = (byte)data.getInt();
                mOutputStream.write(mWrite);
                mOutputStream.flush();
                ret = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                ret = false;
            }
        }

        return ret;
    }


    public static Define.WifiStatus getStatus() {
        if(mSocket != null) {
            if(mSocket.isConnected()) {
                return Define.WifiStatus.WIFISTATUS_CONNECT;
            }
        }

        return Define.WifiStatus.WIFISTATUS_DISCONNECT;
    }

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Define.WIFICON_REVMSG:
                    byte[] data = (byte[])msg.obj;

//                    String str = "";
//                    for(int i=0;i<=data.length;i++) {
//                        str += String.valueOf(data[i]);
//                    }

                    // 受信データが何かわからないし何に使われるかもわからないので何もしない
                    break;
                default:
                    break;
            }
        }
    };



}
