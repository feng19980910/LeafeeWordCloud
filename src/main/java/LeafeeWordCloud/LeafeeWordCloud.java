package LeafeeWordCloud;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.FontWeight;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;

public class LeafeeWordCloud{
    static ChainList wordList = new ChainList();
    static Scanner input = new Scanner(System.in);


    // --in-file --stop-words-file -stop-words-handle
    // --word-amount --in-picture
    // --out-picture --picture-width --picture-height
    // --font-name
    public static void main(String[] args) {
        String inFile = null, stopWordsFile = null;
        String inPicture = null, outPicture = "output.png";
        String fontName = "微软雅黑", charset = "utf-8";
        int wordAmount = 600, pictureWidth = 600, pictureHeight = 600;
        boolean stopWordsHandle = false, help = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("--in-file"))
                inFile = args[++i];
            else if (args[i].equals("--stop-words-file"))
                stopWordsFile = args[++i];
            else if (args[i].equals("--stop-words-handle"))
                stopWordsHandle = true;
            else if (args[i].equals("--word-amount"))
                wordAmount = Integer.parseInt(args[++i]);
            else if (args[i].equals("--in-picture"))
                inPicture = args[++i];
            else if (args[i].equals("--out-picture"))
                outPicture = args[++i];
            else if (args[i].equals("--picture-width"))
                pictureWidth = Integer.parseInt(args[++i]);
            else if (args[i].equals("--picture-height"))
                pictureHeight = Integer.parseInt(args[++i]);
            else if (args[i].equals("--font-name"))
                fontName = args[++i];
            else if (args[i].equals("--charset"))
                charset = args[++i];
            else if (args[i].equals("--help"))
                help = true;
            else {
                System.out.println("unrecognized option:" + args[i]);
                System.exit(-3);
            }
        }
        if (help == true)
            printHelp();
        if (inFile == null){
            System.out.println("in-file is required");
            System.exit(-1);
        }

        Word temp = null;
        // read the content file
        for (String s : readFile(openFile(inFile, charset))) {
            if ((temp = wordList.find(s)) != null)
                temp.count();
            else
                wordList.insertBack(new Word(s));
        }


        // remove the tab stop, or it will throw "width could less equal than 0"
        // because the tab will not be displayed on an picture;
        wordList.remove("\t");

        if (stopWordsHandle) {
            // remove the stop words
            String stopWord = null;
            String removeMessage = null;
            // input the stop words by hand
            System.out.println("Please input the stop words you want to remove from the wordCloud,\n" +
                    "input three equal sign(===) to terminate input:");
            for (stopWord = input.next().trim(); !stopWord.equals("==="); ) {
                removeMessage = wordList.remove(stopWord) ? "Succeed!" : "This word is not contained by wordList.";
                System.out.println(removeMessage);
                stopWord = input.next().trim();
            }
        }


        if (stopWordsFile != null)
            for (String si : readFile(openFile(stopWordsFile, charset)))
                wordList.remove(si);


        // translate from Word which I built, to WordFrequency the kumo made
        List<Word> sortedWords = wordList.toList();
        List<WordFrequency> wfList = new ArrayList<WordFrequency>();
        for (Word x : sortedWords)
            wfList.add(new WordFrequency(x.toString(), x.getCount()));

        // create FrequencyAnalyzer, which can load from file, List, webPage and etc;
        // It also can filter the wordAmount most frequent words, the shortest word must longer than 2
        // Leafee: and Chinese Tokenizer, which waste me lot of time to solve the dependency
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(wordAmount);
        frequencyAnalyzer.setMinWordLength(3);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
        List<WordFrequency> wordFrequencies = null;

        // read WordFrequencies form the list<WordFrequencies> I created
        try {
             wordFrequencies = frequencyAnalyzer.loadWordFrequencies(wfList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // draw picture with wordFrequencies and output to a file;
        final Dimension dimension = new Dimension(pictureWidth, pictureHeight);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        try {
            if (inPicture == null)
                wordCloud.setBackground(new CircleBackground((pictureHeight < pictureWidth ? pictureHeight : pictureWidth) / 2));
            else
                wordCloud.setBackground(new PixelBoundryBackground(new File(inPicture)));
            wordCloud.setPadding(2);
            wordCloud.setColorPalette(new ColorPalette(new Color(0xD5CFFA), new Color(0xBBB1FA),
                    new Color(0x9A8CF5), new Color(0x806EF5)));
            wordCloud.setFontScalar(new SqrtFontScalar(12, 45));
            wordCloud.setKumoFont(new KumoFont(fontName, FontWeight.BOLD));
            // wordCloud.build(wfList); // if I use my List<WordFrequencies>, it will create 4500+ words, without filter
            wordCloud.build(wordFrequencies);
            wordCloud.writeToFile(outPicture);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-2);
        }
    }

    private static BufferedReader openFile(String path, String charset) {
        File file = null;
        BufferedReader bufferedreader = null;
        // open the file. The BufferedReader is used to read file by line
        try {
            // read the file path, in case that the program running in a different computer
            file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputstreamreader = new InputStreamReader(fileInputStream, charset);
            bufferedreader = new BufferedReader(inputstreamreader);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return bufferedreader;
    }

    private static List<String> readFile(BufferedReader bufferedreader) {
        // read file and add the words into wordList
        List<String> list = new ArrayList<String>();
        try {
            while (bufferedreader.ready()) {
                String[] temp = bufferedreader.readLine().split(" ");
                list.addAll(Arrays.asList(temp));
            }
            bufferedreader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return list;
    }

    private static void printHelp() {
        System.out.println("\t--in-file pathOfContentFileName      * read the content file from this path");
        System.out.println("\t--stop-words-file pathOfStopWordFile read the stop words from there");
        System.out.println("\t--stop-words-handle                  if you want press stop words yourself, add this parameter without value");
        System.out.println("\t--word-amount number                 the amount of words will be displayed on picture");
        System.out.println("\t--in-picture path                    read the background picture");
        System.out.println("\t--out-picture path                   output the picture to path");
        System.out.println("\t--picture-width number               width of output picture");
        System.out.println("\t--picture-height number              height of output picture");
        System.out.println("\t--font-name fontName                 the font you want on picture");
        System.out.println("\t--charset charsetName                change the charset used to read file");
        System.out.println("\t--help                               print this menu\n");
        System.out.println("  the argument with * is required");
        System.out.println("  if there are spaces in some arguments, use quote around them");
        System.out.println("  example: java -jar JAR --in-file inputFile.txt");
        System.out.println("           java -jar JAR --in-file inputFile.txt --stop-words-file StopWords.txt --out-picture result.png");
        System.out.println("           java -jar JAR --in-file inputFile.txt --stop-words-file StopWords.txt --in-picture in.png --picture-width 640 --picture-height 640 --out-picture out.png --font-name \"YaHei Consolas Hybrid\"\n");
        System.out.println("  default values :");
        System.out.println("      stop-words-file     = null");
        System.out.println("      word-amount         = 600");
        System.out.println("      in-picture          = null");
        System.out.println("      out-picture         = out.png");
        System.out.println("      picture-width       = 600");
        System.out.println("      picture-height      = 600");
        System.out.println("      font-name           = 微软雅黑");
        System.out.println("      charset             = utf-8");
        System.exit(0);
    }
}