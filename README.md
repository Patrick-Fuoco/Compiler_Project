# Compiler Project 0️⃣ 1️⃣ 0️⃣ 1️⃣

👋 Welcome to my Compiler Project! This compiler tackles a custom language defined by our professor. I chose to write it using the Java programming language as this is what I am most familiar with. It consists of:  
1. Lexical Analyzer  
2. Syntactic Parser (CST builder)  
3. Semantic Parser (AST builder)

⚡ How It Works  
Tokenize: Lexical analyzer converts source code into tokens defined by the custom progamming language.  
Parse: Syntaxtic Parser checks if the tokens follow the rules of the grammar, and builds the CST.  
Analyze: Semantic Parser creates a tree that retains semantic meaning, getting the compiler ready for symbol-table generation, which is the fourth phase of compilation and the introduction to the "back-end" unit!  

🌳 Final Result: Abstract Syntax Tree (AST)  
The AST represents your code in tree form (think of it like a blueprint 🛠️).  
Each node represents things like variables, functions, loops, and more! 🌐  

This AST should then undergo two more compilation phases which were not included in my project (Symbol table generation and Code generation.)  



