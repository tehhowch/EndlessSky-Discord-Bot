package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.audio.Audio;

public class Queue extends Command {

    private final Audio audio;

    public Queue(Audio audio) {
        name = "queue";
        help = "Displays the current Queue.";
        category = James.audio;
        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (audio.getVoiceChannel() != null && event.getMember().getVoiceState().getChannel().equals(audio.getVoiceChannel())) {
            audio.createQueueEmbed(event);
        }
    }
}
