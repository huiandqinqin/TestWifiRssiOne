package activitytest.example.gh.wxit.testwifirssi;


import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnStart ;
    public WifiManager wifiManager;             //管理wifi
    public ConnectivityManager connectManager;              //管理网络连接
    public NetworkInfo netInfo;                 //网络连接
    public WifiInfo wifiInfo;                   //wifi
    public DhcpInfo dhcpInfo;  //<span style="white-space:pre">                   //动态主机配置协议信息的对象，获得IP等网关信息
    String wifiProperty ;
    ArrayList <ScanResult> listAll;                   //存放周围wifi热点对象的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listAll = (ArrayList<ScanResult>) wifiManager.getScanResults();
        btnStart =(Button)this.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                init();
                                Log.d("Num:",String.valueOf(listAll.size()))  ;
                                for(int i=0 ;i<listAll.size() ;i++){
                                    Log.d("Leave",String.valueOf(listAll.get(i).level))  ;
                                    Log.d("WifiName",String.valueOf(listAll.get(i).BSSID))  ;
                                    Log.d("WifiName",String.valueOf(listAll.get(i).capabilities))  ;
                                }
                                Log.d("当前连接的wifi信息是:",wifiProperty);
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                    }
                }).start();
            }
        });

    }

    private void init() {

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);        //获得系统wifi服务
        connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        dhcpInfo = wifiManager.getDhcpInfo();
        wifiInfo = wifiManager.getConnectionInfo();

        wifiProperty = "当前连接Wifi信息如下："+wifiInfo.getSSID()+'\n'+
                "Rssi:"     +     String.valueOf(wifiInfo.getRssi())   +'\n'+
                "ip:"     +     FormatString(dhcpInfo.ipAddress)   +'\n'+
                "mask:"   +     FormatString(dhcpInfo.netmask)     +'\n'+
                "netgate:"+     FormatString(dhcpInfo.gateway)     +'\n'+
                "dns:"    +     FormatString(dhcpInfo.dns1)  ;
    }
/*
FormatString 和  intToByteArray
是两个 装换ip的方法
 */
    public String FormatString(int value){
        String strValue="";
        byte[] ary = intToByteArray(value);
        for(int i=ary.length-1;i>=0;i--){
            strValue += (ary[i] & 0xFF);
            if(i>0){
                strValue+=".";
            }
        }
        return strValue;
    }
    public  byte[] intToByteArray(int value){
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++){
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    //将搜索到的wifi根据信号强度从强到弱进行排序
    private void sortByLevel(ArrayList<ScanResult> list) {
        for(int i=0;i<list.size();i++)
            for(int j=1;j<list.size();j++)
            {
                if(list.get(i).level<list.get(j).level)    //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
    }
}
