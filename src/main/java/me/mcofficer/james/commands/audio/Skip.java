package me.mcofficer.james.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.mcofficer.james.James;
import me.mcofficer.james.audio.Audio;

public class Skip extends Command {
    private final Audio audio;

    public Skip(Audio audio) {
        name = "skip";
        help = "Skips X songs (defaults to 1).";
        arguments = "X";
        category = James.audio;
        this.aliases = new String[]{"next"};
        this.audio = audio;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (audio.getVoiceChannel() != null
                && event.getMember().getVoiceState().getChannel().equals(audio.getVoiceChannel())
                && audio.getPlayingTrack() == null) {
            int amount = 1;
            try {
                amount = Integer.parseInt(event.getArgs());
            }
            catch (NumberFormatException e) {}
            audio.skip(event, amount);
        }
    }
}
