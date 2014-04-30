package FileModerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ModeratorMain {
	
	private JSONReader reader;
	private WatchService watchService;
	private Map<WatchKey, Path> keys;
	private String readDirectory;
	private String writeDirectory;
	//private long player1Update;
	//private long player2Update;
	
	private final String settingsFilePath = "./ProgramSettings.txt";
	
	public ModeratorMain() throws IOException{
		this.watchService = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.reader = new JSONReader();
		//this.player1Update=0;
		//this.player2Update=0;
		
		updateSettings();
		Path directoryPath = Paths.get(readDirectory);
		
		//Make sure write directory exists
		File wf = new File(writeDirectory);
		if(!wf.exists() || !wf.isDirectory()){
			wf.mkdir();
		}
		
		//register directory we're tracking
		register(directoryPath);
			
	}
	//Check for a file that specifies settings - Read Directory, Write To Directory, 
	private void updateSettings() throws IOException{
		File f = new File(settingsFilePath);
		
		if(f.exists() && f.isFile()){//MAY BE REDUNDANT
			try{
				//PARSE FILE
				BufferedReader br = new BufferedReader(new FileReader(f));
				JSONTokener tokener = new JSONTokener(br);
				JSONObject root = new JSONObject(tokener);
				this.readDirectory = root.getString("readDirectory");
				this.writeDirectory = root.getString("writeDirectory");
								
			}catch(IOException e){
				System.err.println("Could not read file at "+this.settingsFilePath+" Rewriting Settings.");
				writeSettings();
			}catch(org.json.JSONException e){
				System.err.println("Could not parse file at "+this.settingsFilePath+" Incorrect JSON formatting. Rewriting Settings.");
				writeSettings();
			}
		}else{
			writeSettings();
		}
	}
	
	private void writeSettings(){
		//IF FILE !EXIST CREATE FILE AT DEFAULT LOCATION
		JSONObject obj = new JSONObject();
		String defaultDir = "C://Users/"+System.getProperty("user.name")+"/Documents/StarCraft II/UserLogs/Agria Valley/";
		obj.put("readDirectory", defaultDir);
		obj.put("writeDirectory", "./");
		
		try{
			FileWriter file = new FileWriter(settingsFilePath);
			file.write(obj.toString());
			file.flush();
			file.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event){
		return (WatchEvent<T>)event;
	}

	
	/*
	 * Initially assuming that we will only have to watch the data logs
	 * 	In other words we don't need to watch for new directories being created
	 */
	private void register(Path dir) throws IOException{
		WatchKey key = dir.register(watchService, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE,java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
		keys.put(key, dir);
		System.out.println("We have lift off");
	}
	
	void processEvents() throws IOException{
		while(true){
			WatchKey key;
			try{
				key = watchService.take();
			}catch(InterruptedException e){
				return;
			}
			
			Path directory = keys.get(key);
			if(directory==null){
				// ERROR WATCHKEY NOT RECOGNIZED
				System.err.println("WatchKey not in keys");
				continue;
			}
			
			for(WatchEvent<?> event: key.pollEvents()){
				WatchEvent.Kind kind = event.kind();
				
				if(kind == java.nio.file.StandardWatchEventKinds.OVERFLOW){
					// Buffer overruns 
					continue;
				}
				
				WatchEvent<Path> entryEvent = cast(event);
				Path name = entryEvent.context();
				Path child = directory.resolve(name);
				
				// print out event
                //System.out.format("%s: %s\n", event.kind().name(), child);
				
				// Read file
                if(kind==java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY){ 
                	if(child.endsWith("DataLog_Player1.txt")){
                		reader.readFile(child.toString(),true);
                	}else if(child.endsWith("DataLog_Player2.txt")){
                		reader.readFile(child.toString(),false);
                	}else{
                		System.out.println("ERROR - TRACKING NON DATALOG FILES");
                	}
                }
				
				boolean reset = key.reset();//resets key for queuing
				if(!reset){
					keys.remove(key);
					if(keys.isEmpty()){
						break;
					}
				}
			}
		}
	}
	
	
/*
 * Initially we will just tell the main method the directory we want to follow
 * 	As we mature the application we will create a GUI for the users to 
 * 	select their directory to trace.
 */
	public static void main(String[] args) throws IOException {
		
		
		new ModeratorMain().processEvents();
		
		
	}

}
