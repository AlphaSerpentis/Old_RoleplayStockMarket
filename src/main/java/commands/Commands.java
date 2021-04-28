package commands;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Member;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.management.relation.Role;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Discord_Bot.SCE_Bot.App;
import Discord_Bot.SCE_Bot.Ref;
import data.Orderbook;
import data.Portfolios;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Commands  {
	
	public static void readPortfolio(Object[] data) {
		//TRANSFER SOME STUFF FROM PORTCOMMAND METHOD
	}
	
	public static String compCommand(String test, MessageChannel chn) {
		
		String result = "Cannot be found!";
		//The array structure to use for the embed
		Object[] embedDat = {"ERROR-TITLE", null, Color.cyan, "ERROR-DESC", "", "Market Status: " + Orderbook.status, chn};//Title, Color, Desc., setAuthor, setFooter, setImage, setThumbnail
		
		String searchTh = test.substring(test.indexOf(" ")+1, test.length()); //Goes one character ahead of the space and searches for the company.
		System.out.println(searchTh);
		JSONArray theOrderbook = Orderbook.getInfo(false); //Calls the getInfo method from the class Orderbook
		for(int i = 0; i < theOrderbook.size(); i++) { //Put it in a for loop so that it searches for the company.
			
			if(searchTh.isEmpty() || searchTh.toLowerCase().matches("company")) {
				result = "There was no input!";
				break;
				}
			
			JSONObject jObj = (JSONObject) theOrderbook.get(i); //During the for-loop-statement, it uses i to get the current object
			if(jObj.get("ticker").toString().toLowerCase().matches(searchTh.toLowerCase()) && (jObj.get("type").toString().matches("stock") || jObj.get("type").toString().matches("stock-delisted"))) {
				embedDat[0] = jObj.get("name").toString() + " - $" + jObj.get("ticker");
				
				DecimalFormat dF = new DecimalFormat("#.###");
				dF.setGroupingSize(3);
				dF.setGroupingUsed(true);
				
				double percent = ((Double) jObj.get("value")/(Double) jObj.get("lValue"))*100-100;
				String add = "+";
				if(percent < 0) {
					add = "-";
				}
				
				
				embedDat[3] = "Price: $" + jObj.get("value") + " (" + add + dF.format(percent) +"%)" + "\nVolume: " + dF.format(jObj.get("lastVol")) + "\nMarket Capitalization: $" + dF.format(jObj.get("marketCap")) + "\n\n" + jObj.get("info");
				
				if((Double) jObj.get("lValue") - (Double) jObj.get("value") == 0) {
					embedDat[2] = Color.gray;
				} else if((Double) jObj.get("lValue") > (Double) jObj.get("value")) {
					embedDat[2] = Color.red;
				} else {
					embedDat[2] = Color.green;
				}
				
				for(Object M: (JSONArray) jObj.get("boardMembs")) {
					JSONObject applyThis = (JSONObject) M;
					embedDat[4] = (String) embedDat[4] + applyThis.get("pos") + ": " + applyThis.get("name")+"\n";
				}
			}
		}
		if(((String) embedDat[0]).matches("ERROR-TITLE")) {
			return result;
		} else {
			embedMessage(embedDat);
			return "SKIP-EMBED!";
		}
		
	}
	
	public static Object[] Commands(String test, User usr, MessageChannel chn) {
		
		JDA shortJ = App.hmmm;
		Guild gld = Ref.thisJDA.getGuildById(Ref.srvrID);
		
		Object[] nice = {"?????", false};
		
		System.out.println(usr.getId().matches(Ref.crtrID));
		
		if(test.matches("about")) {
			nice[0] = "The SCE Assistant Bot is meant to help save time in executing trades and be able to match orders faster than it is by doing it from hand (typing on a spreadsheet).\n\nThis bot is developed by AlphaSerpentis#3203";
			return nice;
		} else if(test.matches("investor") || test.matches("investors")) {
			
			boolean hasRole = false;
			
			nice[0] = "ERROR #8: Contact AlphaSerpentis#3203 about this.";
			
			for(net.dv8tion.jda.core.entities.Role r: gld.getMember(usr).getRoles()) {
				hasRole = true;
				System.out.println(r);
				if(r.getId().matches("492431979288068102")) {
					nice[0] = "You already have the 'Investors' role!";
				} else {
					gld.getController().addSingleRoleToMember(gld.getMember(usr), gld.getRoleById("492431979288068102")).submit();
					 nice[0] = "Your investment portfolio will be created soon. To get it checked, ping AlphaSerpentis#3203 to verify. **(Not implemented at this time).**";
				}
			}
			
			if(!hasRole) {
				gld.getController().addSingleRoleToMember(gld.getMember(usr), gld.getRoleById("492431979288068102")).submit();
				nice[0] = "Your investment portfolio will be created soon. To get it checked, ping AlphaSerpentis#3203 to verify. **(Not implemented at this time).**";
			}
	
			return nice;
	
		} else if(test.matches("help")) {
			nice[0] = "`>about` - Inform users about what this bot is.\n`>help` - List of commands.\n`>investor` - Give a user the Investor role and generate an investment portfolio.\n`>portfolio` - DMs your portfolio information.\n`>company [TICKER]` - Displays information about a company.";
			return nice;
		} else if(test.matches("shutdown") || test.matches("sd") && usr.getId().matches(Ref.crtrID)) {
			nice[0] = "Terminating program.";
			return nice;
		} else if(test.matches("portfolio") || (test.matches("pf"))) {
			nice[0] = Portfolios.portCommand(test, chn, usr);
			nice[1] = true;
			return nice;
		} else if(test.matches("owo") && usr.getId().matches(Ref.crtrID)) {
			nice[0] = "**OwO** What's this?\nx3 *nuzzles* OwO";
			return nice;
		} else if(test.matches("test") && usr.getId().matches(Ref.crtrID)) {
			//Orderbook.getInfo(null, null);
			
			return nice;
		} else {
			
			if(test.indexOf("company") != -1) {
				if(test.indexOf(" ") != test.lastIndexOf(" ")) {
					nice[0] = "Malformed formatting.";
					return nice;
				} else {
					nice[0] = compCommand(test, chn);
					return nice;
				}
			} else if(test.indexOf("deposit") != -1) {
			
				if(test.indexOf(" ") == -1 || test.indexOf(" ") == test.lastIndexOf(" ")) {
					nice[0] = "`>deposit [x amount of cash] \"[Bank (Name)/Cash]\"`\n\nExample:\n>deposit 100000.50 \"The Dynamic Bank\"";
					return nice;
				} else {
					try {
						nice[0] = Portfolios.writeToPortfolio(test, chn, usr);
					} catch (IOException e) {
						e.printStackTrace();
						nice[0] = e.getMessage();
					}
					return nice;
				}
				
			} else {
				return nice;
			}
		}
		
	}
	
	public static void embedMessage(Object[] dataSet) {
		
		EmbedBuilder eb = new EmbedBuilder();
		//eb.setTitle((String) dataSet[4]);
		eb.setColor((Color) dataSet[2]);
		eb.setDescription((String) dataSet[3]);
		eb.setAuthor((String) dataSet[0]);
		eb.addField("Board of Directors", (String) dataSet[4], true);
		eb.setFooter((String) dataSet[5], "https://cdn.discordapp.com/avatars/512398175106236431/e1c73e0821df7410944855f31a6a00ed.png?size=128");
		
		MessageChannel send = (MessageChannel) dataSet[6];
		send.sendMessage(eb.build()).queue();
		
	}
}
