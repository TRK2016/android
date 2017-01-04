package jp.co.sugitest.wifiremocon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.Socket;

import static jp.co.sugitest.wifiremocon.Define.RemoteID.*;

public class MainActivity extends AppCompatActivity {
    private static String IPADDRESS = "192.168.4.1";
    private static int PORT = 6000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        // まずはWifi切断
        WifiConnectManager manager = WifiConnectManager.getInstance();
        manager.stopConnect(); 
    }

    @Override
    public void onResume() {
        super.onResume();

        // まずはWifi接続
        WifiConnectManager manager = WifiConnectManager.getInstance();
        manager.startConnect(IPADDRESS, PORT);

        // 一定時間ごとにソケット通信でイベントを送信する
        Thread thread = new Thread() {
            public void run() {
                try {
                    WifiConnectManager manager = WifiConnectManager.getInstance();
                    Define.WifiStatus status = manager.getStatus();

                    Define.RemoteID id = REMOTEID_ENGINESTART;
                    do {
                        wait(10000);
                        manager.sendData(id);

                        status = manager.getStatus();
                        id = getNextRemoteID(id);
                    } while(status == Define.WifiStatus.WIFISTATUS_CONNECT);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }



    private Define.RemoteID getNextRemoteID(Define.RemoteID id) {
        Define.RemoteID ret = REMOTEID_ENGINESTART;

        switch (id) {
            case REMOTEID_ENGINESTART:
                ret = REMOTEID_ENGINESTOP;
                break;
            case REMOTEID_ENGINESTOP:
                ret = REMOTEID_ENGINEBACK;
                break;
            case REMOTEID_ENGINEBACK:
                ret = REMOTEID_SHIFTHI;
                break;
            case REMOTEID_SHIFTHI:
                ret = REMOTEID_SHIFTNEUTRAL;
                break;
            case REMOTEID_SHIFTNEUTRAL:
                ret = REMOTEID_SHIFTLOW;
                break;
            case REMOTEID_SHIFTLOW:
                ret = REMOTEID_STEERINGLEFT;
                break;
            case REMOTEID_STEERINGLEFT:
                ret = REMOTEID_STEERINGNEUTRAL;
                break;
            case REMOTEID_STEERINGNEUTRAL:
                ret = REMOTEID_STEERINGRIGHT;
                break;
            case REMOTEID_STEERINGRIGHT:
                ret = REMOTEID_ENGINESTART;
                break;
            default:
                break;
        }

        return ret;
    }

}
