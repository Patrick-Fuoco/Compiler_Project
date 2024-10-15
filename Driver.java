import java.io.File;
import java.util.ArrayList;

public class Driver {
    public static void main(String[] args) {

    	    String directory_path = "Files_to_read/";
			
			File directory = new File(directory_path);
			
			File[] all_files = directory.listFiles();
			
			if (all_files != null) {
				for (File current_file : all_files) {

                Token token = new Token(); 
                ArrayList<Token> tokensForFile = token.analyzeFile(current_file.getAbsolutePath());

                Augmented_Parser.tokens.addAll(tokensForFile); // Assign the tokens from analyzeFile to Parser's static field
                Augmented_Parser AST = new Augmented_Parser();

    for(int i=0; i < Augmented_Parser.tokens.size() ; i++){
        System.out.println(Augmented_Parser.tokens.get(i));
    }				

    AST.START(); // Assuming START() begins the parsing process
   //AST.printTree();
}

}
}
}