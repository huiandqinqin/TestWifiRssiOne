package activitytest.example.gh.wxit.testwifirssi;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import activitytest.example.gh.wxit.testwifirssi.constant.XiaocheInfomation;

public class MainTestTwo extends AppCompatActivity {
    private final int N = Menu.FIRST;
    ArrayList<ScanResult> list;  //存放周围wifi所有热点对象的列表
    ArrayList<ScanResult> listxiaoche = new ArrayList<ScanResult>();  //存放周围wifi  只有xiaoche 1 2 3 对象的列表
    WifiManager wifiManager;
    public WifiInfo wifiInfo;                   //wifi
    public DhcpInfo dhcpInfo;  //                 //动态主机配置协议信息的对象，获得IP等网关信息
    String wifiProperty ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show:
                showXiaoche();
                break;
            case R.id.showConnection:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test_two);
        init();

    }

    private void findAllList() {
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);  //获得系统wifi服务
        wifiManager.startScan();
        list = (ArrayList<ScanResult>) wifiManager.getScanResults();
    }

    private void showXiaoche() {
        init();
        if (list != null) {
            for (int i = 0; i < listxiaoche.size(); i++) {
                Log.d("所有小车wifi地址",listxiaoche.get(i).BSSID);
                Log.d("所有小车wifi名称", listxiaoche.get(i).SSID);
                Log.d("所有小车wifi强度", String.valueOf(listxiaoche.get(i).level));
            }
            Log.d("连接上的wifi测试",wifiProperty);
        }
        else {
            Log.d("show","未找到小车wifi");
        }
    }


    private void init() {
        TextView tv1 = (TextView) findViewById(R.id.tv1);
        TextView tv2 = (TextView) findViewById(R.id.tv2);
        TextView tv3 = (TextView) findViewById(R.id.tv3);
        findAllList();
        dhcpInfo = wifiManager.getDhcpInfo();
        wifiInfo= wifiManager.getConnectionInfo() ;
        wifiProperty = "当前连接Wifi信息如下："+wifiInfo.getSSID()+'\n'+
                "Rssi:"     +     String.valueOf(wifiInfo.getRssi())   +'\n'+
                "ip:"     +     FormatString(dhcpInfo.ipAddress)   +'\n'+
                "mask:"   +     FormatString(dhcpInfo.netmask)     +'\n'+
                "netgate:"+     FormatString(dhcpInfo.gateway)     +'\n'+
                "dns:"    +     FormatString(dhcpInfo.dns1)  ;
        findXiaocheWifi(list);
        if (list != null) {
            try {
                if (list.get(0).SSID != null && list.get(1).SSID != null) {
                    tv1.setText("信号最强为" + list.get(0).SSID);
                    tv2.setText("信号第二位：" + list.get(1).SSID);
                    tv3.setText("共有" + list.size() + "个wifi");
                }
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"请打开GPS",Toast.LENGTH_LONG).show();
            }

            Log.d("附近的所有wifi", String.valueOf(list.size()));
        }


    }

    //将搜索到的wifi根据信号强度从强到弱进行排序
    private void sortByLevel(ArrayList<ScanResult> list) {
        for (int i = 0; i < list.size(); i++)
            for (int j = 1; j < list.size(); j++) {
                if (list.get(i).level < list.get(j).level)  //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
    }

    private void findXiaocheWifi(ArrayList<ScanResult> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).BSSID.equals(XiaocheInfomation.Xiaoche2BSSID)) {
                listxiaoche.add(list.get(i));
            } else if (list.get(i).BSSID.equals(XiaocheInfomation.Xiaoche1BSSID) ) {
                listxiaoche.add(list.get(i));
            } else if (list.get(i).BSSID.equals(XiaocheInfomation.Xiaoche3BSSID)) {
                listxiaoche.add(list.get(i));
            }
        }
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

    /*
    *
    *
    *   */
}
