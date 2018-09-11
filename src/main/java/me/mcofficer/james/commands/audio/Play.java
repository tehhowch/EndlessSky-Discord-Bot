package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.audio.Audio;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Play extends Command {

    private final Audio audio;

    public Play(Audio audio) {
        name = "play";
        help = "Plays a track by it's url, or searches for it by a query";
        arguments = "<query>";
        category = James.audio;
        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel() ||
                event.getMember().getVoiceState().getChannel().equals(audio.getVoiceChannel())) {
            String query = event.getArgs();
            try {
                URL url = new URL(query);
                URLConnection conn = url.openConnection();
                conn.connect();
            } catch (IOException e) { // URL is invalid or unreachable
                query = "ytsearch:" + query;
            }

            audio.connect(event.getMember().getVoiceState().getChannel());
            audio.loadItem(query, event);
        }
    }
}
