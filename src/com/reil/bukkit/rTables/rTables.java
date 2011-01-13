package com.reil.bukkit.rTables;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.reil.bukkit.rParser.MessageParser;

import org.bukkit.Color;
import org.bukkit.Player;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class rTables extends JavaPlugin {
	Server MCServer = getServer();
	protected static final int lineLength = 312;
	protected static final int entryLength = 78;
	protected static final int entriesPerPage = 28;
	
    private WarpTableListener listener = new WarpTableListener();

    protected static final Logger log = Logger.getLogger("Minecraft");
    // private final String newLine = System.getProperty("line.separator");
    protected String warpFile;
    
    public rTables(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, plugin, cLoader);
    }
	

	
	public boolean load() {
        return true;	
    }
    
    public void onEnable() {
        if (load())
		{
        	PluginManager loader = MCServer.getPluginManager();
			//etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
        	loader.registerEvent(Event.Type.PLAYER_COMMAND, listener, Event.Priority.Monitor, this);
            log.info("[WarpTable] Enabled.");
			/*etc.getInstance().addCommand("/warptable",   "<pagenumber> - Nicer formatted table of warps.");
			etc.getInstance().addCommand("/plugintable", "<pagenumber> - Nicer formatted table of plugins.");*/
		}	
        else
		{
            log.info("[WarpTable] Error while loading.");
        }
    }
    

    public void onDisable() {
		//etc.getInstance().removeCommand("/warptable");
        log.info("[WarpTable] Plugin Disabled.");
		
    }
	
	public String[] wordWrap(String toWrap) {
		String parsed = new String();
		for (String toParse : toWrap.split("\n")) {
			parsed += MessageParser.lastColor(parsed) + MessageParser.combineSplit(0, MessageParser.wordWrap(toParse), "\n") + "\n";
		}
		return parsed.split("\n");
	}
	public String[] makeTable(String spaceSeperated, int pageNumber){
		return makeTable(spaceSeperated, pageNumber,entryLength, entriesPerPage);
	}
	public String[] makeTable(String spaceSeperated, int pageNumber, int entryLength, int entriesPerPage){
		ArrayList <String> messages = new ArrayList<String>();
		String output = new String();
        String [] warpListSplit = spaceSeperated.split("\\s+");
        int numPages = warpListSplit.length / entriesPerPage;
        if (warpListSplit.length % entriesPerPage >= 1) numPages ++;
        if (pageNumber  > numPages) {
        	messages.add("Invalid page number!");
        	return messages.toArray(new String[messages.size()]);
        }
        /* Display page # */
    	messages.add("Page #" + pageNumber + " of " + numPages);
        /* Display stuff */
        for (int i = (pageNumber-1) * entriesPerPage ; i < (pageNumber * entriesPerPage) && i < warpListSplit.length; i++) {
        	String warpItem = warpListSplit[i];
        	if (MessageParser.msgLength(output) + MessageParser.msgLength (warpItem) > lineLength ) {
        		messages.add(new String(output));
        		output = new String();
        	}
    		output += warpItem + " ";
    		if (lineLength - MessageParser.msgLength(output) < entryLength){
        		messages.add(new String (output));
        		output = new String();
        	} else for (int Compensating = entryLength - (MessageParser.msgLength(output) % entryLength); Compensating > 1;)
    		{
    			switch (Compensating % 4) {
    				case 0:
    					output += " ";
    					Compensating -= 4;
    					break;
    				case 2:
    					output += Color.BLACK + "." + MessageParser.lastColor(output);
    					Compensating -=2;
    					break;
    				case 1:
    				case 3:
    					output += Color.BLACK + "'" + MessageParser.lastColor(output);
    					Compensating -=3;
    					break;
    			}
    			
    		}
        }
        messages.add(new String(output));
        return messages.toArray(new String[messages.size()]);
	}
	
	
    public class WarpTableListener extends PlayerListener
    {

    	public void onPlayerCommand(PlayerChatEvent event){
    		Player player = event.getPlayer();
    		String [] split = event.getMessage().split(" ");
	        if (split[0].equalsIgnoreCase("/playertable") || split[0].equalsIgnoreCase("playertable")){
	        	int pageNumber;
	        	if (split.length == 1)
	        		pageNumber = 1;
	        	else try {
	        		pageNumber = new Integer(split[1]);
	        	} catch (NumberFormatException ex){
	        		player.sendMessage("Invalid page number!");
	        		event.setCancelled(true);
	        		return;
	        	}
	        	String pluginList = new String();
	        	for (Player addme : MCServer.getOnlinePlayers()) {
	        		pluginList = pluginList + addme.getName() + " ";
	        	}
	        	player.sendMessage(pluginList);
	            for (String output : makeTable(pluginList, pageNumber))
	            player.sendMessage(output);
	            event.setCancelled(true);
	            return;
	        }
	        else
	            return;
	    }
    }
}