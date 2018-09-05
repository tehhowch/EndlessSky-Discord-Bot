package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.audio.Audio;

public class Shuffle extends Command {
    private final Audio audio;

    public Shuffle(Audio audio) {
        name = "shuffle";
        help = "Shuffles the AudioPlayer's queue.";
        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (audio.getVoiceChannel() != null && event.getMember().getVoiceState().getChannel().equals(audio.getVoiceChannel()))
            audio.shuffle(event);
    }
}
