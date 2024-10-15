
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;

public class Parser {
	static Token lookahead;
  static ArrayList<Token> tokens = new ArrayList<>();
	static int iterator = 0;
	@SuppressWarnings("unused")
	private FileWriter derivation;
  @SuppressWarnings("unused")
	private FileWriter errors;
	
    public static void clearTokens() {
        tokens.clear();
    }
   
Parser(){}

	public Parser(String File_path1, String File_path2) throws IOException{
			
		derivation = new FileWriter(File_path1);
		errors = new FileWriter(File_path2);	
    
	}

	private boolean skipErrors(Set<String> first, Set<String> follow, Token token) {
		String real_lexeme_storage = "";
		boolean b = false;
		switch (token.type.toString()) {
			case "id":
				real_lexeme_storage = token.lexeme;
				token.lexeme = "id";
				b=true;
				break;
			case "intlit":
				real_lexeme_storage = token.lexeme;
				token.lexeme = "intlit";
				b=true;
				break;
			case "floatlit":
				real_lexeme_storage = token.lexeme;
				token.lexeme = "floatlit";
				b=true;
				break;
			default:
			break;
		}

		if (first.contains(token.lexeme) || (first.contains("EPSILON") && follow.contains(token.lexeme))) {
			
			if(first.contains(token.lexeme) && b){
				token.lexeme = real_lexeme_storage;
			}
	        
			return true;    
		} 
        else {
            System.out.println("Syntax error at line " + lookahead.lineNumber + " on this token: " + lookahead.lexeme + "\n");
			
	        while (!first.contains(token.lexeme) && !follow.contains(token.lexeme)) {
	            token = readNext();
				real_lexeme_storage = token.lexeme;
			
				switch (token.type.toString()) {
					case "id":
						token.lexeme = "id";
						break;
					case "intlit":
						token.lexeme = "intlit";
						break;
					case "floatlit":
						token.lexeme = "floatlit";
						break;
					default:
					break;
				}
	            if (first.contains("EPSILON") && follow.contains(token.lexeme)) {
					lookahead = token;
					lookahead.lexeme = real_lexeme_storage;
	                return false; 
	            }
	        }
			return true;
		}
	}

	    
	
	public static Token readNext() {

        if(iterator < (tokens.size())){
			Token token = tokens.get(iterator);
            iterator++;
			
			while(token.type == Token.TokenType.inline || token.type == Token.TokenType.block){
					token = tokens.get(iterator);
					iterator++;
					
			}
            return token;
        }

       else 
	   return new Token(Token.TokenType.EOF, "eof", 1000);

		}

		public static boolean match(String expectedLexeme, String lexeme) {

			if (lexeme.equals(expectedLexeme)) {
				lookahead = readNext();
				return true;
			
			} 
			else {
				lookahead = readNext();
				return false;
			}
		}
	
		public static boolean match(String expectedLexeme, String lexeme, StringBuilder storage) {
	
			if(expectedLexeme.equals("id")){
				if (lookahead.type == Token.TokenType.id){
					storage.append(lookahead.lexeme);
					lookahead = readNext();
					return true;}
	
				else {
				lookahead = readNext();
				return false;}
	
			}
	
			else if(expectedLexeme.equals("intlit")){
				
				if (lookahead.type == Token.TokenType.intlit){
					storage.append(lookahead.lexeme);
					lookahead = readNext();
					return true;}
	
				else {
					lookahead = readNext();
					return false;}
	
			}
	
			else if(expectedLexeme.equals("floatlit")){
				
				if (lookahead.type == Token.TokenType.floatlit){
				storage.append(lookahead.lexeme);
				lookahead = readNext();
					return true;}
	
				else {
				lookahead = readNext();
				return false;}
				
				}
	
			else
			return false;
		}

		//FOR DEBUGGING PURPOSES
public boolean CHECKER(){
	//System.out.println(lookahead.lexeme);
	return true;
}
	
public boolean FUNCCHECKER(boolean b){
	System.out.println(b);
	return b;
}

public boolean START() {

	lookahead = readNext();
	    
	if (!skipErrors(new HashSet<>(Arrays.asList("eof", "struct", "impl", "func")), new HashSet<>(Arrays.asList()), lookahead))
		return false;
	
	
	if(lookahead.lexeme.equals("struct") || lookahead.lexeme.equals("impl") || lookahead.lexeme.equals("func")) {
		
		if(PROG()) {
			System.out.println("START -> PROG"); 
			return true;
		}
		else {
			return false;
	}
}
	
	else
		return false;
}


public boolean PROG() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("struct", "impl", "func")), new HashSet<>(Arrays.asList("eof")), lookahead))
       return false;
	
	
	if(lookahead.lexeme.equals("struct") || lookahead.lexeme.equals("impl") || lookahead.lexeme.equals("func")) {
		
		if(REPTPROG0()) {
			System.out.println("PROG -> REPTPROG0"); 
			return true;
		}
		else 
			return false;
	}
	
	else
		return false;
	
}

public boolean REPTPROG0() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("struct", "impl", "func", "EPSILON")), new HashSet<>(Arrays.asList("eof")), lookahead))
       return false;
	
	if(lookahead.lexeme.equals("struct") || lookahead.lexeme.equals("impl") || lookahead.lexeme.equals("func")) {
		
		if(STRUCTORIMPLORFUNC() && REPTPROG0()) {
			System.out.println("REPTPROG0 -> STRUCTORIMPLORFUNC REPTPROG0"); 
			return true;
		}
		
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals("eof")) {
		System.out.println("REPTPROG0 -> EPSILON");
		return true;
	}
	
	else
		return false;
	
}



public boolean STRUCTORIMPLORFUNC() {
	if (!skipErrors(new HashSet<>(Arrays.asList("struct", "impl", "func")), new HashSet<>(Arrays.asList("struct", "impl", "func", "eof")), lookahead))
       return false;

	if(lookahead.lexeme.equals("struct")) {
	
		if(STRUCTDECL()) {
			System.out.println("STRUCTORIMPLORFUNC -> STRUCTDECL"); 
			return true;
		}
		
		else
			return false;
}
	
	else if(lookahead.lexeme.equals("impl")) {
	
		if(IMPLDEF()) {
			System.out.println("STRUCTORIMPLORFUNC -> IMPLDECL"); 
			return true;
		}
	
		else
			return false;
}
	
	else if(lookahead.lexeme.equals("func")) {
		
		if(FUNCDEF()) {
			System.out.println("STRUCTORIMPLORFUNC -> FUNCDEF"); 
			return true;
		}
		
		else
			return false;
		}
	
	else
		return false;
}


