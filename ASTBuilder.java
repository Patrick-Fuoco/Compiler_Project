import java.util.Stack;

public class ASTBuilder{

    // Stack to hold the nodes
    private Stack<Node> semanticStack = new Stack<>();


    public boolean createLeaf(String lexeme){
        Node leaf = new Node(lexeme); // Assume Node is a class that represents a node in your AST
        semanticStack.push(leaf);
        return true;
    }

    public boolean createLeaf(StringBuilder lexeme){
        Node leaf = new Node(lexeme.toString()); // Assume Node is a class that represents a node in your AST
        semanticStack.push(leaf);
        return true;
    }
    public boolean PUSH_EPSILON(){
        Node leaf = new Node("EPSILON");
        semanticStack.push(leaf);
        return true;
    }

    // Function to create a subtree node and push onto the stack
    public boolean createSubtree(String name, int pops) {
        Node subtree = new Node(name);
        
        if (pops == 0) {
            while (!semanticStack.isEmpty() && !semanticStack.peek().name.equals("EPSILON")) {
                Node child = semanticStack.pop();
                subtree.addChild(child); // This will add the rightmost child first.
            }
            if (!semanticStack.isEmpty() && semanticStack.peek().name.equals("EPSILON")) {
                semanticStack.pop(); // Remove the "EPSILON" marker from the stack.
            }
        } else {
            for (int i = 0; i < pops; i++) {
                if (!semanticStack.isEmpty()) {
                    Node child = semanticStack.pop();
                    subtree.addChild(child); // This will add the rightmost child first.
                } else {
                    // Handle error: not enough nodes on the stack to create subtree
                    System.out.println("PROBLEM WITH TREE");
                    return false; // Or throw an exception, depending on your error handling strategy
                }
            }
        }
        
        // Push the constructed subtree back onto the stack.
        semanticStack.push(subtree);
        
        return true;
    }

    public void printTree() {
        if (semanticStack.isEmpty()) {
            System.out.println("The AST is empty.");
        } else {
            Node root = semanticStack.peek(); // The root should be the last node remaining on the stack
            root.printSubtree(0); // Start printing from the root with an initial indentation level of 0
        }
    }

    // A simple Node class to represent nodes in the AST
    static class Node {
        String name;
        Stack<Node> children = new Stack<>();

        Node(String name) {
            this.name = name;
        }

        void addChild(Node child) {
            children.push(child);
        }

        // Optional: a method to pretty print the subtree for this node
        void printSubtree(String prefix) {
            System.out.println(prefix + name);
            children.forEach(child -> child.printSubtree(prefix + "  "));
        }

        void printSubtree(int level) {
            String indent = "|".repeat(level); // Create an indent string using the level for nested nodes
            System.out.println(indent + name);  // Print the current node with the appropriate indentation
            for (Node child : children) {
                child.printSubtree(level + 1);   // Recursively print each child, increasing the indentation level
            }
        }
    }
}