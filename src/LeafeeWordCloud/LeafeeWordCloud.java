package LeafeeWordCloud;

// todo beautify the code
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Word {
    private String str;
    private int count;

    public Word(Word w) {
        this(w.str, w.count);
    }
    public Word(String str) {
        this(str, 1);
    }
    public Word(String str, int count) {
        this.str = new String(str);
        this.count = count;
    }
    public boolean equals(Word w) {
        return this.str.equals(w.str);
    }
    public boolean equals(String str) {
        return this.str.equals(str);
    }
    public boolean isBigger(Word w) {return this.count > w.count; }
    public String toString() { return this.str; }
    public void count() {
        ++this.count;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) { this.count = count; }
    public String getString() {
        return this.str;
    }
}

class ChainList {
    private class Node {
        public Word item;
        public Node next;
        public Node(Word item, Node next) {
            this.item = new Word(item);
            this.next = next;
        }
    }
    private Node head;
    
    public ChainList() {
        this.head = null;
    }
    public void insertBack(Word item) {
        if (head == null) {
            head = new Node(item, null);
        }
        else {
            Node temp = head;
            while (temp.next != null)
                temp = temp.next;
            temp.next = new Node(item, null);
        }
    }
    public Word find(String item) {
        Node temp = head;
        while (temp != null && !temp.item.equals(item))
            temp = temp.next;
        return temp.item;
    }
    public boolean remove(String str) {
        // if empty chainList;
        if (head == null)
            return false;

        // if the first node;
        if (head.item.equals(str)) {
            head = head.next;
            return true;
        }
        else {
            // find the Node which before the destination
            // and delete by using Node.next
            // if it is the last node, it will be also judged by using temp.next.item
            Node temp = head;
            while (temp.next != null && !temp.next.item.equals(str))
                temp = temp.next;
            if (temp.next == null)
                return false;
            else {
                // Leafee: always want to delete something
                temp.next = temp.next.next;
                return true;
            }
        }
    }
    public void foreach(MyFunction<Word> f) {
        for (Node temp = head; temp != null; temp = temp.next)
            f.fun(temp.item);
    }
    public List<Word> sortToList() {
        Word w = head.item;
        ArrayList<Word> arrlist = new ArrayList<Word>();
        // the outside iterate only to iterate ChainList.size() times, but it has no this method
        for (Node i = head; i != null; i = i.next) {
            w = i.item;
            for (Node j = head; j != null; j = j.next) {
                if (j.item.isBigger(w))
                    w = j.item;
            }
            arrlist.add(w);
            w.setCount(-1 * w.getCount());
        }
        // Leafee: the java, add to List is also quote. change one, the other also change; -_-||
        // restore the count from the negative
        for (Word wi : arrlist)
            wi.setCount(-1 * wi.getCount());
        return arrlist;
    }
}

interface MyFunction<T> {
    T fun(T t);
}

class MyOutput implements MyFunction<Word> {
    public Word fun(Word w) {
        System.out.println(w + "(" + String.valueOf(w.getCount()) + ")");
        return w;
    }
}

public class LeafeeWordCloud extends JPanel {

    static private List<Word> sortedWords;
    static private String fontName = "YaHei Consolas hybird";
    static private int fontStyle = Font.PLAIN;

    // also means how many words will be print, size will decrease, one zero, drawing will terminate
    static private int fontSize = 40;

    @Override
    public void paint(Graphics g) {
        // todo Paint & not to cover another word
        Random rand = new Random();
        for (Word w : sortedWords) {
            if (fontSize <= 5)
                break;
            Font font = new Font(fontName, fontStyle, fontSize--);
            g.setFont(font);
            System.out.println(w.getString() + String.valueOf(w.getCount()));
            g.drawString(w.getString(), rand.nextInt(800), rand.nextInt(600));
        }
    }

    public static void main(String[] args) {
        ChainList wordList = new ChainList();

        Scanner input = new Scanner(System.in);
        File file = null;
        FileReader filereader = null;
        BufferedReader bufferedreader = null;
        String[] content;

        // open the file. The BufferedReader is used to read file by line
        try {
            // read the file path, in case that the program running in a different computer
            System.out.print("Please input the file path: ");
            file = new File(input.next().trim());
            while (!file.exists()) {
                System.out.print("File not exists, please input an existed file: ");
                file = new File(input.next().trim());
            }
            filereader = new FileReader(file);
            bufferedreader = new BufferedReader(filereader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read file and add the words into wordList
        try {
            while (bufferedreader.ready()) {
                content = bufferedreader.readLine().split(" ");
                for (String oneWord : content) {
                    try {
                        wordList.find(oneWord).count();
                    } catch (NullPointerException e) {
                        wordList.insertBack(new Word(oneWord));
                    }
                }
            }
            bufferedreader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // remove the stop words
        String stopWord = null;
        String removeMessage = null;
        System.out.println("Input by hand or read from a file(a to input by hand, b to read file, c to skip\n)"
                + "(a / b / c):");
        switch (input.next()) {
            case "a":
            case "A":
                System.out.println("Please input the stop words you want to remove from the wordCloud,\n" +
                        "input three equal sign(===) to terminate input:");
                for (stopWord = input.next().trim(); !stopWord.equals("===");) {
                    removeMessage = wordList.remove(stopWord) ? "Succeed!" : "This word is not contained by wordList.";
                    System.out.println(removeMessage);
                    stopWord = input.next().trim();
                }
                break;

            case "B":
            case "b":
                try {
                    System.out.print("Please input the file path: ");
                    file = new File(input.next());
                    while (!file.exists()) {
                        System.out.print("File not exists, please input an existed file: ");
                        file = new File(input.next().trim());
                    }

                    filereader = new FileReader(file);
                    bufferedreader = new BufferedReader(filereader);
                    while (bufferedreader.ready())
                        for (String x : bufferedreader.readLine().split(" "))
                            wordList.remove(x);
                    System.out.println("Done!");
                } catch (Exception io) {
                    System.out.println("An Exception was throwed.");
                    System.out.println("May be file not exist, or reading error.");
                    io.printStackTrace();
                }
                break;
            case "C":
            case "c":
                break;
        }

        sortedWords = wordList.sortToList();

        // // debug output the temporary result
        // wordList.foreach(new MyOutput());


        try {
            JFrame jf = new JFrame();
            jf.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            jf.setContentPane(new LeafeeWordCloud());
            jf.setSize(900, 600);
            jf.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            bufferedreader.close();
            filereader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}