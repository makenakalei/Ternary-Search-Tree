package main.textfiller;

import java.util.*;
import java.util.ArrayList;
/**
 * A ternary-search-tree implementation of a text-autocompletion
 * trie, a simplified version of some autocomplete software.
 * @author Makena Robison
 */
public class TernaryTreeTextFiller implements TextFiller {

    // Fields
    // -----------------------------------------------------------
    private TTNode root;
    private int size;
    private ArrayList<String> words = new ArrayList<String>();
    
    // Constructor
    // -----------------------------------------------------------
    public TernaryTreeTextFiller () {
        this.root = null;
        this.size = 0;
    }
    
    
    // Methods
    // -----------------------------------------------------------
    
    public int size () {
		return this.size;   	// returns amount of words in the tree.
    }

    public boolean empty () {
    	boolean empty = false; //checks if tree is empty
        if(this.size <= 0) {
        	empty = true;
        }
        return empty;
        
    }
    
    public void add (String toAdd) {
    	toAdd = normalizeTerm(toAdd); //normalizes string
    	if(!contains(toAdd)) { //checks if string is in the tree
	    	int index = 0;
	    	this.root = build(toAdd, this.root, index); //calls helper
    	}
    }
    
    public boolean contains (String query) {
    	
        query = normalizeTerm(query);
        char[] characters = strToChars(query); //creates character array
        int index = 0;
        TTNode current = this.root;
        if (empty()) {
        	return false;
        }
        while (current != null) { //iterates through char array and compares to the current node as long as node != null
        	if (characters[index] == current.letter) {
        		if (index == characters.length - 1) { //compares if word end
        			if (current.wordEnd) {
        			return true;
        		} else {
        			return false;
        	    }
        		}
        		index++;
        		current = current.mid; //updates node
        		
        	} else {
        		int comparison = compareChars(characters[index], current.letter); //moves to next potential word branch
        				
        		if (comparison < 0) {
        			current = current.left;
        		} else {
        			current = current.right;
        		}
        		
        	}
        }
        return false; 
    }
    
    public String textFill (String query) { //finishes string with what query is the prefix of
        
        int index = 0;
        String fill = "";
        fill = textFillHelp(query, index, this.root, fill); //calls helper method
        return fill;
    }
    
    public List<String> getSortedList () { //returns an array list of all words in the tree
    	String s = "";
    	getSortedHelp(this.root, s); //calls helper method
        return words;
    }
    
    
    // Private Helper Methods
    // -----------------------------------------------------------
    
    /**
     * Normalizes a term to either add or search for in the tree,
     * since we do not want to allow the addition of either null or
     * empty strings within, including empty spaces at the beginning
     * or end of the string (spaces in the middle are fine, as they
     * allow our tree to also store multi-word phrases).
     * @param s The string to sanitize
     * @return The sanitized version of s
     */
    private String normalizeTerm (String s) {
        // Edge case handling: empty Strings illegal
        if (s == null || s.equals("")) {
            throw new IllegalArgumentException();
        }
        return s.trim().toLowerCase();
    }
    
    /**
     * Given two characters, c1 and c2, determines whether c1 is
     * alphabetically less than, greater than, or equal to c2
     * @param c1 The first character
     * @param c2 The second character
     * @return
     *   - some int less than 0 if c1 is alphabetically less than c2
     *   - 0 if c1 is equal to c2
     *   - some int greater than 0 if c1 is alphabetically greater than c2
     */
    private int compareChars (char c1, char c2) {
        return Character.toLowerCase(c1) - Character.toLowerCase(c2);
    }
    
    // [!] Add your own helper methods here!
    private char[] strToChars (String s) { //converts string to char[]
    	char[] c = new char[s.length()];
    	for (int i = 0; i < s.length(); i++) {
    		c[i] = s.charAt(i);
    	}
    	return c;
    }
    
     
    
    private TTNode build (String toAdd, TTNode current, int index) { //add helper method
		
		
		if (current == null) {
			boolean endChar = index >= toAdd.length()-1;
    		current = new TTNode(toAdd.charAt(index), endChar);
    		
    		if (endChar) { //ends add if endChar == true
    			this.size++;
    			return current;
    		}
    		
    	}
		int comparison = compareChars(toAdd.charAt(index), current.letter); // compares characters
		if (comparison < 0) {
			current.left = build(toAdd, current.left, index); //adds word to the left of the current node
		} else if (comparison > 0){
			current.right = build(toAdd, current.right, index); //adds word to the right of the current node
		} else {
			current.mid = build(toAdd, current.mid, index+1);//adds word to the mid of the current node
		}
		return current;
    }
    	
    // helper method for textFill
    private String textFillHelp(String query, int index, TTNode current, String fill){ 
    	char[] characters = strToChars(query);
    	int length = characters.length;
    	if (!prefixCheck(query)) { //checks if the string is in the tree 
    		return null;
    	}
    	if (empty()) {
    		return fill;
    	}
    	
    	if (index >= length-1 && compareChars(characters[index], current.letter) == 0) { //fills in the rest of the word outside of query
    		while (!current.wordEnd) {
    			fill = fill + current.letter;
    			current = current.mid;
    		}
    		fill = fill + current.letter;
    		return fill;
    	}
    	
    	int comparison = compareChars(characters[index], current.letter); 
    	//adds query to list once found in the tree
    	if (comparison == 0) {
    		fill = fill + current.letter;
    		return textFillHelp(query, index + 1, current.mid, fill);
    	} else if (comparison < 0) {
    		return textFillHelp(query, index, current.left, fill);
    	} else {
    		return textFillHelp(query, index, current.right, fill);
    	} 
    }
    
    // helper method for getSortedList
    private void getSortedHelp(TTNode current, String s) {
    	if (current == null) {
    		return;
    	} 
    	getSortedHelp(current.left, s ); //goes to left most branch 
    	if (current.wordEnd) { // adds completed word to list
    		words.add(s+current.letter);
    	}
    	getSortedHelp(current.mid, s+current.letter); // adds to string going down the mid pointer of the nodes
    	getSortedHelp(current.right, s); //moves to the right most branches
    	
    }
    
    // helper method to check if string exists in tree
    private boolean prefixCheck(String query) {
    	query = normalizeTerm(query);
        char[] characters = strToChars(query);
        int index = 0;
        TTNode current = this.root;
        if (empty()) {
        	return false;
        }
        while (current != null) {
        	if (characters[index] == current.letter) {
        		if (index == characters.length - 1) {
        		    return true;
        		}
        		index++;
        		current = current.mid;
        		
        	} else {
        		int comparison = compareChars(characters[index], current.letter);
        				
        		if (comparison < 0) {
        			current = current.left;
        		} else {
        			current = current.right;
        		}
        		
        	}
        }
        return false;
    }
     
    

    
    
    // TTNode Internal Storage
    // -----------------------------------------------------------
    
    /**
     * Internal storage of textfiller search terms
     * as represented using a Ternary Tree (TT) with TTNodes
     * [!] Note: these are currently implemented for the base-assignment;
     *     those endeavoring the extra-credit may need to make changes
     *     below (primarily to the fields and constructor)
     */
    private class TTNode {
        
        boolean wordEnd;
        char letter;
        TTNode left, mid, right;
        
        /**
         * Constructs a new TTNode containing the given character
         * and whether or not it represents a word-end, which can
         * then be added to the existing tree.
         * @param letter Letter to store at this node
         * @param wordEnd Whether or not this is a word-ending letter
         */
        TTNode (char letter, boolean wordEnd) {
            this.letter  = letter;
            this.wordEnd = wordEnd;
        }
        
    }
    
}