public boolean STRUCTDECL() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("struct")), new HashSet<>(Arrays.asList("struct", "impl", "func", "eof")), lookahead))
        return false;


	if(lookahead.lexeme.equals("struct")) {
		StringBuilder storage = new StringBuilder();
		if(match("struct", lookahead.lexeme) && match("id",lookahead.lexeme,storage) && OPTSTRUCTDECL2() &&  match("{",lookahead.lexeme) && REPTSTRUCTDECL4() && match(";",lookahead.lexeme) &&  match("}",lookahead.lexeme)) {
			System.out.println("STRUCTDECL -> struct " + storage + " OPTSTRUCTDECL2 { REPTSTRUCTDECL4 } ;"); 
			return true;
		}
		else 
			return false;
		
	}
	
	else
		return false;
}

public boolean IMPLDEF() {
    if (!skipErrors(new HashSet<>(Arrays.asList("impl")), new HashSet<>(Arrays.asList("impl", "func", "eof", "struct")), lookahead))
        return false;

	if (lookahead.lexeme.equals("impl")) {
        StringBuilder storage = new StringBuilder();
		if (match("impl", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && match("{", lookahead.lexeme) && REPTIMPLDEF3() && match("}", lookahead.lexeme)) {
            System.out.println("IMPLDECL -> impl " + storage + " { REPTIMPLDEF3 }");
            	return true;
        }
		else
			return false;
    }
	else
    	return false;
}

public boolean FUNCDEF() {
    if (!skipErrors(new HashSet<>(Arrays.asList("func")), new HashSet<>(Arrays.asList("impl", "func", "eof", "struct", "}")), lookahead))
        return false;

    if (lookahead.lexeme.equals("func")) {

        if (FUNCHEAD() && FUNCBODY()) {

            System.out.println("FUNCDEF -> FUNCHEAD FUNCBODY ;");
            return true;
        }
		else
			return false;
    }
	else
    	return false;
}


public boolean OPTSTRUCTDECL2() {
	if (!skipErrors(new HashSet<>(Arrays.asList("inherits", "EPSILON")), new HashSet<>(Arrays.asList("{")), lookahead))
	       return false;
	
	if(lookahead.lexeme.equals("inherits")){
        StringBuilder storage = new StringBuilder();
		 if(match("inherits", lookahead.lexeme) && match("id",lookahead.lexeme,storage) && REPTOPTSTRUCTDECL22()){
			 System.out.println("OPTSTRUCTDECL2 -> inherits " + storage + " REPTOPTSTRUCTDECL22");
	          return true;
		 }
	      else
	       return false;
	  }
	 
	 else if(lookahead.lexeme.equals("{")){
		 
		 System.out.println("OPTSTRUCTDECL2 -> EPSILON");
         return true;
	 }
	 
	 else
		 return false;
}
public boolean REPTSTRUCTDECL4() {
	if (!skipErrors(new HashSet<>(Arrays.asList("public", "private", "EPSILON")), new HashSet<>(Arrays.asList("}")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("public") ||lookahead.lexeme.equals("private")) {
		if(VISIBILITY() && MEMBERDECL() && REPTSTRUCTDECL4()) {
			 System.out.println("REPTSTRUCTDECL4 -> VISIBILITY MEMBERDECL REPTSTRUCTDECL4");
	          return true;
		}
		else 
			return false;
	}
	
	else if(lookahead.lexeme.equals("}")){
		System.out.println("REPTSTRUCTDECL4 -> EPSILON");
		return true;
	}
		
	else
		return false;
}


public boolean REPTIMPLDEF3() {
	if (!skipErrors(new HashSet<>(Arrays.asList("func", "EPSILON")), new HashSet<>(Arrays.asList("}")), lookahead))
        return false;

	if(lookahead.lexeme.equals("func")) {
		 System.out.println("REPTIMPLDEF3 -> FUNCDEF REPTIMPLDEF3");
	     return true;
	}

	else if(lookahead.lexeme.equals("}")) {
		 System.out.println("REPTIMPLDEF3 -> EPSILON");
		 return true;
	}
	
	else
		return false;

}

public boolean FUNCHEAD() {
	if (!skipErrors(new HashSet<>(Arrays.asList("func")), new HashSet<>(Arrays.asList(";", "{")), lookahead))
        return false;
	
	 if(lookahead.lexeme.equals("func")) {
		StringBuilder storage = new StringBuilder();
		if(match("func", lookahead.lexeme)  && match("id", lookahead.lexeme,storage) && match("(", lookahead.lexeme) && FPARAMS() && match(")", lookahead.lexeme) && match("->", lookahead.lexeme) && RETURNTYPE()) {
			System.out.println("FUNCHEAD -> func " + storage + " ( FPARAMS ) -> RETURNTYPE");
			 return true;
		}
		
		else
			return false;
		}
	
	else
		return false;
	}


public boolean FUNCBODY() {
	if (!skipErrors(new HashSet<>(Arrays.asList("{")), new HashSet<>(Arrays.asList("}", "struct", "impl", "func", "eof")), lookahead))
       return false;
	
	if(lookahead.lexeme.equals("{")) {
		if(match("{", lookahead.lexeme) && REPTFUNCBODY1() && match("}", lookahead.lexeme)) {
			System.out.println("FUNCBODY -> { REPTFUNCBODY1 }");
			 return true;
		}
		
		else
			return false;
	}
	
	else
		return false;
	}

	
public boolean REPTOPTSTRUCTDECL22() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList(",", "EPSILON")), new HashSet<>(Arrays.asList("{")), lookahead))
        return false;
  
   if(lookahead.lexeme.equals(",")) {
	StringBuilder storage = new StringBuilder();
	if(match(",", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && REPTOPTSTRUCTDECL22()) {
		System.out.println("REPTOPTSTRUCTDECL22 -> , " +  storage + " REPTOPTSTRUCTDECL22");
		 return true;
		}
		
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("{")) {
		System.out.println("REPTOPTSTRUCTDECL22 -> EPSILON");
		 return true;
	}
	
	else
		return false;
	
}

