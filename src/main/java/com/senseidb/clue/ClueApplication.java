package com.senseidb.clue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.senseidb.clue.commands.ClueCommand;
import com.senseidb.clue.commands.HelpCommand;

public class ClueApplication {
  
  private final ClueContext ctx;
  private final ClueCommand helpCommand;
  
  public ClueApplication(String idxLocation) throws IOException{
    FSDirectory dir = FSDirectory.open(new File(idxLocation));
    if (!DirectoryReader.indexExists(dir)){
      System.out.println("lucene index does not exist at: "+idxLocation);
      System.exit(1);
    }
    IndexReader r = DirectoryReader.open(dir);
    ctx = new ClueContext(r);
    helpCommand = ctx.getCommand(HelpCommand.CMD_NAME);
  }
  
  public void handleCommand(String cmdName, String[] args, PrintStream out){
    ClueCommand cmd = ctx.getCommand(cmdName);
    if (cmd == null){
      out.println(cmdName+" is not supported:");
      cmd = helpCommand;
    }
    try{
      cmd.execute(args, out);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  
  public static void main(String[] args) throws Exception {
    if (args.length < 1){
      System.out.println("usage: <index location> <command> <command args>");
      System.exit(1);
    }
    
    String idxLocation = args[0];
    
    ClueApplication app = new ClueApplication(idxLocation);
    
    if (args.length > 1){
      String cmd = args[1];
      String[] cmdArgs;
      cmdArgs = new String[args.length - 2];
      System.arraycopy(args, 2, cmdArgs, 0, cmdArgs.length);
      app.handleCommand(cmd, cmdArgs, System.out);
    }
    else{
      BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
      while(true){
        System.out.print("> ");
        String line = inReader.readLine().trim();
        if (line.isEmpty()) continue;
        String[] parts = line.split("\\s");
        if (parts.length > 0){
          String cmd = parts[0];
          String[] cmdArgs = new String[parts.length - 1];
          System.arraycopy(parts, 1, cmdArgs, 0, cmdArgs.length);
          app.handleCommand(cmd, cmdArgs, System.out);
        }
      }
    }    
  }
}
