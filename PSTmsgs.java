package lucene;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;

public class PSTmsgs {
	static int depth=-1;
	public static void main(String[] a) throws PSTException, IOException{
		 
		PSTFile pst = new PSTFile(new File(".\\data\\a.pst"));
		PSTFolder folder = pst.getRootFolder();
		processFolder(folder);
	
	}
	public static void processFolder(PSTFolder folder) throws PSTException, IOException{
		depth++;
         // the root folder doesn't have a display name
        if (depth > 0) {
        	printDepth();
          //  System.out.println(folder.getDisplayName());
         }

         // go through the folders...
         if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
            	processFolder(childFolder);
            }
         }

		if(folder.getContentCount()>0){
			//int counter = 0;
			PSTMessage email = (PSTMessage)folder.getNextChild();
			while(email !=null){
		 		//String header=email.getTransportMessageHeaders();
				//EmailParser headerParser = new EmailParser();
				//headerParser.parseheader(header);
				//System.out.println(email.getSenderName());
				//System.out.println(email.getSubject()+"message to me"+(email.getMessageSize()/1024)+email.getMessageDeliveryTime());
				Lindexer lix = new Lindexer();
				lix.indexFromPst("index",email);
				email = (PSTMessage)folder.getNextChild();
			}
		}
		
		}
	private static void printDepth() {
		for (int x = 0; x < depth-1; x++) {
            System.out.print(" | ");
    }
    System.out.print(" |- ");
	}

}
