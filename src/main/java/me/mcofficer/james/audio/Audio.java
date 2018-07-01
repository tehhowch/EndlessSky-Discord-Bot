package me.mcofficer.james.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class Audio {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final AudioPlayer player;
    private final AudioPlayerSendHandler audioPlayerSendHandler;
    private final TrackScheduler trackScheduler;
    private AudioManager audioManager;

    public Audio() {
        AudioSourceManagers.registerRemoteSources(playerManager);
        player = playerManager.createPlayer();

        trackScheduler = new TrackScheduler(player);
        audioPlayerSendHandler = new AudioPlayerSendHandler(player);
    }

    public void loadAndConnect(String identifier, VoiceChannel channel) {
        if (audioManager == null || (!audioManager.isConnected() && !audioManager.isAttemptingToConnect())) {
            audioManager = channel.getGuild().getAudioManager();
            audioManager.openAudioConnection(channel);
            audioManager.setSendingHandler(audioPlayerSendHandler);
        }
        loadItem(identifier);
    }

    public void stopAndDisconnect() {
        trackScheduler.stop();
        audioManager.closeAudioConnection();
    }

    private void loadItem(String identifier) {
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.enqueue(track);
                System.out.println("playing " + track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }
}
