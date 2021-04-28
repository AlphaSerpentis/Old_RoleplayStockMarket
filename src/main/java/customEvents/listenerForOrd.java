package customEvents;

import java.util.ArrayList;
import java.util.List;

import data.Orderbook;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class listenerForOrd implements EventListener {
	
	private static List<Message> checkThese = new ArrayList<Message>();
	
	private List<theyReacted> listeners = new ArrayList<theyReacted>();
	
	public static void addIn(Message msg) {
		
		if(checkThese.size() > 10) {
			checkThese.remove(0);
		}
		
		checkThese.add(msg);
	}
	
	public static void checkFor(String id, Event event, String other) {
		for(Message sorted: checkThese) {
			messageLoop:
			System.out.println(sorted.getAuthor().getId() + " is the user ID");
			
			if(((GuildMessageReactionAddEvent) event).getUser().getId().matches(sorted.getAuthor().getId())) {
				
				for(Object[] result: Orderbook.whatever) {
					orderLoop:
					if(((String) result[0]).matches(sorted.getAuthor().getId())) {
						
						//System.out.println("Special one!");
						
						//System.out.println(other);
						
						if(other.indexOf("✔") != -1) {
							System.out.println("YES PLEASE");
							result[1] = true;
							break;
						}
						
					}
					
				}
				
			}
		}
	}

    public void addListener(theyReacted toAdd) {
        listeners.add(toAdd);
    }
	
	public void onEvent(Event event) {
		
		if(event instanceof GuildMessageReactionAddEvent) {
			
			System.out.println("Reaction clicked!");
			
			System.out.println(((GuildMessageReactionAddEvent) event).getReactionEmote().toString());
			
			checkFor(((GuildMessageReactionAddEvent) event).getUser().getId(), event, ((GuildMessageReactionAddEvent) event).getReactionEmote().toString());
			
			for(theyReacted tR: listeners)
				tR.someoneReacted();
		}
		
	}
	
}
