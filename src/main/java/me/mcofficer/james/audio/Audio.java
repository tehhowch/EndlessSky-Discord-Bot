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
import me.mcofficer.james.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import javax.annotation.CheckForNull;

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

    /** Connects to bot to a VoiceChannel. Silently fails if the bot is already connected.
     * @param voiceChannel
     */
    public void connect(VoiceChannel voiceChannel) {
        if (audioManager == null || (!audioManager.isConnected() && !audioManager.isAttemptingToConnect())) {
            audioManager = voiceChannel.getGuild().getAudioManager();
            audioManager.openAudioConnection(voiceChannel);
            audioManager.setSendingHandler(audioPlayerSendHandler);
        }
    }

    /**
     * Stops Playback, clears the Queue and disconnects from the VoiceChannel.
     */
    public void stopAndDisconnect() {
        trackScheduler.stop();
        audioManager.closeAudioConnection();
    }

    /**
     * Attempts to load a track/playlist that matches the identifier.
     * If successful, enqueues the item and calls {@link #announceTrack(AudioTrack, CommandEvent)}
     * or {@link #announcePlaylist(AudioPlaylist, CommandEvent)} respectively.
     * @param identifier
     * @param event
     */
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

    /**
     * @param guild
     * @return an EmbedBuilder with title and color set.
     */
    private EmbedBuilder createEmbedTemplate(Guild guild) {
        return new EmbedBuilder()
                .setTitle("Audio-Player", James.GITHUB_URL)
                .setColor(guild.getSelfMember().getColor());
    }

    /**
     * Announces that a new Track has been enqueued.
     * @param track
     * @param event
     */
    private void announceTrack(AudioTrack track, CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("Queueing `")
                .appendDescription(track.getInfo().title)
                .appendDescription("` (requested by ")
                .appendDescription(event.getMember().getAsMention());
        event.reply(embedBuilder.build());
    }

    /**
     * Announces that a new Playlist has been enqueued.
     * @param playlist
     * @param event
     */
    private void announcePlaylist(AudioPlaylist playlist, CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("Queueing Playlist`")
                .appendDescription(playlist.getName())
                .appendDescription("`")
                .appendDescription("(")
                .appendDescription((String.valueOf(playlist.getTracks().size())))
                .appendDescription(" tracks, requested by ")
                .appendDescription(event.getMember().getAsMention())
                .appendDescription(")");
        event.reply(embedBuilder.build());
    }

    /** Skips the currently playing Track and announces it.
     * @param event
     */
    public void skip(CommandEvent event) {
        announceSkip(event);
        trackScheduler.skip();
    }

    private void announceSkip(CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("Skipped Track `")
                .appendDescription(player.getPlayingTrack().getInfo().title)
                .appendDescription("`, requested by ")
                .appendDescription(event.getMember().getAsMention())
                .appendDescription(")");
        event.reply(embedBuilder.build());
    }

    /**
     * @return the VoiceChannel the bot is connected to, or null if it's not connected at all.
     */
    @CheckForNull
    public VoiceChannel getVoiceChannel() {
        return audioManager.getConnectedChannel();
    }

    public void shuffle(CommandEvent event) {
        trackScheduler.shuffle();
        announceShuffle(event);
    }

    private void announceShuffle(CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("The Queue has been shuffled by ")
                .appendDescription(event.getMember().getAsMention());
        event.reply(embedBuilder.build());
    }

    /**
     * @return The currently playing AudioTrack or null.
     */
    @CheckForNull
    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    /** Announces the currently playing Track.
     * @param event
     */
    public void announceCurrentTrack(CommandEvent event) {
        AudioTrack track = player.getPlayingTrack();
        if (track == null)
            event.reply("Not playing anything!");
        else {
            EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                    .appendDescription("**Playing:** ")
                    .appendDescription(track.getInfo().title)
                    .appendDescription(" [\uD83D\uDD17](")
                    .appendDescription(track.getInfo().uri)
                    .appendDescription(")\n**Time:** [")
                    .appendDescription(Util.MilisToTimestring(track.getPosition()))
                    .appendDescription(" / ")
                    .appendDescription(Util.MilisToTimestring(track.getDuration()))
                    .appendDescription("]");
            event.reply(embedBuilder.build());
        }
    }
}
