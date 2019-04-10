package com.luka.giffero.engine;

import android.content.Context;
import android.media.MediaPlayer;

import com.luka.giffero.GifferoApplication;
import com.luka.giffero.R;

import java.util.ArrayList;

/**
 * Created by chae on 12/14/2016.
 */
public class AudioPlayer {
	private static AudioPlayer sharedInstance = null;
	public static AudioPlayer sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new AudioPlayer();
		return sharedInstance;
	}

	Context context = null;
	private ArrayList<MediaPlayer> arrAudioPlayer = null;
	private static int MAX_AUDIO_PLAYER_COUNT = 5;

	AudioPlayer() {
		context = GifferoApplication.sharedInstance();

		arrAudioPlayer = new ArrayList<>();
	}

	public void playCameraBeep() {
		if (arrAudioPlayer.size() >= MAX_AUDIO_PLAYER_COUNT) {
			MediaPlayer player = arrAudioPlayer.get(0);

			arrAudioPlayer.remove(player);

			player.stop();
			player.release();
		}

		MediaPlayer audioPlayer = MediaPlayer.create(GifferoApplication.sharedInstance(), R.raw.camera_beep);
		audioPlayer.setVolume(1.f, 1.f);
		audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				arrAudioPlayer.remove(mp);
				mp.release();
			}
		});
		audioPlayer.start();

		arrAudioPlayer.add(audioPlayer);
	}
}
