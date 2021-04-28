package data;

import net.dv8tion.jda.core.JDA;


import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory.MessageRetrieveAction;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.Route.Emotes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import Discord_Bot.SCE_Bot.Ref;
import customEvents.listenerForOrd;


public class Orderbook {
	
	public static String status = "Closed";
	public static Object[][] whatever = {{"404", false, false},{"404", false, false},{"404", false, false},{"404", false, false},{"404", false, false}}; //ID, Order Confirmed?, Active

	Guild gld = Ref.thisJDA.getGuildById(Ref.srvrID);
	
	public static void writeToOrderbook(Object[] data) {
		
		
	
	}
	
	public static void onMarketUpdate() {
		
		//Check if any orders could be matched
		
		
	}
	
	public static void processOrd() {
		
	}
	
	public static JSONArray getInfo(boolean getPort) {
		
		JSONArray tryToRet = null;
		
		JSONParser parser = new JSONParser();
		JSONObject jObj = null;
		
		try {
			
			FileReader reader;
			
			if(!getPort) {
				reader = new FileReader("Orderbook.json");
			} else {
				reader = new FileReader("Portfolios.json");
			}
			
			JSONArray jArr = (JSONArray) parser.parse(reader);
				
			jObj = (JSONObject) jArr.get(0);
			System.out.println(jObj.get("name"));
			tryToRet = jArr;
			//System.out.println(jArr);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return tryToRet;
	}
	
	public static void processThis(Object[] dataSet) {
		
		boolean contLoop = true;
		
		Object[] thisOur = dataSet;
		Object[] justInCase = new Object[10];
		
		User objUser = (User) dataSet[0];
		MessageChannel objMsgCh = (MessageChannel) dataSet[1];
		Message objMsg = (Message) dataSet[2];
		
		String test = objMsg.getContentRaw().toString();
		Object[] theOrder = {
				"DEFAULT", 0.00, "$ERROR", 404, null
				
		};
		
		if(test.indexOf("$") != test.lastIndexOf("$")) {
			
			test = "FAILED";
			createMessageO(dataSet, null);
			
		}
		
		//System.out.println(objMsg.getContentRaw().substring(objMsg.getContentRaw().toString().indexOf("$"), objMsg.getContentRaw().toString().length()));
		
		int posVal = 0;
		int addThis = 0;
		
		int count = 0;
		
		while(contLoop && !test.matches("FAILED")) {
			
			if(test.indexOf("/") == -1) {
				break;
			}
			
			String piece = test.substring(posVal, test.length());
			String useThis = "";
			
			posVal = posVal + piece.indexOf("/")+1;
			addThis = addThis + piece.length();
			
			//System.out.println(posVal + " " + addThis + " " + test.lastIndexOf("/"));
			if(piece.indexOf("/") != -1) {
				
				useThis = test.substring(test.indexOf(piece), posVal-1);
				//System.out.println(test.substring(test.indexOf(piece), posVal-1));
				if(count == 2) {
					JSONArray OrdBk = Orderbook.getInfo(false);
					for(int i = 0; i < OrdBk.size(); i++) {
						
						dataSet[3] = "Ticker/Company doesn't exist!";
						
						JSONObject jObj = (JSONObject) OrdBk.get(i);
						System.out.println(useThis.replace("$",  "") + " || " + jObj.get("ticker").toString());
						System.out.println(jObj.get("ticker").toString().toLowerCase().matches(useThis.replace("$",  "").toLowerCase()));
						if(jObj.get("ticker").toString().toLowerCase().matches(useThis.replace("$",  "").toLowerCase()) && (jObj.get("type").toString().matches("stock") || jObj.get("type").toString().matches("futures"))) {
							dataSet[3] = "";
							System.out.println("It exists alright!");
							break;
						} else if(jObj.get("ticker").toString().toLowerCase().matches(useThis.replace("$",  "").toLowerCase()) && jObj.get("type").toString().matches("stock-delisted")) {
							dataSet[3] = "The company has been delisted from the SCEC. If you have previously owned shares of " + jObj.get("ticker").toString() + ", contact AlphaSerpentis#3203.";
							break;
						}
						
					}
					
					if(dataSet[3].toString().matches("Ticker/Company doesn't exist!") || dataSet[3].toString().indexOf("The company has been delisted from the SCEC.") != -1) {
						justInCase[0] = "FAILED";
						createMessageO(dataSet, justInCase);
						contLoop = false;
						break;
					}
				}
				
				System.out.println("PIECE: " + piece);
				
			} else {
				useThis = piece;
				
				contLoop = false;
				
			}
			theOrder[count++] = useThis;
			
			if(count == 2 || contLoop == false) {
				try {
					Double.parseDouble((String) theOrder[count-1]);
					
				} catch (Exception e) {
					System.out.println("oopsie woopsie");
					if(((String) theOrder[count-1]).indexOf(",") != -1) {
						
						try {
							System.out.println("CHECK");
							Double.parseDouble(theOrder[count-1].toString().replaceAll(",", "."));
							theOrder[count-1] = theOrder[count-1].toString().replaceAll(",", ".");
						} catch (Exception e1) {
							System.out.println("oof.");
							test = "FAILED";
						}
						
					} else {
						test = "FAILED";
					}
	
				}
			}
			
			if(count > 5) {
				System.out.println("Infinite loop detected!");
				break;
			} 
			
			if(contLoop == false && !test.matches("FAILED")) {
				
				theOrder[4] = objUser;
				
				if(test.indexOf("-") != -1) {
					thisOur[3] = "**CONFIRM THIS ORDER** " + objUser.getAsMention() + "\n= = = = = = = = = = = = =\nYou are placing a **" + theOrder[0].toString().toUpperCase() + " Order** for **" + theOrder[3] + "** contracts @ the price of **$" + theOrder[1] + "/share** of **" + theOrder[2] + "**\n\n`React with a check, otherwise use the X (You have 10 seconds)`";
					createMessageO(dataSet, theOrder);
				} else {
					thisOur[3] = "**CONFIRM THIS ORDER** " + objUser.getAsMention() + "\n= = = = = = = = = = = = =\nYou are placing a **" + theOrder[0].toString().toUpperCase() + " Order** for **" + theOrder[3] + "** shares @ the price of **$" + theOrder[1] + "/share** of **" + theOrder[2] + "**\n\n`React with a check, otherwise use the X (You have 10 seconds)`";
					createMessageO(dataSet, theOrder);
				}
				break;
			} else if(contLoop == false || test.matches("FAILED")) {
				System.out.println("HERE! " + test);
				createMessageO(dataSet, justInCase);
				break;
			}
			
		}
		
	}
	
	public static void createMessageO(final Object[] dataSet, final Object[] other) {
		
		final User objUser = (User) dataSet[0];
		final MessageChannel objMsgCh = (MessageChannel) dataSet[1];
		final Message objMsg = (Message) dataSet[2];
		String customMsg = (String) dataSet[3];
		
		if(customMsg.isEmpty()) {
			objMsgCh.sendMessage("**Incorrect or Malformed Formatting!** \n\n(Remember to not use the $ on the price and only on the ticker/symbol!)\n\n`[MARKET/LIMIT] [BUY/SELL]/[PRICE]/$[TICKER]/[# OF SHARES]`").queue(new Consumer<Message>() {

				public void accept(Message msg) {
					
					try {
						Thread.sleep(10000);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			});
		} else {
			objMsgCh.sendMessage(customMsg).queue(new Consumer<Message>() {
				public void accept(Message customMsg) {
					
					if(!other[0].toString().matches("FAILED") && (other[0].toString().indexOf("OUTSIDE") == -1)) {
						boolean delete = true;
						
						customMsg.addReaction("\u274C").queue();customMsg.addReaction("\u2714").queue();
						
						try {
							
							boolean sent = false;
							
							for(Object[] WT: whatever) {
								if(!boolean.class.equals(WT[2])) {
									WT[0] = objUser.getId();WT[2] = true;
									sent = true;
									
									System.out.println("WT is now: " + WT[0]);
									break;
								}
							}
							
							//ADD IN A SPOT TO ALLOW FOR MORE PLACES TO BE HELD
							
							listenerForOrd.addIn(objMsg);
							
							Thread.sleep(10000);
							
							System.out.println("Alright Pinhead, your time is up.");
							customMsg.clearReactions().queue();
							
							boolean TheWhatever[] = new boolean[3];
							
							for(Object[] WT: whatever) {
								
								System.out.println(WT[0] + " || " + WT[1] + " || " + WT[2]);
								
								TheWhatever[1] = (Boolean) WT[1];
								TheWhatever[2] = (Boolean) WT[2];
								
								if(((String) WT[0]).matches(objUser.getId()) && TheWhatever[1] == false && TheWhatever[2] == true) {
									customMsg.editMessage("Your order has been cancelled.").queue();
									Thread.sleep(5000);
									customMsg.delete().queue();
									
									try {
										objMsg.delete().queue();
									} catch (Exception e) {
										
									}
									
									WT[0] = "404";WT[1] = false;WT[2] = false;
									
									break;
									
								} else if(((String) WT[0]).matches(objUser.getId()) && TheWhatever[1] == true && TheWhatever[2] == true) {
									customMsg.editMessage("Your order has been placed, and we'll DM you when it has been executed.").queue();
									Thread.sleep(5000);
									customMsg.delete().queue();
									
									try {
										objMsg.delete().queue();
									} catch (Exception e) {
										
									}
									
									WT[0] = "404";WT[1] = false;WT[2] = false;
									
									break;
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						if(other[0].toString().indexOf("DO NOT DELETE") == -1)
							try {
								Thread.sleep(10000);
								
								customMsg.delete().queue();
								
								try {
									objMsg.delete().queue();
								} catch (Exception e) {
									
								}
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
					}
					
				}
			});
			
		}
	}
	
}
