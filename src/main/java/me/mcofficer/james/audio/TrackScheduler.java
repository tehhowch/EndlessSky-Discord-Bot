package me.mcofficer.james.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collections;
import java.util.LinkedList;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final LinkedList<AudioTrack> queue = new LinkedList<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        player.addListener(this);
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public void skip() {
        play(queue.poll());
    }

    public void play(AudioTrack track) {
        player.playTrack(track);
    }

    public void enqueue(AudioTrack track) {
        if(!player.startTrack(track, true)) //something is currently playing
            queue.offer(track);
    }

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
    }

}
