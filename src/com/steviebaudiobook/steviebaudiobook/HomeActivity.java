package com.steviebaudiobook.steviebaudiobook;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class HomeActivity extends Activity implements FileDownloader.Communicator {
	
	Context context;
	SharedPreferences sharedPref;
	boolean allTracksDownloaded;
	long enqueue;
	
	int totalChapters = 13;
	FileDownloader fileDownloader;
	
	RelativeLayout downloadHolder;

	DownloadList downloadList;
	
	File extDir = Environment.getExternalStorageDirectory();
	
	File file;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		
		context = this;
		sharedPref = this.getSharedPreferences("stevieb", Context.MODE_PRIVATE);
		
		makeSharedPrefs();
		
		
		RelativeLayout splashScreen = (RelativeLayout)findViewById(R.id.splashPage);
		splashScreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToAudioBook();
			}
			
			
		});
		
		
		
		
		
		
		if(!checkAllDownloaded()) {
		
			
			
			
			
			new Handler().postDelayed(new Runnable() {
						
	            @Override
	            public void run() {
	            	
	            	/*
	            	boolean notFirstTime = sharedPref.getBoolean("notInitial", false);
	            	
	            	if(!notFirstTime) {
	            		
	            		SharedPreferences.Editor editor = sharedPref.edit();
	            		editor.putBoolean("notInitial", true);
	            		editor.commit();
	            		editor = null;
		            	Intent intent = new Intent(context, DownloadList.class);
		            	startActivity(intent);
		            	
	            	}
	            	else {
	            		goToAudioBook();
	            	}
	            	*/
	            	Intent i = new Intent(context, DownloadList.class);
	            	startActivity(i);
	            	
	            	
	    	        
	            } 
	            
	        }, 3000);
	        
		
		}
		else {
			new Handler().postDelayed(new Runnable() {
											
	            @Override
	            public void run() {
	            	goToAudioBook();
	            }
            }, 3000);
			
		}
		
		
		
		
	}
	
	
	
	public boolean checkAllDownloaded() {
		
		for(int i=0; i<totalChapters; i++) {
			
			File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file.exists()) {
				return false;
			}
			
		}
		return true;
	}
	
	public boolean checkAllQueued() {
		
		for(int i=0; i<totalChapters; i++) {
			
			File file = new File(Environment.getExternalStorageDirectory() + "/StevieB/chapter" + Integer.toString(i+1) + ".mp3");
			Log.d("file", file.getPath());
			if(!file.exists()) {
				Log.d("Files Exists", "false");
				return false;
			}
			
		}
		Log.d("Files Exist", "true");
		return true;
	}

	public void goToAudioBook() {
		
		
		
		Intent i = new Intent(this, AudioBookActivity.class);
		startActivityForResult(i, 1);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == 1) {
			
			if(resultCode == RESULT_OK) {
				
			}
		}
		
	}
	
	public void makeSharedPrefs() {
		
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for(int i=0; i<totalChapters; i++) {
			File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file.exists()) {
				editor.putBoolean("ch" + Integer.toString(i+1) + "exists", false);
				editor.putLong("ch" + Integer.toString(i+1) + "progress" , 0);
				
			}
			else {
				editor.putBoolean("ch" + Integer.toString(i+1) + "exists", true);
			}
		}
		
		editor.commit();
		editor = null;
		
		for(int i=0; i<totalChapters; i++) {
			boolean ch = sharedPref.getBoolean("ch" + Integer.toString(i+1) + "exists", false);
			Log.d("ch" + Integer.toString(i+1), Boolean.toString(ch));
		}
		
	}
	
	public void makeNoSDAlert() {
		new AlertDialog.Builder(this)
	  	.setTitle("No SD Card")
	  	.setMessage("Your SD Card is missing.\nPlease insert card and try again.")
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
	}
	
	public void makeFreeSpaceAlert() {
		new AlertDialog.Builder(this)
	  	.setTitle("Not enough free space")
	  	.setMessage("Please clear some space and try again.")
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
	}
	
	
	
	public void copy(File src, File dst) throws IOException {
	    FileInputStream inStream = new FileInputStream(src);
	    FileOutputStream outStream = new FileOutputStream(dst);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	}
	
	public boolean copyFiles() {
		
		final Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File filepath = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/");
				if(!filepath.exists()) {
					filepath.mkdirs();
				}
				for(int i=0; i<totalChapters; i++) {
					
					File file1 = new File(Environment.getExternalStorageDirectory() + "/StevieB/chapter" + Integer.toString(i+1) + ".mp3");
					File file2 = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/chapter" + Integer.toString(i+1) + ".mp3");
					if(!file2.exists() && file1.exists()) {
						try {
							copy(file1, file2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						file1.delete();
					}
				}
				
			}
			
		});
		return true;
		
	}
	
	
	
	public void closeDownloads() {
		
			Log.d("allDLed", "true");
        	allTracksDownloaded = true;
        	makeSharedPrefs();
        	
        	
        	fileDownloader.removeProgressDialog();
	        fileDownloader.removeReceiver();
        	
        	
        	goToAudioBook();
			
		
	}
	
	public void getNextMissing() {
		
		String missing = "";
		
		for(int i=totalChapters; i>0; i--) {
			
			File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/chapter" + Integer.toString(i) + ".mp3");
			if(!file.exists()) {
				missing = "chapter" + Integer.toString(i) + ".mp3";
				
				
			}
			
		}
		
		if(!missing.equals("")) {
			Log.d("Missing", missing);
			fileDownloader.setFileString(missing);
			
			fileDownloader.initManager();
			//downloadNext(missing);
		}
		else {
			closeDownloads();
		}
	
	}
	
	@Override
	public void receiverReturn() {
		// TODO Auto-generated method stub
		for(int i=0; i<totalChapters; i++) {
			
			File file1 = new File(Environment.getExternalStorageDirectory() + "/StevieB/chapter" + Integer.toString(i+1) + ".mp3");
			File file2 = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file2.exists() && file1.exists()) {
				try {
					copy(file1, file2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				file1.delete();
			}
		}
		getNextMissing();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    
	}
	
	
}
