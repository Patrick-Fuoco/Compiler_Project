
import java.util.ArrayList;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Token{

	TokenType type; //ENUM type called TokenType
	String lexeme;
	int lineNumber;
	ArrayList<Token> Tokens = new ArrayList<>();

	public Token(){}

    public Token(TokenType type, String lexeme, int line_number) {
        this.type = type;
        this.lexeme = lexeme;
        lineNumber = line_number;
    }
    
    public enum TokenType {
        assign,
        plus,
        or,
        l_paren,
        semi,
        IF,
        PUBLIC,
        READ,
        neq,
        minus,
        and,
        r_paren,
        comma,
        THEN,
        PRIVATE,
        WRITE,
        lt,
        multiply,
        not,
        l_brace,
        dot,
        ELSE,
        FUNC,
        RETURN,
        gt,
        div,
        r_brace,
        colon,
        INTEGER,
        VAR,
        SELF, 
        leq,
        eq,
        lsqbr,
        arrow,
        FLOAT,
        STRUCT,
        INHERITS,
        geq,
        rsqbr,
        VOID,
        WHILE,
        LET,
        IMPL,
        inline,
        block,
        intlit,
        floatlit,
        id,
        ERROR,
        EOF,
         
    }
    private static TokenType getEnum(String lexeme) {
        switch (lexeme) {
            case "=": return TokenType.assign;
            case "+": return TokenType.plus;
            case "|": return TokenType.or;
            case "(": return TokenType.l_paren;
            case ";": return TokenType.semi;
            case "<>": return TokenType.neq;
            case "-": return TokenType.minus;
            case "&": return TokenType.and;
            case ")": return TokenType.r_paren;
            case ",": return TokenType.comma;
            case "<": return TokenType.lt;
            case "*": return TokenType.multiply;
            case "!": return TokenType.not;
            case "{": return TokenType.l_brace;
            case "}": return TokenType.r_brace;
            case "/": return TokenType.div;
            case ">": return TokenType.gt;
            case ":": return TokenType.colon;
            case ".": return TokenType.dot;
            case "<=": return TokenType.leq;
            case "==": return TokenType.eq;
            case "[": return TokenType.lsqbr;
            case "->": return TokenType.arrow;
            case ">=": return TokenType.geq;
            case "]": return TokenType.rsqbr;
            case "if": return TokenType.IF;
            case "public": return TokenType.PUBLIC;
            case "read": return TokenType.READ;
            case "then": return TokenType.THEN;
            case "private": return TokenType.PRIVATE;
            case "write": return TokenType.WRITE;
            case "else": return TokenType.ELSE;
            case "func": return TokenType.FUNC;
            case "return": return TokenType.RETURN;
            case "while": return TokenType.WHILE;
            case "impl": return TokenType.IMPL;
            case "integer": return TokenType.INTEGER;
            case "float": return TokenType.FLOAT;
            case "var": return TokenType.VAR;
            case "self": return TokenType.SELF;
            case "struct": return TokenType.STRUCT;
            case "inherits": return TokenType.INHERITS;
            case "void": return TokenType.VOID;
            case "let": return TokenType.LET;
            default: 
            	return TokenType.ERROR;
        
            }
    }
    public static Token getNextToken(String lexeme, int line_number, boolean inline, boolean block) {
        
    	if (inline) {
            return new Token(TokenType.inline, lexeme, line_number);
        }
        
    	else if (block) {
            return new Token(TokenType.block, lexeme, line_number);
        }
        
    	else {
    		Token token = new Token(getEnum(lexeme),lexeme,line_number);
    		
    		if(!(token.type.toString().equals("ERROR"))) 
    			return token;
    		 
    		else {
    			
    			if (lexeme.matches("[a-zA-Z][a-zA-Z0-9_]*")) { //IDENTIFIER
    		            return new Token(TokenType.id, lexeme, line_number);
    		        } else if (lexeme.matches("0|[1-9][0-9]*")) { //INT
    		            return new Token(TokenType.intlit, lexeme, line_number);
    		        } else if (lexeme.matches("0|[1-9][0-9]*\\.[0-9]*[1-9]([eE][+-]?[0-9]+)?")) {//FLOAT
    		            return new Token(TokenType.floatlit, lexeme, line_number);
    		        }
    		      
    		        else
    		        	return token;
    		 }	
    	} 		 
    }
    Set<String> symbols = Set.of(
        "=", "+", "|", "(", ";", "<>", "-", "&", ")", ",",
        "<", "*", "!", "{", "}", "/", ">", ":", ".", "<=",
        "==", "[", "->", ">=", "]"
    );

	public ArrayList<Token> analyzeFile(String File1_path) {
       
        StringBuilder Token_builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(File1_path))){

            String line;
            int line_Number = 0;
     
            while ((line = reader.readLine()) != null) { //iterates through every line in file passed (file1_path)
                System.out.println(Token_builder.toString());
                line_Number++;
                String trimmed_line = line.replaceAll("\\s+$", "");
                System.out.println(trimmed_line);

                for(int i=0; i<(trimmed_line.length()); i++){
                   
    
                char current_char = line.charAt(i);
                
                if(Character.isWhitespace(current_char)){//read next character ONLY AFTER creating token from whatever was chilling in stringbuilder
                   if(Token_builder.length() != 0){ 
                    Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, false));
                    Token_builder.setLength(0);
                   }
                   else    
                    continue;
                }

                //checking if we encounter 1 or 2 character symbol.
                else if(symbols.contains(String.valueOf(current_char))){
                    
                    if(Token_builder.length() !=0 && Character.isDigit(Token_builder.toString().charAt(0)) && String.valueOf(current_char).equals(".")){ //for the number scenarios, when we encounter a dot
                        Token_builder.append(current_char);
                        continue;
                    }

                    if(Token_builder.length() != 0){ //create token from whatever was in builder
                        Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, false));
                 }

                Token_builder.setLength(0); //flush out what was there
                Token_builder.append(current_char); //put first char
                
                if((i+1) == line.length()){
                    Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, false));
                    Token_builder.setLength(0);
                    continue; //brings us back to the beginning of for loop, in order words we will not pass the condition and then end up in the outar while loop 
                }
                i++;
                current_char = line.charAt(i);
                Token_builder.append(current_char);
                if(symbols.contains(Token_builder.toString())){ //2 character symbol is legit
                    Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, false));
                    Token_builder.setLength(0);
                }
                else if(Token_builder.toString().equals("//")){
                    
                    while(++i<(trimmed_line.length())){

                        current_char = line.charAt(i);
                        Token_builder.append(current_char);
                    }

                    Tokens.add(getNextToken(Token_builder.toString(), line_Number, true, false));
                    Token_builder.setLength(0);
                }
                else if(Token_builder.toString().equals("/*")){
                   
                    int depth_counter = 1;
                    boolean go_again = true;

                    while(go_again){ //when we must see another line

                        while(++i<trimmed_line.length() && depth_counter != 0){
                            
                            current_char = line.charAt(i);
                            Token_builder.append(current_char); //keep appending while in this block comment, no laws anything gets appended
                           
                            if(Token_builder.toString().substring(Token_builder.length()-2).equals("*/"))
                                depth_counter--;
                            if(Token_builder.toString().substring(Token_builder.length()-2).equals("/*"))
                                depth_counter++;
                         }
    
                         if(i == trimmed_line.length()){//we reached end of line but still have depth counter != 0, we must go back for more
                            String nextLine = reader.readLine();
                            if (nextLine != null) {
                                trimmed_line = nextLine.trim();
                                line_Number++;
                                i=0;
                            } 
                        }
    
                         else{ //depth counter =0, we have more to our line
                            go_again = false;
                         }
    
                        }
    
                        Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, true)); //we broke out of block and are ready to create block token
                        Token_builder.setLength(0);
                    }
               
                else{ //1 character symbol
                    Token_builder.deleteCharAt(Token_builder.length()-1);
                    Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, false));
                    Token_builder.setLength(0);
                    i--;//so we can recreate stringbuilder with the ending we chopped off
                }
            }


            else{ //not a whitespace nor a symbol
                Token_builder.append(current_char); 
                if((i+1) == trimmed_line.length()){
                Tokens.add(getNextToken(Token_builder.toString(), line_Number, false, false));
                Token_builder.setLength(0);
                }
            }
        }
    }
 } catch (IOException e) {
            System.err.println("An error occurred while processing the file.");
            e.printStackTrace();
        }
        return Tokens;  
    }

    public String toString() {
    	
    	if(type.toString().equals("ERROR")) {
    		if(lexeme.matches("[0-9]+(\\.[0-9]+)?"))
    			return "Lexical Error: invalid number: " + lexeme + " , " + lineNumber;
    		else
    			return "Lexical Error: invalid identifier: " + lexeme + " , " + lineNumber;
    	}
    	else 
    		return "[" + type + "," + lexeme + ", " + lineNumber + "]";
    	
    }
}
  
