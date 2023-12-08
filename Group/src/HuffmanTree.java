import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Creates a Huffman tree and allows for decoding and returning the codes
 * @since 11-27-2023
 * @author Terry Ton, Esteban Madrigal , Manvir Hansra, Kushaan Naskar, Vinh Tran 
 */
public class HuffmanTree implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final char EOF = '\uFFFF';
    private Node root;
    private final File file;
    private Map<Character, Integer> charFrequencies = new HashMap<>();
    private Map<Character, String> huffmanCodes = new HashMap<>();

    /**
    * frequency is the occurrence of a character in list
    * parent nodes store the sum of frequency of children
    */
    public class Node implements Serializable, Comparable<Node>{
        private static final long serialVersionUID = 1L;
        private Node left;
        private Node right;
        private int frequency;
        
        /**
         * updates frequency to be sum between left and right nodes
         * @param leftNode
         * @param rightNode
         */
        public Node(Node leftNode, Node rightNode) {
            this.left = leftNode;
            this.right = rightNode;
            this.frequency = leftNode.frequency + rightNode.frequency;
        }
    
        public Node(int frequency) {
            this.frequency = frequency;
        }
        
        /**
         * @param node
         * @return whether this.frequency is larger than node.frequency
         */
        @Override
        public int compareTo(Node node) {
            return Integer.compare(frequency, node.frequency);
        }
    }

    public class Leaf extends Node {
        private static final long serialVersionUID = 1L;
        private final char CHARACTER;
    
        public Leaf(char character, int frequency) {
            super(frequency);
            this.CHARACTER = character;
        }
    }

    /**
     * Call the the encode method to create the HuffmanTree
     */

    public HuffmanTree(File inputFile) throws IOException{
        this.file = inputFile;
        encode();
    }

    /**
     * Keep counts of characters and their frequencies in the text
     */
    private void getCharFrequencies() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Character c;

        while (reader.ready()) {
            c = (char) reader.read();
            if (charFrequencies.containsKey(c)) {
                charFrequencies.put(c, charFrequencies.get(c) + 1);
            } else {
                charFrequencies.put(c, 1);
            }
        }
        reader.close();
    }

    /**
     * Encode the data into a Huffman Tree
     */

    private void encode() throws IOException {
        getCharFrequencies();
        charFrequencies.put(EOF, 1);
        Queue<Node> queue = new PriorityQueue<>();
        charFrequencies.forEach((character, frequency) -> queue.add(new Leaf(character, frequency)));
        while (queue.size() > 1) {
            queue.add(new Node(queue.poll(), queue.poll()));
        }
        generateHuffmanCodes(root = queue.poll(), "");
    }
    
    /**
     * Decodes the given encoded string.
     * @param bits The encoded bits to be decoded.
     * @return The decoded string.
     */
    public String decode(String bits) {
        Node current = root;
        StringBuilder decodeStr = new StringBuilder();

        for (char bit : bits.toCharArray()) {
        	if (bit == '0')
        		current = current.left;
        	else
        		current = current.right;

            if (current.left == null && current.right == null) {
            	if (((Leaf) current).CHARACTER == EOF)
                    break;  
            	decodeStr.append(((Leaf)current).CHARACTER);
                current = root;
            }
        }

        return decodeStr.toString();
    }     

    /**
     * Generate the HuffmanCodes tree from the given string 
     * @param node The current node
     * @param code The text which need to be added
     */
    private void generateHuffmanCodes(Node node, String code) {
        if (node instanceof Leaf) {
            huffmanCodes.put(((Leaf)node).CHARACTER, code);
            return;
        }
        generateHuffmanCodes(node.left, code.concat("0"));
        generateHuffmanCodes(node.right, code.concat("1"));
    }

    /**
     * HuffmanCodes getter
     */
    public Map<Character, String> getHuffmanCodes() {
        return huffmanCodes;
    }
}
