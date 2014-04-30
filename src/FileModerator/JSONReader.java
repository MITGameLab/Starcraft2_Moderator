package FileModerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONReader {

	ArrayList<JSONObject> jsonStringsPlayer1;
	ArrayList<JSONObject> jsonStringsPlayer2;
	
	long lastUpdate =0;
	
	public JSONReader() throws IOException{	
		
		//TODO: START THREAD
		jsonStringsPlayer1 = new ArrayList<JSONObject>();
		jsonStringsPlayer2 = new ArrayList<JSONObject>();
		
	}

	//Read file, save as string to memory (jsonStrings Array)
	public void readFile(String readDirectory, boolean isPlayer1) throws IOException{
		
		//FILE -> STRING
		BufferedReader br = new BufferedReader(new FileReader(readDirectory));
		StringBuilder sb = new StringBuilder();
		
		String line;
		
		while((line=br.readLine()) != null){
			sb.append(line);
		}
		br.close();
		
		//br = stripBom(sb.toString());
		
		//br_decoded = decode(br);
		
		//STRING -> JSON
		BufferedReader stringForJson = decode(stripBom(sb.toString()));
		JSONTokener tokener = new JSONTokener(stringForJson);
		JSONObject root;
		
		long update;
		
		try{
			root = new JSONObject(tokener);
			update = root.getLong("update");
			
			if(update > lastUpdate){
				if(update != lastUpdate+1){
					System.out.println("Missing between "+lastUpdate+" and "+ update);
				}
				lastUpdate = update;
				if(isPlayer1){
					jsonStringsPlayer1.add(root);
				}else{
					jsonStringsPlayer2.add(root);
				}
			}
			
		}catch(org.json.JSONException e){
			//System.out.println(sb.toString());
		}
		
		
	}
	
	/*
	 * Strip Byte Order Marks
	 * Keeping as separate because had to try multiple ways to do; can enhance at a later time other than just moving over 3
	 */
	public BufferedReader stripBom(String readFile) throws IOException{

		PushbackReader pbr = new PushbackReader(new StringReader(readFile),4);

		char[] charBuff = new char[3];
		pbr.read(charBuff);
		
		if(charBuff[0]  == (char) 0xEF &&
			charBuff[1] == (char) 0xBB &&
			charBuff[2] == (char) 0xBF){
			//System.out.println("match: "+charBuff.toString());
			
		}else{
			System.out.println("No BOM");
			pbr.unread(charBuff);
		}
		
		return new BufferedReader(pbr);
	}
	
	public BufferedReader decode(BufferedReader input){
		return input;
	}
}

		