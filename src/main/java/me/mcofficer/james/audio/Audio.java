package me.mcofficer.james.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
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
import java.util.ArrayList;
import java.util.LinkedList;

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
                .appendDescription("` [\uD83D\uDD17](")
                .appendDescription(track.getInfo().uri)
                .appendDescription(") (requested by ")
                .appendDescription(event.getMember().getAsMention())
                .appendDescription(")")
                .setThumbnail(getThumbnail(track));
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
                    .appendDescription("]")
                    .setThumbnail(getThumbnail(track));
            event.reply(embedBuilder.build());
        }
    }

    /** Pauses playback and announces it.
     * @param event
     */
    public void pause(CommandEvent event) {
        player.setPaused(true);
        announcePause(event);
    }

    private void announcePause(CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("The Audio Player has been paused.\n(requested by ")
                .appendDescription(event.getMember().getAsMention())
                .appendDescription("`)");
        event.reply(embedBuilder.build());
    }

    /** Pauses playback and announces it.
     * @param event
     */
    public void unpause(CommandEvent event) {
        if (player.isPaused()) {
            player.setPaused(false);
            announceUnpause(event);
        }
    }

    private void announceUnpause(CommandEvent event) {
        EmbedBuilder embedBuilder = createEmbedTemplate(event.getGuild())
                .appendDescription("The Audio Player has been unpaused.\n(requested by ")
                .appendDescription(event.getMember().getAsMention())
                .appendDescription("`)");
        event.reply(embedBuilder.build());
    }

    /**
     * @param event
     */
    public void createQueueEmbed(CommandEvent event) {
        LinkedList<AudioTrack> queue = trackScheduler.getQueue();

        if(queue.isEmpty())
            event.reply("The Queue is empty!");
        else {
            ArrayList<String> items = new ArrayList<>();
            long queueLength = 0;
            for(AudioTrack track : queue){
                items.add(String.format("`[%s]` %s", Util.MilisToTimestring(track.getDuration()), track.getInfo().title));
                queueLength += track.getDuration();
            }

            new Paginator.Builder()
                    .setText(String.format("Showing %s Tracks. \n Total Queue Time Length: %s", queue.size(), Util.MilisToTimestring(queueLength)))
                    .setItems(items.toArray(new String[0]))
                    .setItemsPerPage(10)
                    .setEventWaiter(James.eventWaiter)
                    .setColor(event.getGuild().getSelfMember().getColor())
                    .useNumberedItems(true)
                    .waitOnSinglePage(true)
                    .setBulkSkipNumber(items.size() > 50 ? 5 : 0) // Only show bulk skip buttons when the Queue is sufficiently large
                    .build()
                    .display(event.getChannel());
        }
    }

    /** Attempts to find a Thumbnail URL for the AudioTrack track.
     * If nothing is found (or the service is not supported), returns the default "Playing" Icon.
     * @param track An AudioTrack.
     * @return A valid Thumbnail URL.
     */
    public String getThumbnail(AudioTrack track) {
        String url = null; // Util.getHttpStatus will catch it

        if (track.getInfo().uri.startsWith("https://www.youtube.com/watch?v=")) // lavaplayer *always* constructs YT URLs like this
            url = "http://i1.ytimg.com/vi/" + track.getIdentifier() + "/0.jpg";
        else if (track.getInfo().uri.startsWith("https://soundcloud.com/"))
            url = getSoundcloudThumbnail(track.getInfo().uri);

        if (Util.getHttpStatus(url) == 200)
            return url;
        return James.GITHUB_RAW_URL + "thumbnails/play.png";
    }

    /** Not the most reliable method, but i doesn't *have* to work, and i don't want to depend on yet another API.
     * @param trackUrl The URL of a Soundcloud track.
     * @return A Soundcloud Thumbnail URL (500x500) or null.
     */
    @CheckForNull
    private String getSoundcloudThumbnail(String trackUrl){
        String html = Util.getContentFromUrl(trackUrl);
        int pos = html.indexOf("\"artwork_url\":") + 15;
        try{
            String artwork_url = html.substring(pos, html.indexOf("-large.jpg\"", pos) + 10);
            if(artwork_url.contains("-large.png"))
                artwork_url = html.substring(pos, html.indexOf("-large.png\"", pos) + 10);
            return artwork_url;
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
}
