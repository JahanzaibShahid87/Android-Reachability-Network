
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ReachabilityNetwork {

	public interface ReachabilityDelegate
	{
		void NetworkChange(boolean isnetAvailable,boolean ismobileinternet,boolean isATINetworkConnected);
	}
	
	public ReachabilityDelegate delegate;
	Activity act;
	public ReachabilityNetwork(ReachabilityDelegate delegate,Activity act)
	{
		this.delegate = delegate;
		this.act = act;
	}

	public void Setup()
	{ 
		WifiReceiver receiverWifi = new WifiReceiver();

		final IntentFilter mIFNetwork = new IntentFilter();
		mIFNetwork.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION); //"android.net.conn.CONNECTIVITY_CHANGE"
		act.registerReceiver(receiverWifi, mIFNetwork);
	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			haveNetworkConnection() ;
		}
	}

	private boolean haveNetworkConnection() {

		boolean haveConnectedWifi=false,haveConnectedMobile=false;
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();

		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
			{
				if (ni.isConnected())
				{
					delegate.NetworkChange(true, false,CheckAtiNetwork());
					haveConnectedWifi = true;
					return haveConnectedWifi;
				}
				else 
				{
					delegate.NetworkChange(false, false,false);
					haveConnectedWifi = false;
					return haveConnectedWifi;
				}
			}
			/*if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
			{
				if (ni.isConnected())
				{
					delegate.NetworkChange(true, true,false);
					haveConnectedMobile = true;
				}
				else
				{
					delegate.NetworkChange(false, false,false);
					haveConnectedMobile = false;
				}
			}*/
			
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	boolean CheckAtiNetwork()
	{
		WifiManager wifiManager = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid=wifiInfo.getSSID();

		if (ssid.startsWith("\"") && ssid.endsWith("\""))
			ssid = ssid.substring(1, ssid.length()-1);

		String subString =ssid.substring(0, 3);
		System.out.println("This is subString"+subString);

		if(subString.equalsIgnoreCase(act.getResources().getString(R.string.NetworkName)))
		{
			return true; 
		}
		else
		{
			return false;
		}
	}
}
