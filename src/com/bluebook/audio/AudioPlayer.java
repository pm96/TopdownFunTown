package com.bluebook.audio;

import com.bluebook.util.GameObject;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

/**
 * class 'AudioPlayer' is generated to play audio-files loaded by the class 'AudioLoader'
 */
public class AudioPlayer {

    private boolean isSpatial = false;
    private Clip clip;
    public static float volume = 1f;

    private GameObject source;

    private FloatControl gainControl;

    /**
     * Constructor for AudioPlayer which sets and loads audio-spritePath and gains control of the audio
     * through FloatControls gainControl
     *
     * @param path spritePath
     */
    public AudioPlayer(String path) {
        clip = AudioLoader.loadAudioClip(path);
        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if (clip.isControlSupported(Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * AudioPlayer.volume) + gainControl.getMinimum();
            volume.setValue(gain);
        }
    }

    /**
     * plays the selected audio once
     */
    public void playOnce() {
        clip.loop(0);
        playAudio();
    }

    void playAudio() {
        if (isSpatial) {
            if (clip.isControlSupported(FloatControl.Type.PAN)) {
                FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.PAN);
                float pan = (float) (Math
                    .max(0, Math.min(1, source.getProcentageXPosition() * 1.5 - .75)));
                balance.setValue(pan);
            } else if (clip.isControlSupported(FloatControl.Type.BALANCE)) {
                FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
                float pan = (float) (Math
                    .max(0, Math.min(1, source.getProcentageXPosition() * 1.5 - .75)));
                balance.setValue(pan);
            } else {
                System.out.println(
                    "NO PAN OR BALANCE SUPPORTED channels: " + clip.getFormat().getChannels());
            }
        }
        if (clip == null) {
            return;
        }
        stop();
        clip.setFramePosition(0);
        if (!clip.isRunning()) {
            clip.start();
        }
    }

    /**
     * stops currently playing audio if clip.isRunning() returns true
     */
    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * stops, drains then closes the selected audio-clip.
     */
    public void close() {
        stop();
        clip.drain();
        clip.close();
    }

    /**
     * sets LOOP_CONTINOUSLY to true and calls clip.PlayOce()
     */
    public void playLoop() {
        clip.loop(clip.LOOP_CONTINUOUSLY);
        playAudio();
    }

    /**
     * Sets the volum of selected audio-clip through decibel. e.g. -30 to lower the amount
     *
     * @param volume decibel
     */
    public void setVolume(float volume) {
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    }

    /**
     * Checks if the audio-clip is running
     *
     * @return true OR false
     */
    public boolean isRunning() {
        return clip.isRunning();
    }

    /**
     * Checks if isSpatial is true OR false
     *
     * @return true OR false
     */
    public boolean isSpatial() {
        return isSpatial;
    }

    /**
     * Will try to render with
     *
     * @param source GameObject source
     */
    public void setSpatial(GameObject source) {
        this.isSpatial = true;
        this.source = source;
    }

    public Clip getClip() {
        return clip;
    }

    public FloatControl getGainControl() {
        return gainControl;
    }
}
