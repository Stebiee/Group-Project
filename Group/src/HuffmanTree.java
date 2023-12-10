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
 * Creates a Huffman tree and allows for encoding and decoding of files. 
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
     * This class is a node in the HuffmanTree. 
     */
    public class Node implements Serializable, Comparable<Node>{
        private static final long serialVersionUID = 1L;
        private Node left;
        private Node right;
        private int frequency;
        
        /**
         * Creates a new node with the given left and right children.
         * @param leftNode Left child.
         * @param rightNode Right child. 
         */
        public Node(Node leftNode, Node rightNode) {
            this.left = leftNode;
            this.right = rightNode;
            this.frequency = leftNode.frequency + rightNode.frequency;
        }

        /**
         * Creates a new node with a given frequency.
         * @param frequency The frequency of the node.
         */        
        public Node(int frequency) {
            this.frequency = frequency;
        }
        
        /**
         * Compares this node with the input node.
         * @param node The input node to be compared with. 
         * @return Verifies if this node is less than, greater than or equal to the input node and 
         * 		   accordingly returns an integer that is negative, positive or zero respectively. 
         */
        @Override
        public int compareTo(Node node) {
            return Integer.compare(frequency, node.frequency);
        }
    }

    /**
     * This implements the Leaf class in the Huffman Tree. 
     */    
    public class Leaf extends Node {
        private static final long serialVersionUID = 1L;
        private final char CHARACTER;
 
        /**
         * Creates a Leaf node with a given character and frequency.
         * @param character Character for this leaf node. 
         * @param frequency Frequency of the character.
         */
        public Leaf(char character, int frequency) {
            super(frequency);
            this.CHARACTER = character;
        }
    }

    /**
     * Create a HuffmanTree from a given input file. 
     * @param inputFile The input file.
     * @throws IOException An exception is thrown if an I/O error occurs.
     */
    public HuffmanTree(File inputFile) throws IOException{
        this.file = inputFile;
        encode();
    }

    /**
     * Gets the frequency of each character in the file. 
     * @throws IOException An exception is thrown if an I/O error occurs.
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
     * @throws IOException An exception is thrown if an I/O error occurs.
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
     * Generates the Huffman Codes by traversing the tree. 
     * @param node The current node.
     * @param code The Huffman code.
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
     * Gets a map of the Huffman Codes.
     * @return Returns a map of the Huffman codes.
     */
    public Map<Character, String> getHuffmanCodes() {
        return huffmanCodes;
    }
}
