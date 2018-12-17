package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.Util;
import me.mcofficer.james.audio.Audio;
import me.mcofficer.james.audio.Playlists;
import net.dv8tion.jda.core.EmbedBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Playlist extends Command {

    private Audio audio;
    private Playlists playlists;

    public Playlist(Audio audio, Playlists playlists) {
        name = "playlist";
        help = "Saves URLs as playlists, to be quickly accessible. Each playlist is associated with a key.\n" +
                "`-playlist X` plays a playlist X.\n" +
                "`-playlist list` shows all available playlists.\n" +
                "`-playlist save X U` saves the URL U under the key X.\n" +
                "`-playlist delete X` deletes the playlist X, if you are the owner of X.\n" +
                "`-playlist edit X U` updates the playlist X with the URL U.\n" +
                "`-playlist info X` Shows the URL and Owner of the playlist X.\n";
        category = James.audio;

        this.audio = audio;
        this.playlists = playlists;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().length() == 0)
            return;

        String[] args = event.getArgs().split(" ");
        String arg = args[0];
        String[] revisedArgs = Arrays.copyOfRange(args, 1, args.length);

        try {
            if (arg.equals("list"))
                list(event);
            else if (arg.equals("save"))
                save(event, revisedArgs);
            else if (arg.equals("delete"))
                delete(event, revisedArgs);
            else if (arg.equals("edit"))
                edit(event, revisedArgs);
            else if (arg.equals("info"))
                info(event, revisedArgs);
            else
                play(event);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void play(CommandEvent event) throws IOException {
        audio.connect(event.getMember().getVoiceState().getChannel());
        audio.loadItem(playlists.getPlaylistUrl(event.getArgs()), event);
    }

    private void list(CommandEvent event) throws IOException {
        List<String> keys = playlists.getKeys();
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : keys)
            stringBuilder.append(String.format("\n\u2022 `%s`\n", key));

        EmbedBuilder embedBuider = new EmbedBuilder()
                .setTitle("EndlessSky-Discord-Bot", James.GITHUB_URL)
                .setColor(event.getGuild().getSelfMember().getColor())
                .setDescription(String.format("Playlists: %s\n%s", keys.size(), stringBuilder.toString()));
        event.reply(embedBuider.build());
    }

    private void save(CommandEvent event, String[] args) throws IOException {
        if (playlists.keyExists(args[0]))
            event.reply("A playlist with key `" + args[0] + "` already exists!");
        else {
            playlists.addPlaylist(args[0], args[1], event.getAuthor().getId());
            event.reply("Saved playlist as `" + args[0] + "`");
        }
    }

    private void delete(CommandEvent event, String[] args) throws IOException {
        if (!playlists.isOwner(args[0], event.getAuthor().getId()))
            event.reply("You're not the Owner of this playlist!");
        else {
            playlists.removePlaylist(args[0]);
            event.reply("Playlist `" + args[0] + "` has been removed.");
        }
    }

    private void edit(CommandEvent event, String[] args) throws IOException {
        if (!playlists.isOwner(args[0], event.getAuthor().getId()))
            event.reply("You're not the Owner of this playlist!");
        else {
            playlists.changePlaylistUrl(args[0], args[1]);
            event.reply("Playlist `" + args[0] + "` has been edited.");
        }
    }

    private void info(CommandEvent event, String[] args) throws IOException {
        Map.Entry<String, String> info = playlists.getPlaylistInfo(args[0]);
        event.getJDA().retrieveUserById(info.getValue()).queue(user -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("EndlessSky-Discord-Bot", James.GITHUB_URL)
                    .setColor(event.getGuild().getSelfMember().getColor())
                    .setDescription(String.format("Key: `%s`\nURL: %s\n Owner: %s", args[0], info.getKey(), user.getAsMention()));
            event.reply(embedBuilder.build());
        });
    }
}
