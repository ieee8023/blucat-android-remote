package the.bluetoothremote;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import the.bluetoothreverseshell.R;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Bluetooth_Remote extends Activity {

	static BluetoothServerSocket socket = null;
	static final String TAG = "BTRS";
	private static List<String> logs = new ArrayList<String>();

	
	OutputStream os = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_bluetooth__remote);
        final Activity activity = this;
    	
    	Button back = (Button) findViewById(R.id.backward);
    	
    	back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					if (os != null){
						os.write("b\n".getBytes());
						os.flush();
					}else{
						Toast.makeText(activity, "Nothing is connected!", Toast.LENGTH_SHORT);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
    	
    	
    	
    	
    	
    	
    	Button forward = (Button) findViewById(R.id.foward);
    	
    	forward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					if (os != null){
						os.write("f\n".getBytes());
						os.flush();
					}else{
						Toast.makeText(activity, "Nothing is connected!", Toast.LENGTH_SHORT);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
    	
    	
    	
    	
    	
    	ToggleButton b = (ToggleButton) findViewById(R.id.toggleButton1);
    	b.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			if(isChecked){
				Toast.makeText(getApplicationContext(), "Turning On", Toast.LENGTH_LONG).show();
				prependLog("Turning On");
				
				createShell();
				
				buttonView.setChecked(true);
				
			}else{
				Toast.makeText(getApplicationContext(), "Turning Off", Toast.LENGTH_LONG).show();
				prependLog("Turning Off");
				
				stopShell();
			}
				
			
			//if (socket == null)
			
		}
	});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bluetooth__remote, menu);
        return true;
    }
    
    @Override
    protected void onDestroy() {
    	
    	stopShell();
    	super.onDestroy();
    }
    
    @Override
    public void onUserInteraction() {
    	
    	drawLogs();
    	super.onUserInteraction();
    }
    
    
    private void stopShell(){
    	
    	if (socket != null){
    		
    		try {
				socket.close();
			} catch (IOException e) {
				prependLog("Error:" + e.getMessage());
				e.printStackTrace();
			}
    		socket = null;
    	}
    	
    }
    
    private void createShell(){
    	
    	String serviceName = getString(R.string.SDP_Name);
    	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    	
    	
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); 
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0); 
        startActivity(discoverableIntent);
    	
    	try {
			socket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(serviceName, uuid);
			
			prependLog("Socket Created");
			
			new Thread( new Runnable() {
        		public void run() {

        			prependLog("Forked Thread");
        			
        			try {
        				while(true){

        					prependLog("Waiting for connection, socket.accept();");

        					final BluetoothSocket soc = socket.accept();

        					prependLog("Got Connection");
        					
        					//final Process p = Runtime.getRuntime().exec("/system/bin/sh -i");

        					//prependLog("Launched /system/bin/sh");


        					//final OutputStream os = new CharArrayWriter();
        					//final InputStream is = p.getInputStream();
        					//final InputStream err = p.getErrorStream();


        					os = soc.getOutputStream();
        					
        					

        					try {

        						byte[] buffer = new byte[1024]; // Adjust if you want
        						int bytesRead;
        						while ((bytesRead = soc.getInputStream().read(buffer)) != -1){
        							
        							os.write(buffer, 0, bytesRead);
        							os.flush();
        						}
        					} catch (IOException e) {
        						
        						prependLog("Error: " + e.getMessage() );
        						e.printStackTrace();
        					}


        					prependLog("Connection Ended");
        				}

        			} catch (IOException e) {
        				
        				prependLog("Error: " + e.getMessage() );
        				e.printStackTrace();
        			}

        		}
        	}).start();
			
		} catch (IOException e) {
			
			prependLog("Error: " + e.getMessage());
			e.printStackTrace();
		}
    	
    }
    
    
    
    
    
    private void prependLog(String s){
    	
    	logs.add(new Date() + " " + s);
    }
    
    
    private void drawLogs(){
    	TextView log = (TextView)findViewById(R.id.textView2);
    	
    	log.setText("++Tap screen to update log++");
    	for(String s : logs)
    		log.setText(s + "\n" + log.getText());
    }
}
