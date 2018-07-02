package me.mcofficer.james.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.mcofficer.james.James;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
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

    public void connect(VoiceChannel voiceChannel) {
        if (audioManager == null || (!audioManager.isConnected() && !audioManager.isAttemptingToConnect())) {
            audioManager = voiceChannel.getGuild().getAudioManager();
            audioManager.openAudioConnection(voiceChannel);
            audioManager.setSendingHandler(audioPlayerSendHandler);
        }
    }

    public void stopAndDisconnect() {
        trackScheduler.stop();
        audioManager.closeAudioConnection();
    }

    public void loadItem(String identifier, CommandEvent event) {
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.enqueue(track);
                announceTrack(track, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack track = playlist.getTracks().get(0);
                    trackScheduler.enqueue(track);
                    announceTrack(track, event);
                }
                else {
                    playlist.getTracks().forEach(trackScheduler::enqueue);
                    announcePlaylist(playlist, event);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    private EmbedBuilder createEmbedTemplate(Guild guild) {
        return new EmbedBuilder()
                .setTitle("Audio-Player", James.GITHUB_URL)
                .setColor(guild.getSelfMember().getColor());
    }

    private void announceTrack(AudioTrack track, CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("Queueing `")
                .appendDescription(track.getInfo().title)
                .appendDescription("` (requested by `")
                .appendDescription(event.getMember().getEffectiveName())
                .appendDescription("`)");
        event.reply(embedBuilder.build());
    }

    private void announcePlaylist(AudioPlaylist playlist, CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("Queueing Playlist`")
                .appendDescription(playlist.getName())
                .appendDescription("`")
                .appendDescription("(")
                .appendDescription((String.valueOf(playlist.getTracks().size())))
                .appendDescription(" tracks, requested by `")
                .appendDescription(event.getMember().getEffectiveName())
                .appendDescription("`)");
        event.reply(embedBuilder.build());
    }
}
