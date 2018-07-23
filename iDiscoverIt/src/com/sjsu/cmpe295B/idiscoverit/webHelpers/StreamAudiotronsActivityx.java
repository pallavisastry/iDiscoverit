//package com.sjsu.cmpe295B.idiscoverit.webHelpers;
//import java.io.File;
//
//import android.app.Activity;
//import android.os.Bundle;
//
//public class StreamAudiotronsActivityx extends Activity {
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		System.out.println("INSIDE oncreate");
//		File dir = new File("data/data/com/sjsu/cmpe295B/idiscoverit/main/");
//
//		if (dir.isDirectory()) {
//		        String[] children = dir.list();
//		        for (int i = 0; i < children.length; i++) {
//		            new File(dir, children[i]).delete();
//		        }
//		    }
//	}
//}




//package com.sjsu.cmpe295B.idiscoverit.webHelpers;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//
//import android.app.Activity;
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.os.Handler;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//public class StreamAudiotronsActivityx extends Activity {
//
//	private static final int INITIAL_KB_BUFFER = 96 * 10 / 8; // 96kbps*10secs/8bits
//																// per byte
//	private TextView textStreamed;
//	private ImageButton playButton;
//	private ProgressBar progressBar;
//
//	private long mediaLengthInKb, mediaLengthInSeconds;
//	private int totalKbRead = 0;
//
//	private final Handler handler = new Handler();
//	private MediaPlayer mediaPlayer;
//
//	private File downloadedMediaFile;
//	private boolean isInterrupted;
//	private Context context;
//	private int counter = 0;
//
//	public StreamAudiotronsActivityx(Context context, TextView textStreamed,
//			ImageButton playButton, Button streamButton, ProgressBar progressBar) {
//		this.context = context;
//		this.textStreamed = textStreamed;
//		this.playButton = playButton;
//		this.progressBar = progressBar;
//	}
//
//	public void startStreaming(final String mediaUrl,String audiotronName)throws IOException {// long mediaLengthInKb,long mediaLenghtInSeconds) throws IOException {
//		
//		//this.mediaLengthInKb = mediaLengthInKb;
//    	//this.mediaLengthInSeconds = mediaLengthInSeconds;
//    	
//		Runnable r= new Runnable() {
//			
//			public void run() {
//				try {   
//	        		downloadAudioIncrement(mediaUrl);
//	            } catch (IOException e) {
//	            	Log.e(getClass().getName(), "Unable to initialize the MediaPlayer for fileUrl=" + mediaUrl, e);
//	            	return;
//	            }				
//			}
//		};new Thread(r).start();
//	}
//	public void downloadAudioIncrement(String mediaUrl)throws IOException{
//		
//		URLConnection cn = new URL(mediaUrl).openConnection();   
//        cn.connect();   
//        
//        InputStream stream = cn.getInputStream();
//        
//        if (stream == null) {
//        	Log.e(getClass().getName(), "Unable to create InputStream for mediaUrl:" + mediaUrl);
//        }
//        
//        downloadedMediaFile = new File(context.getCacheDir(),"downloadingMedia_" + (counter++) + ".dat");
//        FileOutputStream out = new FileOutputStream(downloadedMediaFile);   
//        byte buf[] = new byte[16384];
//        int totalBytesRead = 0, incrementalBytesRead = 0;
//        do {
//        	int numread = stream.read(buf);   
//            if (numread <= 0)   
//                break;   
//            out.write(buf, 0, numread);
//            totalBytesRead += numread;
//            incrementalBytesRead += numread;
//            totalKbRead = totalBytesRead/1000;
//            
//            testMediaBuffer();
//           	fireDataLoadUpdate();
//        } while (validateNotInterrupted());   
//
//       	stream.close();
//        if (validateNotInterrupted()) {
//	       	fireDataFullyLoaded();
//        }
//	}
//	private boolean validateNotInterrupted() {
//		if (isInterrupted) {
//			if (mediaPlayer != null) {
//				mediaPlayer.pause();
//				//mediaPlayer.release();
//			}
//			return false;
//		} else {
//			return true;
//		}
//    }
//	private void  testMediaBuffer() {
//	    Runnable updater = new Runnable() {
//	        public void run() {
//	            if (mediaPlayer == null) {
//	            	//  Only create the MediaPlayer once we have the minimum buffered data
//	            	if ( totalKbRead >= INITIAL_KB_BUFFER) {
//	            		try {
//		            		startMediaPlayer();
//	            		} catch (Exception e) {
//	            			Log.e(getClass().getName(), "Error copying buffered conent.", e);    			
//	            		}
//	            	}
//	            } else if ( mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000 ){ 
//	            	//  NOTE:  The media player has stopped at the end so transfer any existing buffered data
//	            	//  We test for < 1second of data because the media player can stop when there is still
//	            	//  a few milliseconds of data left to play
//	            	transferBufferToMediaPlayer();
//	            }
//	        }
//	    };
//	    handler.post(updater);
//    }
//	  private void startMediaPlayer() {
//	        try {   
//	        	File bufferedFile = new File(context.getCacheDir(),"playingMedia" + (counter++) + ".dat");
//	        	moveFile(downloadedMediaFile,bufferedFile);
//	    		
//	        	Log.e("Player",bufferedFile.length()+"");
//	        	Log.e("Player",bufferedFile.getAbsolutePath());
//	        	
//	    		mediaPlayer = new MediaPlayer();
//	        	mediaPlayer.setDataSource(bufferedFile.getAbsolutePath());
//	        	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//	    		mediaPlayer.prepare();
//	        	fireDataPreloadComplete();
//	        	
//	        } catch (IOException e) {
//	        	Log.e(getClass().getName(), "Error initializing the MediaPlaer.", e);
//	        	return;
//	        }   
//	    }
//	  private void transferBufferToMediaPlayer() {
//		    try {
//		    	// First determine if we need to restart the player after transferring data...e.g. perhaps the user pressed pause
//		    	boolean wasPlaying = mediaPlayer.isPlaying();
//		    	int curPosition = mediaPlayer.getCurrentPosition();
//		    	mediaPlayer.pause();
//
//	        	File bufferedFile = new File(context.getCacheDir(),"playingMedia" + (counter++) + ".dat");
//		    	//FileUtils.copyFile(downloadingMediaFile,bufferedFile);
//
//				mediaPlayer = new MediaPlayer();
//	    		mediaPlayer.setDataSource(bufferedFile.getAbsolutePath());
//	    		//mediaPlayer.setAudioStreamType(AudioSystem.STREAM_MUSIC);
//	    		mediaPlayer.prepare();
//	    		mediaPlayer.seekTo(curPosition);
//	    		
//	    		//  Restart if at end of prior beuffered content or mediaPlayer was previously playing.  
//	    		//	NOTE:  We test for < 1second of data because the media player can stop when there is still
//	        	//  a few milliseconds of data left to play
//	    		boolean atEndOfFile = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000;
//	        	if (wasPlaying || atEndOfFile){
//	        		mediaPlayer.start();
//	        	}
//			}catch (Exception e) {
//		    	Log.e(getClass().getName(), "Error updating to newly loaded content.", e);            		
//			}
//	    }
//	    
//	    private void fireDataLoadUpdate() {
//			Runnable updater = new Runnable() {
//		        public void run() {
//		        	textStreamed.setText((CharSequence) (totalKbRead + " Kb read"));
//		    		float loadProgress = ((float)totalKbRead/(float)mediaLengthInKb);
//		    		progressBar.setSecondaryProgress((int)(loadProgress*100));
//		        }
//		    };
//		    handler.post(updater);
//	    }
//	    
//	    /**
//	     * We have preloaded enough content and started the MediaPlayer so update the buttons & progress meters.
//	     */
//	    private void fireDataPreloadComplete() {
//	    	Runnable updater = new Runnable() {
//		        public void run() {
//		    		mediaPlayer.start();
//		    		startPlayProgressUpdater();
//		        	playButton.setEnabled(true);
//		        	//streamButton.setEnabled(false);
//		        }
//		    };
//		    handler.post(updater);
//	    }
//
//	    private void fireDataFullyLoaded() {
//			Runnable updater = new Runnable() { 
//				public void run() {
//	   	        	transferBufferToMediaPlayer();
//		        	textStreamed.setText((CharSequence) ("Audio full loaded: " + totalKbRead + " Kb read"));
//		        }
//		    };
//		    handler.post(updater);
//	    }
//	    
//	    public MediaPlayer getMediaPlayer() {
//	    	return mediaPlayer;
//		}
//		
//	    public void startPlayProgressUpdater() {
//	    	float progress = (((float)mediaPlayer.getCurrentPosition()/1000)/(float)mediaLengthInSeconds);
//	    	progressBar.setProgress((int)(progress*100));
//	    	
//			if (mediaPlayer.isPlaying()) {
//				Runnable notification = new Runnable() {
//			        public void run() {
//			        	startPlayProgressUpdater();
//					}
//			    };
//			    handler.postDelayed(notification,1000);
//	    	}
//	    }    
//	    
//	    public void interrupt() {
//	    	playButton.setEnabled(false);
//	    	isInterrupted = true;
//	    	validateNotInterrupted();
//	    }
//	    
//		public void moveFile(File	oldLocation, File	newLocation)
//		throws IOException {
//
//			if ( oldLocation.exists( )) {
//				BufferedInputStream  reader = new BufferedInputStream( new FileInputStream(oldLocation) );
//				BufferedOutputStream  writer = new BufferedOutputStream( new FileOutputStream(newLocation, false));
//	            try {
//			        byte[]  buff = new byte[8192];
//			        int numChars;
//			        while ( (numChars = reader.read(  buff, 0, buff.length ) ) != -1) {
//			        	writer.write( buff, 0, numChars );
//	      		    }
//	            } catch( IOException ex ) {
//					throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
//	            } finally {
//	                try {
//	                    if ( reader != null ){
//	                    	writer.close();
//	                        reader.close();
//	                    }
//	                } catch( IOException ex ){
//					    Log.e(getClass().getName(),"Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() ); 
//					}
//	            }
//	        } else {
//				throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath() );
//	        }
//		}
//	   
//}
