import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class BehaviorResponse {
    private static ArrayList<String> responses = new ArrayList<>();

    public static void main(String[] args) {
        String input;
        Boolean quit = false;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String file = "input.xml";
            Document document = builder.parse(new File(file));
            document.getDocumentElement().normalize();
            NodeList nodes = document.getChildNodes();
            Node rootN = document.getLastChild();

            System.out.println("Behavior Tree Loaded...");
            printTree(document, "");

            Scanner in = new Scanner(System.in);

            // 1. Get a behavior input
            // 2. Search the tree for behavior and associated responses
            // 3. Randomly select a response to output
            // 4. Repeat while input is not "quit"
            while (!quit) {
                System.out.print("Event ('quit' to exit): ");
                input = in.nextLine();
                if (input.equalsIgnoreCase("quit"))
                    quit = true;
                else if(!input.isEmpty()) {
                    bSrch(input, nodes);
                    //dSrch(input, rootN);
                    System.out.print("Response = ");
                    if (responses.isEmpty())
                        System.out.println("");
                    else
                        System.out.println((responses.get((int) (Math.random() * (responses.size())))));
                    responses.clear();

                    // DELETE/COMMENT NEXT LINE FOR CONTINUED PROCESS OPERATION
                    //quit = true;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Recursive method to print all of the elements of the tree
    private static void printTree(Node node, String indent) {
        if (node.hasChildNodes()) {
            NodeList nl = node.getChildNodes();
            for (int n = 0; n < nl.getLength(); n++) {
                Node child = nl.item(n);
                String fc;
                if (child.hasChildNodes())
                    fc = child.getFirstChild().getNodeValue().trim();
                else
                    fc = "no value";
                if (child.getNodeType() == Node.ELEMENT_NODE)
                    System.out.println(indent + child.getNodeName() + " = " + fc);
                //System.out.print("Name: " + child.getNodeName() + " Value: " + child.getNodeValue() + "\n");
                printTree(child, indent+"   ");
            }
        }
    }

    // Iterative Breadth-first search
    private static void bSrch(String behavior, NodeList nodes) {
        Queue<Node> q = new LinkedList<>();
        for (int i = 0; i < nodes.getLength(); i++)
            q.add(nodes.item(i));

        while (!q.isEmpty()) {
            Node n = q.remove();
            if (n.hasChildNodes()) {
                NodeList children = n.getChildNodes();
                if (n.getFirstChild().getNodeValue().trim().equalsIgnoreCase(behavior)) {
                    //System.out.println(behavior + " FOUND!");
                    getResponses(n);
                    //System.out.println("Responses size: " + responses.size());
                } else {
                    for (int i = 1; i < children.getLength(); i++)
                        q.add(children.item(i));
                }
            }
        }
    }

    // Recursive Depth-first search
    private static void dSrch(String behavior, Node n) {
        if (n.hasChildNodes()) {
            NodeList children = n.getChildNodes();
            if (n.getFirstChild().getNodeValue().trim().equalsIgnoreCase(behavior)) {
                //System.out.println(behavior + " FOUND!");
                getResponses(n);
                //System.out.println("Responses size: " + responses.size());
            } else {
                for (int i = 1; i < children.getLength(); i++)
                    dSrch(behavior, children.item(i));
            }
        }
    }

    // Recursive method to find all possible responses for the given behavior
    private static String getResponses(Node node) {
        if (node.hasChildNodes()) {
            NodeList nl = node.getChildNodes();
            for (int n = 0; n < nl.getLength(); n++) {
                Node child = nl.item(n);
                if (child.hasChildNodes())
                    if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase("response"))
                        responses.add(child.getFirstChild().getNodeValue().trim());
                String recurse = getResponses(child);
                if (!recurse.isEmpty())
                    responses.add(recurse);
            }
        }
        return "";
    }
}
