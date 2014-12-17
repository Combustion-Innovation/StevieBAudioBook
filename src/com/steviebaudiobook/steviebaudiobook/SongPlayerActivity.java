package com.steviebaudiobook.steviebaudiobook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SongPlayerActivity extends Activity implements OnCompletionListener, FileDownloader.Communicator {
	
	public SharedPreferences sharedPref;
	public ABChapter chapter;
	public Context context;
	
	
	public String trackName;
	public File file;
	public File extDir = Environment.getExternalStorageDirectory();
	
	
	
	public ToggleButton rewindBtn, playBtn, ffBtn;
	public ImageButton audioCheckBtn;//, rewindBtn, playBtn, ffBtn;
	public ImageView albumCover;
	public TextView chapterYears, albumText, chapterNumber, chapterTitle, audioLength, audioProgress;
	public LinearLayout background, progressLayout, buttonsLayout, volumeLayout;
	
	public SeekBar trackBar;
	public SeekBar volumeBar;
	
	public TrackPlayer trackPlayer;
	public FileDownloader fileDownloader;
	
	public ProgressDialog pd;
	public long progress;
	public String audioPath;
	
	public boolean fileExists;
	public boolean buttonClicked = false;
	public boolean isDownloading = false;
	
	public String[] albumStrings = {"", "", "", "Party Your Body - Album 1988", "In My Eyes - Album 1988", "Love & Emotion - Album 1990", "Healing - Album 1992", "Funky Melody - Album 1994", "Waiting For Your Love - Album 1996", "Right Here Right Now! - Album 1998", "It's So Good - Album 2001", "The Terminator - Album 2009", "The King of Hearts - Album 2014"};
	public int[] backgrounds = {R.drawable.blurredkidpicture, R.drawable.blurredchapter2, R.drawable.blurredchapter3, R.drawable.blurredpartyyourbodybackground, R.drawable.blurredinmyeyesbackground, R.drawable.blurredlovaeandemotionbackground, R.drawable.blurredhealingbackground, R.drawable.blurredfunkyemotionbackground, R.drawable.blurredwaitingforyourlovebackground, R.drawable.blurredrightherebackground, R.drawable.blurreditsogoodbackground, R.drawable.blurredterminatorbackground, R.drawable.blurredkingofheartsbackground};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_player);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		sharedPref = this.getSharedPreferences("stevieb", Context.MODE_PRIVATE);
		
		context = getBaseContext();
		albumCover = (ImageView)findViewById(R.id.sp_albumcover);
		
		audioCheckBtn = (ImageButton)findViewById(R.id.sp_audiocheck);
		rewindBtn = (ToggleButton)findViewById(R.id.sp_rewindButton);
		playBtn = (ToggleButton)findViewById(R.id.sp_playButton);
		ffBtn = (ToggleButton)findViewById(R.id.sp_ffButton);
		
		chapterYears = (TextView)findViewById(R.id.sp_chyears);
		albumText = (TextView)findViewById(R.id.sp_albumtext);
		chapterNumber = (TextView)findViewById(R.id.sp_chapterNumber);
		chapterTitle = (TextView)findViewById(R.id.sp_chapterName);
		audioLength = (TextView)findViewById(R.id.sp_trackLength);
		audioProgress = (TextView)findViewById(R.id.sp_currentLength);
		
		progressLayout = (LinearLayout)findViewById(R.id.sp_progressLayout);
		buttonsLayout = (LinearLayout)findViewById(R.id.audioButtonsLayout);
		volumeLayout = (LinearLayout)findViewById(R.id.sp_volumeLayout);
		
		
		background = (LinearLayout)findViewById(R.id.sp_background);
		
		trackBar = (SeekBar)findViewById(R.id.sp_trackbar);
		volumeBar = (SeekBar)findViewById(R.id.sp_volumebar);
		
		chapter = (ABChapter)getIntent().getSerializableExtra("chapter");
				
		setLayout();
		trackPlayer = new TrackPlayer(this, playBtn, rewindBtn, ffBtn, trackBar, audioProgress, audioLength, chapter.getAudioPath(), volumeBar, chapter.getAudioProgress());
				
		file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.StevieB.StevieB/files/" + trackName);
		
		Log.d("file", Boolean.toString(file.exists()));
		
		if(!file.exists()) {
			playBtn.setBackgroundResource(R.drawable.play);
			setDownloadButton();
			hideLayouts();
		}
		else {
			fileExists = true;
			setDeleteButton();
		
		}
		chapter.setAudioLength(trackPlayer.getTrackLength());
		
	}
		
	public void setLayout() {
		
		int albumImg = chapter.getAlbumImg();
		String chYrs = chapter.getYears();
		
		int chNum = chapter.getChapterNumber();
		String chTtl = chapter.getTitle();
		
		
		albumCover.setImageResource(albumImg);
		
		progress = chapter.getAudioProgress();
		audioPath = Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/";
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
		chapterYears.setTypeface(tf);
		albumText.setTypeface(tf);
		chapterNumber.setTypeface(tf);
		chapterTitle.setTypeface(tf);
				
		chapterYears.setText(chYrs);
		albumText.setText(albumStrings[chapter.getChapterNumber()]);
		chapterNumber.setText("Chapter " + Integer.toString(chNum + 1));
		chapterTitle.setText(chTtl);
		background.setBackgroundResource(backgrounds[chNum]);
		
		trackName = "chapter" + Integer.toString(chNum + 1) + ".mp3";
			
	}
	
	public void showLayouts() {
		/*
		buttonsLayout.setVisibility(View.VISIBLE);
		*/
		ffBtn.setOnClickListener(null);
		rewindBtn.setOnClickListener(null);
		playBtn.setOnClickListener(null);
		trackBar.setEnabled(true);
		volumeBar.setEnabled(true);
		progressLayout.setOnClickListener(null);
		volumeLayout.setOnClickListener(null);
				
		trackPlayer.setListeners();
				
	}
	
	public void hideLayouts() {
		/*
		buttonsLayout.setVisibility(View.GONE);
		*/
		trackBar.setEnabled(false);
		volumeBar.setEnabled(false);
		playBtn.setBackgroundResource(R.drawable.play);
		playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				makeDialog();
			}
			
		});
		
		rewindBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				makeDialog();
			}
			
		});
		
		ffBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				makeDialog();
			}
			
		});
		progressLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				makeDialog();
			}
			
		});
		volumeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				makeDialog();
			}
			
		});
		
	}
	
	public void leavePlayer() {
		
		if(file.exists()) {
			progress = trackPlayer.getProgress();
		}
		else {
			progress = 0;
		}
		
		
		chapter.setAudioProgress(progress);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong("ch" + (chapter.getChapterNumber() + 1) + "progress", progress);
		editor.commit();
		editor = null;
		
		trackPlayer.cancelAll();
		trackPlayer.leavePlayer();
				
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		
		returnIntent.putExtra("chapter", chapter);
		returnIntent.putExtra("position", chapter.getChapterNumber());
		
		finish();
		
		
	}
	
	
	
	public void receiverReturn() {
		setDeleteButton();
		trackPlayer.releasePlayer();
        chapter.setAudioExist(true);
        chapter.setAudioPath(audioPath);
        chapter.setAudioProgress(0);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        
        
        fileExists = true;
        	                                                       
        
        showLayouts();
        
        File file1 = new File(Environment.getExternalStorageDirectory() + "/StevieB/" + trackName);
		File file2 = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.stevieb.stevieb/files/" + trackName);
		
        try {
			if(copy(file1, file2)) {
				Log.d("fileExists", Boolean.toString(file2.exists()));
				file1.delete();
				editor.putBoolean("ch" + (chapter.getChapterNumber() + 1) + "exists", true);
		        editor.putLong("ch" + (chapter.getChapterNumber() + 1) + "progress", 0);
		        editor.commit();
		        editor = null;
		        
		        Toast.makeText(this, "Download Complete", Toast.LENGTH_LONG).show();
		        isDownloading = false;
		        fileDownloader.removeProgressDialog();
			    trackPlayer.playSong();
			    
			    playBtn.setBackgroundResource(R.drawable.pause);
				
			    
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}
	
	public void setDeleteButton() {
		audioCheckBtn.setBackgroundResource(R.drawable.clouddelete);
		audioCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!buttonClicked) {
					buttonClicked = true;
					file.delete();
					trackPlayer.stopPlayer();
					trackPlayer.resetProgress();
					trackPlayer.releasePlayer();
					fileExists = false;
					chapter.setAudioExist(false);
					chapter.setAudioProgress(0);
					audioProgress.setText("0:00");
					
					setDownloadButton();
					buttonClicked = false;
					hideLayouts();
					Toast.makeText(context, "Audio Deleted", Toast.LENGTH_LONG).show();
				}
			}
			
			
		});
	}
	
	public void setDownloadButton() {
		audioCheckBtn.setBackgroundResource(R.drawable.clouddownload);
		audioCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!buttonClicked) {
					if(!isDownloading) {
						buttonClicked = true;
						
						if(extDir.exists()) {
							
							isDownloading = true;
							makeDownloadManager();
							
							
						}
						else if(extDir.getFreeSpace() < chapter.getFileSize()) {
							
							
							makeFreeSpaceAlert();
							
						}
						else {
							makeNoSDAlert();
						}
						
				        buttonClicked = false;
						
					}
				}
			}
			
		});
	}
	
	public void makeDownloadManager() {
		isDownloading = true;
		fileDownloader = new FileDownloader(this);
		fileDownloader.setCommunicator(this);
		fileDownloader.setUrl("http://combustioninnovation.com/steviebmusic.com/audioBookFiles/");
		fileDownloader.setFilePath("/StevieB/");
		
		fileDownloader.setReceiver();
		fileDownloader.makeProgressDialog();
		fileDownloader.setFileString(trackName);
		
		fileDownloader.init1xManager(trackName);
		
	}

	public void makeDialog() {
		if(!isDownloading) {
			new AlertDialog.Builder(this)
		  	.setTitle("Download MP3")
		  	.setMessage("Your MP3 file is missing.\nWould you like to download it now?")
		  	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(extDir.exists()) {
						
						makeDownloadManager();
						
						
					}
					else if(extDir.getFreeSpace() < chapter.getFileSize()) {
						
						
						makeFreeSpaceAlert();
						
					}
					else {
						makeNoSDAlert();
					}
				}
		  	})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					hideLayouts();
					
				}
		  		
		  	}).show();
		}
	}
	
	public void makeFreeSpaceAlert() {
		new AlertDialog.Builder(this)
	  	.setTitle("Not enough free space")
	  	.setMessage("Please clear some space and try again./n"
	  			+ "Download size: " + Long.toString(chapter.getFileSize()) + "\n"
	  			+ "Free space: " + Long.toString(extDir.getFreeSpace()))
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
	}
	
	
	

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBackPressed() {
		if(!buttonClicked) {
			buttonClicked = true;
			leavePlayer();
			buttonClicked = false;
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
			
	public boolean copy(File src, File dst) throws IOException {
	    FileInputStream inStream = new FileInputStream(src);
	    FileOutputStream outStream = new FileOutputStream(dst);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	    return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    int action = event.getAction();
	    int keyCode = event.getKeyCode();
	    switch (keyCode) {
	        case KeyEvent.KEYCODE_VOLUME_UP:
	            if (action == KeyEvent.ACTION_DOWN) {
	                //TODO
	            	AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	            	trackPlayer.setVolumeBar(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
	            	return super.dispatchKeyEvent(event);
	            }
	            return true;
	        case KeyEvent.KEYCODE_VOLUME_DOWN:
	            if (action == KeyEvent.ACTION_DOWN) {
	                //TODO
	            	AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	            	trackPlayer.setVolumeBar(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
	            	return super.dispatchKeyEvent(event);
	            }
	            return true;
	        default:
	            return super.dispatchKeyEvent(event);
	   }
	   
	}
	

	
}