public boolean FPARAMS() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "EPSILON")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;

	
	 if(lookahead.type == Token.TokenType.id) {
		StringBuilder storage = new StringBuilder();
		if(match("id", lookahead.lexeme,storage) && match(":", lookahead.lexeme) && TYPE() && REPTFPARAMS3() && REPTFPARAMS4()) {
			System.out.println("FPARAMS -> " + storage + " : TYPE REPTFPARAMS3 REPTFPARAMS4");
			 return true;
		}
	
	 else
		return false;
	}
	
	else if(lookahead.lexeme.equals(")")) {
		System.out.println("FPARAMS -> EPSILON");
		 return true;
	}
	
	else
		return false;
	
}
	
public boolean VISIBILITY() {
	if (!skipErrors(new HashSet<>(Arrays.asList("public", "private")), new HashSet<>(Arrays.asList("let", "func")), lookahead))
        return false;

	if(lookahead.lexeme.equals("public")) {
		if(match("public", lookahead.lexeme)) {
			System.out.println("VISIBILITY -> public");
		 	return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals("private")) {
		
	if(match("private", lookahead.lexeme)) {
		System.out.println("VISIBILITY -> private");
		 return true;
	}
		else
			return false;
	}
	
	else
		return false;

}

public boolean MEMBERDECL() {
	if (!skipErrors(new HashSet<>(Arrays.asList("let", "func")), new HashSet<>(Arrays.asList("}", "public", "private")), lookahead))
        return false;

	if(lookahead.lexeme.equals("func")) {
		if(FUNCDECL()) {
			System.out.println("MEMBERDECL -> FUNCDECL");
			 return true;
		}
			else
				return false;
	}
	
	else if(lookahead.lexeme.equals("let")) {
		if(VARDECL()) {
			System.out.println("MEMBERDECL -> VARDECL");
			 return true;
		}
		else
			return false;
	}
	
	else
		return false;
	
}


public boolean FUNCDECL() {
	if (!skipErrors(new HashSet<>(Arrays.asList("func")), new HashSet<>(Arrays.asList("}", "public", "private")), lookahead))
        return false;

	if(lookahead.lexeme.equals("func")) {
		if(FUNCHEAD() && match(";", lookahead.lexeme)) {
			System.out.println("FUNCDECL -> FUNCHEAD ;");
			 return true;
		}
		else
			return false;
		}
	
	else
		return false;
	}



public boolean VARDECL() {
	if (!skipErrors(new HashSet<>(Arrays.asList("let")), new HashSet<>(Arrays.asList("public", "private", "let", "id", "if", "while", "read", "write", "return", "}")), lookahead))
       return false;

	if(lookahead.lexeme.equals("let")){
		StringBuilder storage = new StringBuilder();
		if(match("let", lookahead.lexeme) && match("id", lookahead.lexeme,storage)  && match(":", lookahead.lexeme)  && TYPE() && REPTVARDECL4() && match(";", lookahead.lexeme)) {
			System.out.println("VARDECL -> let " + storage + " : TYPE REPTVARDECL4 ;");
			 return true;
		}
		else
			return false;
	}
	
	else
		return false;
	}

public boolean TYPE() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("integer", "float", "id")), new HashSet<>(Arrays.asList(")", "{", ",", "[", ";")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("integer")) {
		if(match("integer", lookahead.lexeme)) {
			System.out.println("TYPE -> integer");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("float")) {
		if(match("float", lookahead.lexeme)) {
			System.out.println("TYPE -> float");
			 return true;
		}
		else
			return false;
		}
	
		else if(lookahead.type == Token.TokenType.id) {
			StringBuilder storage = new StringBuilder();
			if(match("id", lookahead.lexeme,storage)) {
				System.out.println("TYPE -> " + storage);
				 return true;
		}
		else
			return false;
		}
    else
		return false;
	
	}


