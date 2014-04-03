package FileModerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONReader {

	FileWriter writer;
	//String writeToFile = "./testing.txt";
	
	public JSONReader() throws IOException{
		//writer = new FileWriter(writeToFile);
	}
	
	
	public void readFile(String readDirectory, String writeDirectory, boolean append) throws IOException{
		
		this.writer = new FileWriter(writeDirectory,append);
		BufferedReader br = new BufferedReader(new FileReader(readDirectory));
		
		String line = br.readLine();
		while(line != null){
			writer.append(line);
			writer.append(System.lineSeparator());
			line = br.readLine();
		}
		br.close();
		writer.close();
	}
	
}
