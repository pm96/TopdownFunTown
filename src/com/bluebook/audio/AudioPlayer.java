package com.bluebook.audio;

import com.bluebook.util.GameObject;

import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;

/**
 * class 'AudioPlayer' is generated to play audio-files loaded by the class 'AudioLoader'
 */
public class AudioPlayer {

    private boolean spital = false;
    private static String path = "";
    Clip clip;

    private GameObject source;

    FloatControl gainControl;

    /**
     * Constructor for AudioPlayer which sets and loads audio-path and gains control of the audio thorugh FloatControls gainControl
     * @param path
     */
    public AudioPlayer(String path){
        this.path = path;
        clip = AudioLoader.loadAudioClip(path);
        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    }

    /**
     * plays the selected audio once
     */
    public void playOnce(){
        if(spital){
            if(clip.isControlSupported(FloatControl.Type.PAN)) {
                FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.PAN);
                balance.setValue((float)(source.getProcentageXPosition() * 1.0 - 1.0));
            }else if(clip.isControlSupported(FloatControl.Type.BALANCE)){
                FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
                balance.setValue((float)(source.getProcentageXPosition() * 2.0 - 1.0));
            }else{
                System.out.println("NO PAN OR BALANCE SUPPORTED channels: " + clip.getFormat().getChannels());
            }
        }
        if(clip == null){
            return;
        }
        stop();
        clip.setFramePosition(0);
        while (!clip.isRunning()){
            clip.start();
        }
    }

    /**
     * stops currently playing audio if clip.isRunning() returns true
     */
    public void stop(){
        if(clip.isRunning()){
            clip.stop();
        }
    }

    /**
     * stops, drains then closes the selected audio-clip.
     */
    public void close(){
        stop();
        clip.drain();
        clip.close();
    }

    /**
     * sets LOOP_CONTINOUSLY to true and calls clip.PlayOce()
     */
    public void playLoop(){
        clip.loop(clip.LOOP_CONTINUOUSLY);
        playOnce();
    }

    /**
     * Sets the volum of selected audio-clip through decibel. e.g. -30 to lower the amount
     * @param decibel
     */
    public void setVolume(float decibel){
        gainControl.setValue(decibel);
    }

    /**
     * Checks if the audio-clip is running
     * @return true OR false
     */
    public boolean isRunning(){
        return clip.isRunning();
    }

    /**
     * Checks if spital is true OR false
     * @return true OR false
     */
    public boolean isSpital() {
        return spital;
    }

    /**
     * Sets the boolean value of spital
     * @param source
     */
    public void setSpital(GameObject source) {
        this.spital = true;
        this.source = source;
    }
}