public boolean RETURNTYPE() {

	if (!skipErrors(new HashSet<>(Arrays.asList("void", "float", "id", "integer")), new HashSet<>(Arrays.asList("{",";")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("float") ||lookahead.lexeme.equals("integer") || lookahead.type == Token.TokenType.id) {
		if(TYPE()) {
			System.out.println("RETURNTYPE -> TYPE");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("void")) {
		if(match("void", lookahead.lexeme)) {
			System.out.println("TYPE -> void");
			 return true;
		}
		else
			return false;
	}
	else 
		return false;
	}


public boolean REPTFPARAMS3() {
	if (!skipErrors(new HashSet<>(Arrays.asList("[", "EPSILON")), new HashSet<>(Arrays.asList(")",",")), lookahead))
      return false;

	if(lookahead.lexeme.equals("[")) {
		if(ARRAYSIZE() && REPTFPARAMS3()) {
			System.out.println("REPTFPARAMS3 -> ARRAYSIZE REPTFPARAMS3 ");
			 return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals(")") || lookahead.lexeme.equals(",")) {
		System.out.println("REPTFPARAMS3 -> EPSILON");
		 return true;
	}
	else
		return false;
}

public boolean ARRAYSIZE() {

	if (!skipErrors(new HashSet<>(Arrays.asList("[")), new HashSet<>(Arrays.asList(";","[", ")", ",")), lookahead))
        return false;

	if(lookahead.lexeme.equals("[")) {
		if(match("[", lookahead.lexeme) && ARRAYSIZE2()) {
			System.out.println("ARRAYSIZE -> [ ARRAYSIZE2");
			 return true;
		}
		else
			return false;
		}
	
	else
		return false;
	
}


public boolean ARRAYSIZE2() {

	if (!skipErrors(new HashSet<>(Arrays.asList("intlit", "]")), new HashSet<>(Arrays.asList(";","[", ")", ",")), lookahead))
       return false;
	
	
	if(lookahead.type == Token.TokenType.intlit){
		StringBuilder storage = new StringBuilder();
		if(match("intlit", lookahead.lexeme,storage) && match("]", lookahead.lexeme)){
			System.out.println("ARRAYSIZE2 -> " + storage + " ]");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("]")) {
		if(match("]", lookahead.lexeme)){
			System.out.println("ARRAYSIZE2 -> ]");
			 return true;
		}
		else
			return false;
	}
	else
		return false;
	}
	

public boolean REPTVARDECL4() {
	if (!skipErrors(new HashSet<>(Arrays.asList("[", "EPSILON")), new HashSet<>(Arrays.asList(";")), lookahead))
		return false;

	if(lookahead.lexeme.equals("[")){
		if(ARRAYSIZE()) {
			System.out.println("REPTVARDECL4 -> ARRAYSIZE REPTVARDECL4");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals(";")) {
	
		System.out.println("REPTVARDECL4 -> EPSILON");
		 return true;
	}
	else
		return false;
	}

public boolean REPTFPARAMS4() {
	if (!skipErrors(new HashSet<>(Arrays.asList(",", "EPSILON")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;

	if(lookahead.lexeme.equals(",")) {
		if(FPARAMSTAIL() && REPTFPARAMS4()) {
			System.out.println("REPTFPARAMS4 -> FPARAMSTAIL REPTFPARAMS4");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals(")")) {
		System.out.println("REPTFPARAMS4 -> EPSILON");
		 return true;
	}
	else
		return false;
}

public boolean FPARAMSTAIL() {
	if (!skipErrors(new HashSet<>(Arrays.asList(",")), new HashSet<>(Arrays.asList(",",")")), lookahead))
        return false;
		
	 if(lookahead.lexeme.equals(",")) {
		StringBuilder storage = new StringBuilder();
		if(match(",", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && match(":", lookahead.lexeme) && TYPE() && REPTFPARAMSTAIL4()) {
			System.out.println("FPARAMSTAIL -> , " + storage + " : TYPE REPTFPARAMSTAIL4");
			 return true;
		}
		else
			return false;
	}
	else
		return false;
}

public boolean REPTFPARAMSTAIL4() {
	if (!skipErrors(new HashSet<>(Arrays.asList("[", "EPSILON")), new HashSet<>(Arrays.asList(",",")")), lookahead))
       return false;

	if(lookahead.lexeme.equals("[")) {
		if(ARRAYSIZE() && REPTFPARAMSTAIL4()) {
			System.out.println("REPTFPARAMSTAIL4 -> ARRAYSIZE REPTFPARAMSTAIL4");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals(",") || lookahead.lexeme.equals(")")) {
		System.out.println("REPTFPARAMSTAIL4 -> EPSILON");
		 return true;
}
	else
		return false;
}

public boolean STATEMENTIDNEST() {
	if (!skipErrors(new HashSet<>(Arrays.asList(".", "(", "[", "=")), new HashSet<>(Arrays.asList(";")), lookahead))
        return false;

	if(lookahead.lexeme.equals(".")) {
		StringBuilder storage = new StringBuilder();
		if(match(".", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && STATEMENTIDNEST()) {
			System.out.println("STATEMENTIDNEST -> . " + storage + " STATEMENTIDNEST");
			 return true;
		}
		else
			return false;
		}
	else if(lookahead.lexeme.equals("(")){
		if(match("(", lookahead.lexeme) && APARAMS() && match(")", lookahead.lexeme) && STATEMENTIDNEST2()) {
			System.out.println("STATEMENTIDNEST -> ( APARAMS ) STATEMENTIDNEST2");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("[")) {

		if(INDICE() && REPTIDNEST1() && STATEMENTIDNEST3()) {
			System.out.println("STATEMENTIDNEST -> INDICE REPTIDNEST1 STATEMENTIDNEST3");
			 return true;
		}
		else
			return false;
		}
	else if(lookahead.lexeme.equals("=")) {
		if(ASSIGNOP() && EXPR()) {
			System.out.println("STATEMENTIDNEST -> ASSIGNOP EXPR");
			 return true;
		}
		else
			return false;
	}
	
	else 
		return false;
	}

public boolean APARAMS() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-", "EPSILON")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;

	if(lookahead.type == Token.TokenType.id || lookahead.type == Token.TokenType.intlit || lookahead.type == Token.TokenType.floatlit || lookahead.lexeme.equals("(") || lookahead.lexeme.equals("!") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
		if(EXPR() && REPTAPARAMS1()) {
			System.out.println("APARAMS -> EXPR REPTAPARAMS1");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals(")")) {
		System.out.println("APARAMS -> EPSILON");
		 return true;
	}
	else
		return false;
		
	}

public boolean EXPR() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), new HashSet<>(Arrays.asList(")", ";", ",")), lookahead))
        return false;


	if(lookahead.type == Token.TokenType.id || lookahead.type == Token.TokenType.intlit || lookahead.type == Token.TokenType.floatlit || lookahead.lexeme.equals("(") || lookahead.lexeme.equals("!") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
		if(ARITHEXPR() && EXPR2()) {
			System.out.println("EXPR -> ARITHEXPR EXPR2");
			 return true;
		}
		else
			return false;
	}
	else 
		return false;
	}

public boolean EXPR2() {
	if (!skipErrors(new HashSet<>(Arrays.asList("==", "<>", "<", ">", "<=", ">=", "EPSILON")), new HashSet<>(Arrays.asList(")", ";", ",")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("==") || lookahead.lexeme.equals("<>")  || lookahead.lexeme.equals("<")  || lookahead.lexeme.equals(">")  || lookahead.lexeme.equals("<=")  || lookahead.lexeme.equals(">=")) {
		if(RELOP() && ARITHEXPR()) {
			System.out.println("EXPR2 -> RELOP ARITHEXPR");
			 return true;
		}
		else
			return false;
		}
	else if(lookahead.lexeme.equals(")") || lookahead.lexeme.equals(";")  || lookahead.lexeme.equals(",")) {
		System.out.println("EXPR2 -> EPSILON");
		 return true;
	}
	else
		return false;
}

public boolean RELOP() {
	if (!skipErrors(new HashSet<>(Arrays.asList("==", "<>", "<", ">", "<=", ">=")), new HashSet<>(Arrays.asList("!", "+", "-", "intlit", "floatlit", "id", "(")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("==")) {
		if(match("==", lookahead.lexeme)) {
			System.out.println("RELOP -> ==");
			 return true;
		}
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("<>")) {
		if(match("<>", lookahead.lexeme)) {
			System.out.println("RELOP -> <>");
			 return true;
		}
		else
			return false;
	}
	
	
	else if(lookahead.lexeme.equals("<")) {
		if(match("<", lookahead.lexeme)) {
			System.out.println("RELOP -> <");
			 return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals(">")) {
		if(match(">", lookahead.lexeme)) {
			System.out.println("RELOP -> >");
			 return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals("<=")) {
		if(match("<=", lookahead.lexeme)) {
			System.out.println("RELOP -> <=");
			 return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals(">=")) {
		if(match(">=", lookahead.lexeme)) {
			System.out.println("RELOP -> >=");
			 return true;
		}
		else
			return false;
	}
	
	else
		return false;
	
	}

public boolean ARITHEXPR() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), new HashSet<>(Arrays.asList(";", "]", "==", "<>", "<", ">", "<=", ">=", ",", ")")), lookahead))
       return false;
	
	if(lookahead.type == Token.TokenType.id || lookahead.type == Token.TokenType.intlit || lookahead.type == Token.TokenType.floatlit || lookahead.lexeme.equals("(") || lookahead.lexeme.equals("!") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
		if(TERM() && RIGHTRECARITHEXPR()) {
			System.out.println("ARITHEXPR -> TERM RIGHTRECARITHEXPR");
			 return true;
		}
		else
			return false;
	}
	else
		return false;
}

public boolean TERM() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), new HashSet<>(Arrays.asList(";", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
       return false;
	
	if(lookahead.type == Token.TokenType.id || lookahead.type == Token.TokenType.intlit || lookahead.type == Token.TokenType.floatlit || lookahead.lexeme.equals("(") || lookahead.lexeme.equals("!") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
		if(FACTOR() && RIGHTRECTERM()) {
			System.out.println("TERM -> FACTOR RIGHTRECTERM");
			 return true;
		}
		else
			return false;
		}
	else
		return false;
	}

public boolean FACTOR() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), new HashSet<>(Arrays.asList(";","*", "/", "&", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;

	if(lookahead.type == Token.TokenType.id) {
		StringBuilder storage = new StringBuilder();
		if(match("id", lookahead.lexeme,storage) && FACTOR2() && REPTVARORFUNCCALL()) {
			System.out.println("FACTOR -> " + storage + " FACTOR2 REPTVARORFUNCCALL");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.type == Token.TokenType.intlit) {
		StringBuilder storage = new StringBuilder();
		if(match("intlit", lookahead.lexeme,storage)){
			System.out.println("FACTOR -> " + storage);
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.type == Token.TokenType.floatlit) {
		StringBuilder storage = new StringBuilder();
		if(match("floatlit", lookahead.lexeme,storage)){
			System.out.println("FACTOR -> " + storage);
			 return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals("(")) {
		if(match("(", lookahead.lexeme) && ARITHEXPR() &&  match(")", lookahead.lexeme)){
			System.out.println("FACTOR -> ( ARITHEXPR )");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("!")) {
		if(match("!", lookahead.lexeme) && FACTOR()){
			System.out.println("FACTOR -> ! FACTOR ");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
		if(SIGN() && FACTOR()) {
			System.out.println("FACTOR -> SIGN FACTOR");
			 return true;
		}
		else
			return false;
	}
	else
		return false;
	}

public boolean SIGN() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("+", "-")), new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("+")) {
		if(match("+", lookahead.lexeme)) {
			System.out.println("SIGN -> +");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("-")) {
		if(match("-", lookahead.lexeme)) {
			System.out.println("SIGN -> -");
			 return true;
		}
		else
			return false;
	}
	
	else 
		return false;
	
	}

public boolean INDICE() {
	if (!skipErrors(new HashSet<>(Arrays.asList("[")), new HashSet<>(Arrays.asList(";","*", "/", "&", "[", "=", ".", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;


	if(lookahead.lexeme.equals("[")) {
		
		if(match("[", lookahead.lexeme) && ARITHEXPR() && match("]", lookahead.lexeme)) {
			System.out.println("INDICE -> [ ARITHEXPR ] ");
			 return true;
		}
		else
			return false;
	}
	
	else 
		return false;

}

public boolean FACTOR2() {
	if (!skipErrors(new HashSet<>(Arrays.asList("[", "(", "EPSILON")), new HashSet<>(Arrays.asList(";","*", "/", "&", ".", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;

	if(lookahead.lexeme.equals("(")) {
		if(match("(", lookahead.lexeme) && APARAMS() && match(")", lookahead.lexeme)) {
			System.out.println("FACTOR2 -> ( APARAMS ) ");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("[")) {
		if(REPTIDNEST1()) {
			System.out.println("FACTOR2 -> REPTIDNEST1 ");
			 return true;
		}
		else
			return false;
	}

	else if(lookahead.lexeme.equals(";") || lookahead.lexeme.equals("*") || lookahead.lexeme.equals("/") || lookahead.lexeme.equals("&") || lookahead.lexeme.equals(".") || lookahead.lexeme.equals("]") ||  lookahead.lexeme.equals("==") || lookahead.lexeme.equals("<>") || lookahead.lexeme.equals("<") || lookahead.lexeme.equals(">") || lookahead.lexeme.equals("<=") || lookahead.lexeme.equals(">=") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-") || lookahead.lexeme.equals("|") || lookahead.lexeme.equals(",") || lookahead.lexeme.equals(")")){
		System.out.println("FACTOR2 -> EPSILON ");
		return true;
	}
	else
		return false;
}

public boolean REPTIDNEST1() {
	if (!skipErrors(new HashSet<>(Arrays.asList("[", "EPSILON")), new HashSet<>(Arrays.asList("=", ";","*", "/", "&", ".", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("[")) {
	
		if(INDICE() && REPTIDNEST1()) {
			System.out.println("REPTIDNEST1 -> INDICE REPTIDNEST1");
			 return true;
		}
		else
			return false;
		}
	else if(lookahead.lexeme.equals("=") || lookahead.lexeme.equals(";") || lookahead.lexeme.equals("*") || lookahead.lexeme.equals("/") || lookahead.lexeme.equals("&") || lookahead.lexeme.equals(".") || lookahead.lexeme.equals("]") || lookahead.lexeme.equals("==") || lookahead.lexeme.equals("<>") || lookahead.lexeme.equals("<") || lookahead.lexeme.equals(">") || lookahead.lexeme.equals("<=") || lookahead.lexeme.equals(">=") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-") || lookahead.lexeme.equals("|") || lookahead.lexeme.equals(",") || lookahead.lexeme.equals(")")) {
		System.out.println("REPTIDNEST1 -> EPSILON");
		 return true;
	}

	else 
		return false;

}

public boolean REPTVARORFUNCCALL() {
	if (!skipErrors(new HashSet<>(Arrays.asList(".", "EPSILON")), new HashSet<>(Arrays.asList(";","*", "/", "&", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;

	if(lookahead.lexeme.equals(".")) {
		if(IDNEST() && REPTVARORFUNCCALL()) {
			System.out.println("REPTVARORFUNCCALL -> IDNEST REPTVARORFUNCCALL");
			 return true;
		}
		else 
			return false;
	}
	
	else if(lookahead.lexeme.equals(";") || lookahead.lexeme.equals("*")|| lookahead.lexeme.equals("]") || lookahead.lexeme.equals("==") || lookahead.lexeme.equals("<>") || lookahead.lexeme.equals("<") || lookahead.lexeme.equals(">") || lookahead.lexeme.equals("<=") || lookahead.lexeme.equals(">=") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-") || lookahead.lexeme.equals("|") || lookahead.lexeme.equals(",") || lookahead.lexeme.equals(")")) {
		System.out.println("REPTVARORFUNCCALL -> EPSILON");
		 return true;
		
	}
	
	else
		return false;

}

public boolean IDNEST() {
	if (!skipErrors(new HashSet<>(Arrays.asList(".")), new HashSet<>(Arrays.asList(".", ";","*", "/", "&", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;

	if(lookahead.lexeme.equals(".")){
		StringBuilder storage = new StringBuilder();
		if(match(".", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && IDNEST2()) {
			System.out.println("IDNEST -> . " + storage + " IDNEST2");
			 return true;
		}
		else 
			return false;
	}
	else
		return false;
}


public boolean IDNEST2() {
	if (!skipErrors(new HashSet<>(Arrays.asList("(", "[")), new HashSet<>(Arrays.asList(".", ";","*", "/", "&", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("(")) {
		if(match("(", lookahead.lexeme) && APARAMS() && match(")", lookahead.lexeme)) {
			System.out.println("IDNEST2 -> ( APARAMS )");
			 return true;
		}
		else 
			return false;
	}
	else if(lookahead.lexeme.equals("[")){
		
		if(REPTIDNEST1()) {
			System.out.println("IDNEST2 -> REPTIDNEST1");
			 return true;
		}
		else 
			return false;
	}
	else
		return false;
}

public boolean STATEMENTIDNEST2() {
	if (!skipErrors(new HashSet<>(Arrays.asList(".", "EPSILON")), new HashSet<>(Arrays.asList(";")), lookahead))
       return false;

	 if(lookahead.lexeme.equals(".")) {
		StringBuilder storage = new StringBuilder();
		if(match(".", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && STATEMENTIDNEST()) {
			System.out.println("STATEMENTIDNEST2 -> . " + storage + " STATEMENTIDNEST");
			 return true;
		}
		else 
			return false;
	}
	else if(lookahead.lexeme.equals(";")){
		System.out.println("STATEMENTIDNEST2 -> EPSILON");
		 return true;
	}
	
	else
		return false;
	}

public boolean REPTAPARAMS1() {
	if (!skipErrors(new HashSet<>(Arrays.asList(",", "EPSILON")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;

	if(lookahead.lexeme.equals(",")) {
		if(APARAMSTAIL() && REPTAPARAMS1()) {
			System.out.println("REPTAPARAMS1 -> APARAMSTAIL REPTAPARAMS1");
			 return true;
		}
		
	else
		return false;
	}
	
	else if(lookahead.lexeme.equals(")")){
		System.out.println("REPTAPARAMS1 -> EPSILON");
		 return true;
	}
	else 
		return false;
	
}

public boolean APARAMSTAIL() {
	if (!skipErrors(new HashSet<>(Arrays.asList(",")), new HashSet<>(Arrays.asList(")", ",")), lookahead))
       return false;

	if(lookahead.lexeme.equals(",")) {
		if(match(",", lookahead.lexeme) && EXPR()) {
			System.out.println("REPTAPARAMS1 -> EPSILON");
			 return true;
		}
		else 
			return false;
		}
	
   else 
	return false;

}

public boolean ASSIGNOP() {
	if (!skipErrors(new HashSet<>(Arrays.asList("=")), new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), lookahead))
        return false;

	if(lookahead.lexeme.equals("=")) {
		if(match("=", lookahead.lexeme)) {
			System.out.println("ASSIGNOP -> =");
			 return true;
		}
		else 
			return false;
		}
	else
		return true;
}

public boolean STATEMENTIDNEST3() {
	if (!skipErrors(new HashSet<>(Arrays.asList("=", ".")), new HashSet<>(Arrays.asList(";")), lookahead))
       return false;


	if(lookahead.lexeme.equals(".")) {
		StringBuilder storage = new StringBuilder();
		if(match(".", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && STATEMENTIDNEST()){
			System.out.println("STATEMENTIDNEST3 -> . " + storage + " STATEMENTIDNEST");
			 return true;
		}
		else 
			return false;
	}
	else if(lookahead.lexeme.equals("=")) {
		if(ASSIGNOP() && EXPR()){
			System.out.println("STATEMENTIDNEST3 -> . id STATEMENTIDNEST");
			 return true;
		}
		else 
			return false;
	}
	else
		return false;
	}

public boolean RIGHTRECARITHEXPR() {
	if (!skipErrors(new HashSet<>(Arrays.asList("+", "-", "|", "EPSILON")), new HashSet<>(Arrays.asList(";", "]", "==", "<>", "<", ">", "<=", ">=", ",", ")")), lookahead))
        return false;

	if(lookahead.lexeme.equals("+") || (lookahead.lexeme.equals("-")) || (lookahead.lexeme.equals("|"))){
		if(ADDOP() && TERM() && RIGHTRECARITHEXPR()) {
			System.out.println("RIGHTRECARITHEXPR -> ADDOP TERM RIGHTRECARITHEXPR");
			 return true;
		}
		else 
			return false;
		}
	
	else if(lookahead.lexeme.equals(";")|| lookahead.lexeme.equals("]") || lookahead.lexeme.equals("==") || lookahead.lexeme.equals("<>") || lookahead.lexeme.equals("<") || lookahead.lexeme.equals(">") || lookahead.lexeme.equals("<=") || lookahead.lexeme.equals(">=")|| lookahead.lexeme.equals(",") || lookahead.lexeme.equals(")")) {
		System.out.println("RIGHTRECARITHEXPR -> EPSILON");
		 return true;
	}
	else
		return false;
		
}

public boolean ADDOP() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("+", "-", "|")), new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), lookahead))
        return false;

	if(lookahead.lexeme.equals("+")) {
		if(match("+", lookahead.lexeme)) {
			System.out.println("ADDOP -> +");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("-")) {
		if(match("-", lookahead.lexeme)) {
			System.out.println("ADDOP -> -");
			 return true;
		}
		else
			return false;
	}
	else if(lookahead.lexeme.equals("|")) {
		if(match("|", lookahead.lexeme)) {
			System.out.println("ADDOP -> |");
			 return true;
		}
		else
			return false;
	}
	else
		return false;

	
	}

public boolean RIGHTRECTERM() {

	if (!skipErrors(new HashSet<>(Arrays.asList("*", "/", "&", "EPSILON")), new HashSet<>(Arrays.asList(";", "]", "==", "<>", "<", ">", "<=", ">=", "+", "-", "|", ",", ")")), lookahead))
        return false;

	if(lookahead.lexeme.equals("*") || lookahead.lexeme.equals("/") || lookahead.lexeme.equals("&")) {
		if(MULTOP() && FACTOR() && RIGHTRECTERM()) {
		System.out.println("RIGHTRECTERM -> MULTOP FACTOR RIGHTRECTERM");
		 return true;
		}
		 else
			 return false;
	
	}
	else if(lookahead.lexeme.equals(";") || lookahead.lexeme.equals("]") || lookahead.lexeme.equals("==") || lookahead.lexeme.equals("<>") || lookahead.lexeme.equals("<") || lookahead.lexeme.equals(">") || lookahead.lexeme.equals("<=") || lookahead.lexeme.equals(">=") ||  lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-") || lookahead.lexeme.equals("|") || lookahead.lexeme.equals(",") || lookahead.lexeme.equals(")")) {
		System.out.println("RIGHTRECTERM -> EPSILON");
		 return true;
	}
	else
		return false;


}

public boolean MULTOP() {
	if (!skipErrors(new HashSet<>(Arrays.asList("*", "/", "&")), new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), lookahead))
        return false;

	if(lookahead.lexeme.equals("*")) {
		if(match("*", lookahead.lexeme)){
			System.out.println("MULTOP -> *");
			 return true;
		}
		else 
			return false;
	}
	
	else if(lookahead.lexeme.equals("/")) {
		if(match("/", lookahead.lexeme)){
			System.out.println("MULTOP -> /");
			 return true;
		}
		else 
			return false;
	}
	
	else if(lookahead.lexeme.equals("&")) {
		if(match("&", lookahead.lexeme)){
			System.out.println("MULTOP -> &");
			 return true;
		}
		else 
			return false;
	}

	else
		return false;

}

public boolean VARIABLE() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;
	
	if(lookahead.type == Token.TokenType.id) {
		StringBuilder storage = new StringBuilder();
		if(match("id", lookahead.lexeme,storage)) {
			System.out.println("VARIABLE -> " + storage);
			 return true;
		}
		else 
			return false;
			
	}
	
	else
		return false;

}

public boolean VARIABLE2() {
	if (!skipErrors(new HashSet<>(Arrays.asList("(", "[", ".")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("(")) {
		if(match("(", lookahead.lexeme)  && APARAMS() && (match(")", lookahead.lexeme)) && VARIDNEST()){
			System.out.println("VARIABLE2 -> ( APARAMS ) VARIDNEST");
			 return true;
		}
		else 
			return false;
			
		}
	
	else if(lookahead.lexeme.equals("[")) {
		if(REPTIDNEST1() && REPTVARIABLE()) {
			System.out.println("VARIABLE2 -> REPTIDNEST1 REPTVARIABLE");
			 return true;
		}
		else 
			return false;
		}
	
	
	else
		return false;
	
	}
	

public boolean REPTVARIABLE() {
	if (!skipErrors(new HashSet<>(Arrays.asList(".", "EPSILON")), new HashSet<>(Arrays.asList(")")), lookahead))
       return false;

	
	if(lookahead.lexeme.equals(".")) {
		if(VARIDNEST() && REPTVARIABLE()) {
			System.out.println("REPTVARIABLE -> VARIDNEST REPTVARIABLE");
			 return true;
		}
		
		else 
			return false;
	}
	
	else if(lookahead.lexeme.equals(")")) {
		System.out.println("REPTVARIABLE -> EPSILON");
		 return true;
	}
	
	else
		return false;
	}


public boolean VARIDNEST() {
	if (!skipErrors(new HashSet<>(Arrays.asList(".")), new HashSet<>(Arrays.asList(")",".")), lookahead))
       return false;
	
	
	if(lookahead.lexeme.equals(".")) {
		StringBuilder storage = new StringBuilder();
		if(match(".", lookahead.lexeme) && match("id", lookahead.lexeme,storage) && VARIDNEST2()) {
			System.out.println("VARIDNEST -> . " + storage + " VARIDNEST2");
			 return true;
		}
		
		else
			return false;
	}
	
	else
		return false;
	}



public boolean VARIDNEST2() {
	if (!skipErrors(new HashSet<>(Arrays.asList("(", "[")), new HashSet<>(Arrays.asList(")",".")), lookahead))
       return false;
	
	if(lookahead.lexeme.equals("(")){
		if(match("(", lookahead.lexeme) && APARAMS() && match(")", lookahead.lexeme) && VARIDNEST()) {
			System.out.println("VARIDNEST2 -> ( APARAMS ) VARIDNEST");
			 return true;
		}
		
		else
			return false;	
		}
	
	else if(lookahead.lexeme.equals("[")){
		
		if(REPTIDNEST1()) {
			System.out.println("VARIDNEST2 -> REPTIDNEST1");
			 return true;
		}
		
		else
			return false;
		}
	
	else
		return false;
}

public boolean VARDECLORSTAT() {
	if (!skipErrors(new HashSet<>(Arrays.asList("let", "id", "if", "while", "read", "write", "return")), new HashSet<>(Arrays.asList("let", "id", "if", "while", "read", "write", "return", "}")), lookahead))
       return false;
	
	if(lookahead.type == Token.TokenType.id || lookahead.lexeme.equals("if") || lookahead.lexeme.equals("while") || lookahead.lexeme.equals("read") || lookahead.lexeme.equals("write") || lookahead.lexeme.equals("return")) {
		if(STATEMENT()) {
			System.out.println("VARDECLORSTAT -> STATEMENT");
			 return true;
		}
		
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals("let")) {
		if(VARDECL()) {
			System.out.println("VARDECLORSTAT -> VARDECL");
			 return true;
		}
		
		else
			return false;
	}
	
	else
		return false;
	
	}

public boolean STATEMENT() {
	if (!skipErrors(new HashSet<>(Arrays.asList( "id", "if", "while", "read", "write", "return")), new HashSet<>(Arrays.asList("let", "id", "else", ";", "read", "write", "if", "while", "return", "}")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("if")){
		
		if(match("if", lookahead.lexeme) && match("(", lookahead.lexeme) && RELEXPR() && match(")", lookahead.lexeme) && match("then", lookahead.lexeme) && STATBLOCK() && match("else", lookahead.lexeme) && STATBLOCK() && match(";", lookahead.lexeme)) {
			System.out.println("STATEMENT -> if ( RELEXPR ) then STATBLOCK else STATBLOCK ;");
			 return true;
		}
		
		else
			return false;
		}

	else if(lookahead.type == Token.TokenType.id){
		StringBuilder storage = new StringBuilder();
		if(match("id", lookahead.lexeme,storage) && STATEMENTIDNEST() && match(";", lookahead.lexeme)){
			System.out.println("STATEMENT -> " + storage + " STATEMENTIDNEST ;");
			return true;
		}
		else
			return false;
	}
	
	else if(lookahead.lexeme.equals("while")) {
		
		if(match("while", lookahead.lexeme) && match("(", lookahead.lexeme) && RELEXPR() && match(")", lookahead.lexeme)  && STATBLOCK() &&CHECKER() && match(";", lookahead.lexeme)) {
			System.out.println("STATEMENT -> while ( RELEXPR ) STATBLOCK ;");
			 return true;
		}
		
		else
			return false;
		}
	
	
	else if(lookahead.lexeme.equals("read")){
		
		if(match("read", lookahead.lexeme) && match("(", lookahead.lexeme) && VARIABLE() && match(")", lookahead.lexeme) && match(";", lookahead.lexeme)) {
	
			System.out.println("STATEMENT -> read ( VARIABLE ) ;");
			 return true;
		}
		
		else
			return false;
		}
	
	
	else if(lookahead.lexeme.equals("write")) {
		if(match("write", lookahead.lexeme) && match("(", lookahead.lexeme) && EXPR() && match(")", lookahead.lexeme) && match(";", lookahead.lexeme)) {
			
			System.out.println("STATEMENT -> write ( EXPR ) ;");
			 return true;
		}
		
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("return")) {
		if(match("return", lookahead.lexeme) && match("(", lookahead.lexeme) && EXPR() && match(")", lookahead.lexeme) && match(";", lookahead.lexeme)) {
			
			System.out.println("STATEMENT -> return ( EXPR ) ;");
			 return true;
		}
		
		else
			return false;
		}
	
	else 
		return false;
	
	
	
	}
	
public boolean RELEXPR() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "intlit", "floatlit", "(", "!", "+", "-")), new HashSet<>(Arrays.asList(")")), lookahead))
        return false;
	
	if(lookahead.type == Token.TokenType.id || lookahead.type == Token.TokenType.intlit || lookahead.type == Token.TokenType.floatlit || lookahead.lexeme.equals("(") || lookahead.lexeme.equals("!") || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
		
		if(ARITHEXPR() && RELOP() && ARITHEXPR()) {

			System.out.println("RELEXPR -> ARITHEXPR RELOP ARITHEXPR");
			 return true;
		}
		
		else
			return false;
	}
	
	else
		return false;
	
	}

public boolean STATBLOCK() {
	
	if (!skipErrors(new HashSet<>(Arrays.asList("{", "id", "if", "while", "read", "write", "return", "EPSILON")), new HashSet<>(Arrays.asList("else", ";")), lookahead))
        return false;
	
	
	if(lookahead.lexeme.equals("{")) {
		if(match("{", lookahead.lexeme) && REPTSTATBLOCK1() && match("}", lookahead.lexeme)) {
			System.out.println("STATBLOCK -> { REPTSTATBLOCK1 }");
			 return true;
		}
		
		else
			return false;
		}
	
	else if(lookahead.type == Token.TokenType.id || lookahead.lexeme.equals("if") || lookahead.lexeme.equals("while") || lookahead.lexeme.equals("read") | lookahead.lexeme.equals("write") || lookahead.lexeme.equals("return")) {
		if(STATEMENT()) {
			System.out.println("STATBLOCK -> STATEMENT");
			 return true;
		}
		
		else
			return false;
		}
	
	else if(lookahead.lexeme.equals("else") || lookahead.lexeme.equals(";")) {
		System.out.println("STATBLOCK -> EPSILON");
		 return true;
	
	}
	
	else
		return false;
	
}

public boolean REPTSTATBLOCK1() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "if", "while", "read", "write", "return", "EPSILON")), new HashSet<>(Arrays.asList("}")), lookahead))
        return false;
	
	if(lookahead.type == Token.TokenType.id || lookahead.lexeme.equals("if") || lookahead.lexeme.equals("while") || lookahead.lexeme.equals("read") | lookahead.lexeme.equals("write") || lookahead.lexeme.equals("return")) {
	
		if(STATEMENT() && REPTSTATBLOCK1()) {
		System.out.println("REPTSTATBLOCK1 -> STATEMENT REPTSTATBLOCK1");
		 return true;
	
	}
	
	else
		return false;
	
	}
	
	else if(lookahead.lexeme.equals("}")) {
		System.out.println("REPTSTATBLOCK1 -> EPSILON");
		 return true;
	
	}
	
	else
		return false;
	
}

public boolean REPTFUNCBODY1() {
	if (!skipErrors(new HashSet<>(Arrays.asList("id", "let", "while", "read", "write", "return" , "if", "EPSILON")), new HashSet<>(Arrays.asList("}")), lookahead))
        return false;
	
	if(lookahead.lexeme.equals("let") || lookahead.type == Token.TokenType.id || lookahead.lexeme.equals("if") || lookahead.lexeme.equals("while") || lookahead.lexeme.equals("read") | lookahead.lexeme.equals("write") || lookahead.lexeme.equals("return")) {
		if((VARDECLORSTAT() && REPTFUNCBODY1())) {
		System.out.println("REPTFUNCBODY1 -> VARDECLORSTAT REPTFUNCBODY1");
		 return true;
	
	}
	
	else
		return false;
	}
	
	else if(lookahead.lexeme.equals("}")) {
		System.out.println("REPTFUNCBODY1 -> EPSILON");
		 return true;
	}
	
	else
		return false;
		
}


}




