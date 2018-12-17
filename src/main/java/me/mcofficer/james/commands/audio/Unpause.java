package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.audio.Audio;

public class Unpause extends Command {

    private final Audio audio;

    public Unpause(Audio audio) {
        name = "unpause";
        help = "Resumes Playback";
        aliases = new String[]{"resume"};
        category = James.audio;
        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (audio.getVoiceChannel() != null && event.getMember().getVoiceState().getChannel().equals(audio.getVoiceChannel()))
            audio.unpause(event);
    }
}
