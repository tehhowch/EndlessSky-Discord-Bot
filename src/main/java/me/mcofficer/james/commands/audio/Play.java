package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.audio.Audio;

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
        //TODO: search
        audio.connect(event.getMember().getVoiceState().getChannel());
        audio.loadItem(event.getArgs(), event);
    }
}
