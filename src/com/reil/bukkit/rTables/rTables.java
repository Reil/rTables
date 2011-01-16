package com.reil.bukkit.rTables;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.reil.bukkit.rParser.rParser;


import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class rTables extends JavaPlugin {
	Server MCServer = getServer();
	protected static final int lineLength = 312;
	protected static final int entryLength = 78;
	protected static final int entriesPerPage = 28;
	
    private rTablesListener listener = new rTablesListener(this);

    protected static final Logger log = Logger.getLogger("Minecraft");
    // private final String newLine = System.getProperty("line.separator");
    protected String warpFile;
    
    public rTables(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
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
		return wordWrap(toWrap, "", lineLength);
	}
	public String[] wordWrap(String toWrap, String prefix, int lineLength){
		String parsed = new String();
		for (String toParse : toWrap.split("\n")) {
			parsed += rParser.lastColor(parsed) + rParser.combineSplit(0, rParser.wordWrap(toParse, prefix, lineLength), "\n") + "\n";
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
        	if (rParser.msgLength(output) + rParser.msgLength (warpItem) > lineLength ) {
        		messages.add(new String(output));
        		output = new String();
        	}
    		output += warpItem + " ";
    		if (lineLength - rParser.msgLength(output) < entryLength){
        		messages.add(new String (output));
        		output = new String();
        	} else for (int Compensating = entryLength - (rParser.msgLength(output) % entryLength); Compensating > 1;)
    		{
        		String lastColor = rParser.lastColor(output);
    			switch (Compensating % 4) {
    				case 0:
    					output += " ";
    					Compensating -= 4;
    					break;
    				case 2:
    					output += ChatColor.BLACK + "." + lastColor;
    					Compensating -=2;
    					break;
    				case 1:
    				case 3:
    					output += ChatColor.BLACK + "'" + lastColor;
    					Compensating -=3;
    					break;
    			}
    			
    		}
        }
        messages.add(new String(output));
        return messages.toArray(new String[messages.size()]);
	}
	/*
	 * Following functions essentially give other plugins access to rParser.
	 */
	public int msgLength(String findLength){
		return rParser.msgLength(findLength);
	}
}