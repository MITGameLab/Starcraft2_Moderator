package FileModerator;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ModeratorMain {
	
	private JSONReader reader;
	private Path watchedDirectory;
	private WatchService watchService;
	private Map<WatchKey, Path> keys;
	private String player1WriteDirectory = "./player1";
	private String player2WriteDirectory = "./player2";
	//private String readingDirectory = "C://Users/Arti/Documents/StarCraft II/UserLogs/Agria Valley/DataLog_Player1.txt";
	
	public ModeratorMain(Path directory) throws IOException{
		this.watchService = FileSystems.getDefault().newWatchService();
		this.watchedDirectory = directory;
		this.keys = new HashMap<WatchKey, Path>();
		this.reader = new JSONReader();;
		
		//TODO: CHECK IF WRITE-DIRS ALREADY EXIST - CAN BE HANDLED BY GUI
		
		
		register(directory);
			
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
				//ERROR WATCHKEY NOT RECOGNIZED
				System.err.println("WatchKey not in keys");
				continue;
			}
			
			for(WatchEvent<?> event: key.pollEvents()){
				WatchEvent.Kind kind = event.kind();
				
				if(kind == java.nio.file.StandardWatchEventKinds.OVERFLOW){
					//WHAT TO DO IF INFO LOST
					continue;
				}
				
				WatchEvent<Path> entryEvent = cast(event);
				Path name = entryEvent.context();
				Path child = directory.resolve(name);
				
				// print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
				
				//TODO: READ THE CHILD AND APPEND IT TO A MASTER FILE
                if(event.kind()==java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY){
                	if(child.endsWith("DataLog_Player1.txt")){
                		reader.readFile(child.toString(), player1WriteDirectory, true);
                	}else if(child.endsWith("DataLog_Player2.txt")){
                		reader.readFile(child.toString(), player2WriteDirectory, true);
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
		
		String directory = "C://Users/Arti/Documents/StarCraft II/UserLogs/Agria Valley/";
		Path directoryPath =Paths.get(directory);
		
		new ModeratorMain(directoryPath).processEvents();
	
		
	}

}
