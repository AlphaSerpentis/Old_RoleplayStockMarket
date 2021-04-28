package Discord_Bot.SCE_Bot;

import commands.Commands;
import customEvents.listenerForOrd;
import data.Orderbook;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class App extends ListenerAdapter
{
	
	public static JDA hmmm;
	
	private void shutdown(JDA jda) {
		
		jda.shutdown();
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
		System.exit(0);
		
	}
	
    public static void main( String[] args ) throws Exception
    {
       
    	System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
    	
       JDA test = new JDABuilder(Ref.token).build();
       test.getPresence().setGame(Game.playing("Trying to execute orders."));
       //JDA jda = new JDABuilder(AccountType.BOT).setToken(Ref.token).build();
       test.addEventListener(new App());
       test.addEventListener(new listenerForOrd());
       //test.addEventListener(new Orderbook());
       
       hmmm = test;
       
    }
    
    public void onMessageReceived(MessageReceivedEvent evt) {
    	
    	if(!evt.getAuthor().isBot()) {
    	
    		//Objects
        	Object[] setOfData = {
        			(User) evt.getAuthor(), (MessageChannel) evt.getChannel(), (Message) evt.getMessage(), ""
        			};
        	
        	//Commands
        	
        	System.out.println(((Message) setOfData[2]).getContentRaw().toLowerCase().indexOf(Ref.prefix));
        	if(((Message) setOfData[2]).getContentRaw().toLowerCase().indexOf(Ref.prefix) == 0) {
        		createMessage.createMessage(setOfData, null);
        	};
        	
        	if(((Message) setOfData[2]).getContentRaw().equalsIgnoreCase(Ref.prefix+"shutdown") && ((Message) setOfData[2]).getAuthor().getId().matches(Ref.crtrID)) {
        		((MessageChannel) setOfData[1]).sendMessage("Bot terminated.").queue();
        		shutdown(evt.getJDA());
        	};
    		
        	if(((MessageChannel) setOfData[1]).getId().matches(Ref.testPit)) {
        		
        		Orderbook.processThis(setOfData);
        		//System.out.println("Nice");
        		
        	}
    	} else if(evt.getAuthor().isBot() && evt.getAuthor().getId() == "512398175106236431") {
    		
    	}
    	
    }
    
    public void onGuildMemberJoin(GuildMemberJoinEvent evt) {
    	
    	User objUser = evt.getUser();
    	MessageChannel objMsgCh = evt.getUser().openPrivateChannel().complete();
    	
    	System.out.println("User Joined Server.");
    	objMsgCh.sendMessage("Hello, and welcome to the Santa Cruz Exchange Corporation! Please read the documents provided in the server under #important-documents.\n\nMadelyn A. (AlphaSerpentis#3203), owner of the SCE, will be willing to help you. If you have any questions, DM or ping AlphaSerpentis#3203! Thanks for joining the SCE.\n\n**To obtain the \"Investor\" role, type in >investor").queue();
    	
    }
    
}
