package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.audio.Audio;

public class Stop extends Command {

    private final Audio audio;

    public Stop(Audio audio) {
        name = "stop";
        help = "Stops playback and disconnects from the VoiceChannel";
        arguments = "<query>";

        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        audio.stopAndDisconnect();
    }
}
