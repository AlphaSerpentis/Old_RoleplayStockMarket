package Discord_Bot.SCE_Bot;

import java.util.function.Consumer;

import commands.Commands;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class createMessage {

	public static void createMessage(Object[] dataSet, Object[] other) {
		
		User objUser = (User) dataSet[0];
		MessageChannel objMsgCh = (MessageChannel) dataSet[1];
		final Message objMsg = (Message) dataSet[2];
		final String newMsg = (String) dataSet[3];
		
		Object[] RReturn;
		
		if(dataSet[2] != null) { //Determines if the createMessage method was called that uses a custom message
			
			System.out.println("Checking for: " + objMsg.getContentRaw().toString().substring(1, objMsg.getContentRaw().length()));
			
			//Searches for a command
			RReturn = Commands.Commands(objMsg.getContentRaw().toString().substring(1, objMsg.getContentRaw().length()), objUser, objMsgCh);
			
			if(objMsg.getContentRaw().toString().indexOf(" ") == -1) {
				
			} else {
				String result = (String) RReturn[0]; //Whatever it returned
				
				if(result.matches("SKIP-EMBED!")) {
					System.out.println("Embedding instead.");
				} else {
					
					if((Boolean) RReturn[1]) {
						objUser.openPrivateChannel().complete().sendMessage((String) RReturn[0]).queue();
						
						objMsgCh.sendMessage("Check your DMs...").queue(new Consumer<Message>() {
							public void accept(Message msg) {
								try {
									Thread.sleep(10000);
									//Cannot delete a Dm'd message
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								msg.delete().queue();
							}
						});
					} else {
						objMsgCh.sendMessage((String) RReturn[0]).queue();
					}
				}
			}
			
		} else {
			
			
			
		}
		
	}
	
}
