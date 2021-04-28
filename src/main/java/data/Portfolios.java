package data;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import Discord_Bot.SCE_Bot.App;
import Discord_Bot.SCE_Bot.Ref;
import commands.Commands;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Portfolios extends Commands {

	@SuppressWarnings("unchecked")
	public static String writeToPortfolio(String test, MessageChannel chn, User usr) throws IOException {
		
		String result = "**Failed to commit action! Please try again later or contact AlphaSerpentis#3203!**";
		MessageChannel pending = App.hmmm.getTextChannelById("521815565749452801");
		boolean abort = false;
		
		JSONArray tempBackup = Orderbook.getInfo(true);
		JSONArray thePort = tempBackup;
		
		Object[] items = new Object[2];
		
		for(int i = 0; i < thePort.size(); i++) {
			
			JSONObject jObj = (JSONObject) thePort.get(i);
			
			if(jObj.get("id").toString().matches(usr.getId())) {
				
				JSONArray pendingTran;
				
				if(jObj.get("pending") == null) {
					pendingTran = new JSONArray();
					jObj.put("pending", pendingTran);
				} else
					pendingTran = (JSONArray) jObj.get("pending");
				
				FileWriter file = null;
				
				try {
					HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();
					JSONObject objX = new JSONObject();
					
					items[0] = test.substring(test.indexOf(" ")+1, test.indexOf("\"")-1);
					items[1] = test.substring(test.indexOf("\"")+1, test.lastIndexOf("\""));
					
					System.out.println(pendingTran.size());
					System.out.println(items[0] + " || " + items[1]);
					
					
					objX.put("value", Double.parseDouble((String) items[0]));
					objX.put("name", items[1]);
					map.put("pending", objX);
					pendingTran.add(map.get("pending"));
					
					file = new FileWriter("Portfolios.json");
					
					file.write(thePort.toJSONString());
					
				} catch(IOException e) {
					e.printStackTrace();
					abort = true;
				} catch(Exception e) {
					e.printStackTrace();
					abort = true;
				} finally {
					
					if(abort) {
						file = new FileWriter("Portfolios.json");
						
						file.write(tempBackup.toJSONString());
						result = "Failure contained! Attempting to overwrite incorrect data!";
					} else {
						result = "Successfully processed transaction! The deposit will be in a queue for approval, please be patient.";
						Object[] sendThis = new Object[4];
						sendThis[0] = usr;sendThis[1] = App.hmmm.getTextChannelById(Ref.transPen);sendThis[3] = usr.getName() + " has requested for a transaction from " + items[1] + " with a value of " + items[0];
						Object[] temporaryResp = new Object[4];
						temporaryResp[0] = "OUTSIDE-DO NOT DELETE";
						Orderbook.createMessageO(sendThis, temporaryResp); 
					}
					
					System.out.println("Flushing and closing..");
					file.flush();
					file.close();
					
					//break;
				}
				
			}
			
		}
		
		return result;		
		
	}
	
	public static String portCommand(String test, MessageChannel chn, User usr) {
		
		String result = "**ERROR 404:** You don't have an investment portfolio! Please type >investor to create one (soon)!";
		
		JSONArray thePort = Orderbook.getInfo(true);
		for(int i = 0; i < thePort.size(); i++) {
		
			JSONObject jObj = (JSONObject) thePort.get(i);
			
			if(jObj.get("id").toString().matches(usr.getId())) {
				
				JSONArray someArr = (JSONArray) jObj.get("owned");
				
				double accumVal = (Double) jObj.get("deposited");
				
				DecimalFormat dF = new DecimalFormat("#.###");
				dF.setGroupingSize(3);
				dF.setGroupingUsed(true);
				
				result = "**BASIC INFO**\n\nBuying Power: $accumVal\nDeposited: $DInput\n\nHoldings: **You have no current holdings!**\n";
				
				for(int Z = 0; Z < someArr.size(); Z++) {
					
					JSONObject innerObj = (JSONObject) someArr.get(Z);
					JSONArray deepArr = (JSONArray) innerObj.get("orders");
					
					long shares = 0;
					double avgPrice = 0;
					
					for(int Y = 0; Y < deepArr.size(); Y++) {
						
						JSONObject deepObj = (JSONObject) deepArr.get(Y);
						avgPrice = (avgPrice + (Double) deepObj.get("price"));
						
						if(!(Boolean) innerObj.get("exempt"))
							accumVal = accumVal - ((Double) deepObj.get("price") * (Long) deepObj.get("shares"));
						
						if(Y+1==deepArr.size())
							avgPrice = avgPrice/deepArr.size();
						
						System.out.println(deepArr.size() + " || " + Y);
						shares = (Long) deepObj.get("shares") + shares;
						
					}
					
					if(result.indexOf("**You have no current holdings!**") != -1)
						result = result.replace("**You have no current holdings!**", "");
					
					result = result + "**$" + innerObj.get("ticker") + "** with **" + shares + " shares** @ an estimated average price of $" + avgPrice + "/share\n\n";
					
					System.out.println(innerObj);
					
				}
				
				result = result.replaceFirst("accumVal", String.valueOf(dF.format(accumVal)));
				result = result.replaceFirst("DInput", String.valueOf(dF.format(jObj.get("deposited"))));
				
				break;
			} else {
				// write portfolio 
			}
			
		}
		
		return result;
		
	}
	
}
