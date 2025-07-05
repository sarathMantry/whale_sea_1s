package org.example;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Main {

    private static final Set<Character> wordSeparators = new HashSet<>(Arrays.asList(' ', ',', '.', '\n', '\r', '\t', '!', '?', '-', ';', ':'));
    private static final List<String> pronouns = Arrays.asList("he","she","him","her","they","them","it","his","i","me","you","their","my");
    private static final List<String> conjunctions = Arrays.asList("or","and","but","not","all","by","from","so","then","when","some","one","no","if","more");
    private static final List<String> prepositions = Arrays.asList("in","or","at","of","to","that","as","with","for","this","on","there","now","like","which","upon","out","into","up","what");
    private static final List<String> articles = Arrays.asList("the","a","an","there");
    private static final List<String> verbs = Arrays.asList("can","can't","could","couldn't","may","mayn't","might","mightn't","shall","shan't","should","shouldn't","will","won't","would","wouldn't","must","mustn't","ought to","oughtn't to","need","needn't","dare","daren't","used to","didn't use to","is","isn't","are","aren't","was","wasn't","were","weren't","be","being","been","am","ain't","do","don't","does","doesn't","did","didn't","have","haven't","has","hasn't","had","hadn't");
    private static final Pattern alphabeticPattern = Pattern.compile("^[A-Za-z]+$");
    private static long wordCount =0;







    public static void main(String[] args)  {
        // to see how IntelliJ IDEA suggests fixing it.
      //  System.out.printf("Hello and welcome!");
        try {
            var startTime = new Date();
            System.out.println("text  analyzer  program started: " + startTime);
//            InputStream inputStream = getFile("src/main/resources/moby.txt");
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("moby.txt");
            if(inputStream == null){
                System.out.println("File not found");
                return;
            }
            Map<String,Integer> words = getWords(inputStream);
            closeStream(inputStream);

            OutputStream outputStream = getOutputFileStream("src/main/resources/","output.txt");
            printTotalWordCount(outputStream);
            printHighFrequencyWords(words,5,outputStream);
            printUniqueWords(words,outputStream);
            outputStream.close();
            var endTime = new Date();
            System.out.println("text  analyzer  program ended : " + endTime );
            long timeTakenMillis = endTime.getTime() - startTime.getTime();
            System.out.println("Time taken: " + (timeTakenMillis / 1000.0) + " seconds");

//        for (Map.Entry<String,Integer> e : words.entrySet()) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("Word = " + e.getKey() + " count : "+ e.getValue());
//        }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("text analyzer program ended with error" + e.getMessage());
        }


    }

    private static FileOutputStream getOutputFileStream(String path,String fileName) throws FileNotFoundException {
        return new FileOutputStream(path  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_HH_mm_ss")) +"_"+ fileName);
    }

    private static void printTotalWordCount(OutputStream outputStream) throws IOException {
        String output = "Word Count: " + wordCount + "\n";
        System.out.print(output);
        outputStream.write(output.getBytes());
    }

    private static void printUniqueWords(Map<String, Integer> words, OutputStream outputStream) throws IOException {
        String uniqueWords =words.entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(","));
        String output = "Unique Words are: " + uniqueWords + "\n";
        System.out.print(output);
        outputStream.write(output.getBytes());
    }

    private static void printHighFrequencyWords(Map<String, Integer> words, int limit, OutputStream outputStream) throws IOException {
        String header = "Most Frequent Words:\n";
        System.out.print(header);
        outputStream.write(header.getBytes());
        List<String> freqWords = new ArrayList<>();
        for(int i = 0;i < limit;i++){
            freqWords.add(printMostFreqWord(words,freqWords,outputStream));
        }
    }

    private static String printMostFreqWord(Map<String, Integer> words, List<String> freqWords, OutputStream outputStream) throws IOException {
        int maxCount = 0;
        String word = null;
        for(Map.Entry<String,Integer> e : words.entrySet()){
            if(e.getValue() > maxCount && !freqWords.contains(e.getKey())){
                maxCount = e.getValue();
                word = e.getKey();
            }
        }
        String line = "Word: " + word+ ", Count: " + maxCount + "\n";
        System.out.print(line);
        outputStream.write(line.getBytes());
        return word;
    }

    private static Map<String, Integer> getWords(InputStream inputStream) throws IOException {
        int b;
        Map<String,Integer> words = new TreeMap<>();
        StringBuilder word = new StringBuilder();
        while((b = inputStream.read()) != -1){
            char c= (char) b;
            if(wordSeparators.contains(c)){
                processWord(word, words);
                word.setLength(0);
            }else {
                word.append(c);
            }
        }
        processWord(word, words);
        return words;
    }

    private static void processWord(StringBuilder word, Map<String, Integer> words) {
        String w = word.toString().toLowerCase()
                .replace("\"","");
//                if(w.endsWith("'s")){
//                    w = w.substring(0, w.lastIndexOf("'s"));
//                }
        if(!w.isEmpty()
                && alphabeticPattern.matcher(w).matches()
                && !pronouns.contains(w)
                && !conjunctions.contains(w)
                && !prepositions.contains(w)
                && !articles.contains(w)  &&  !verbs.contains(w) ){
            wordCount++;
            words.merge(w, 1, Integer::sum);
        }
    }

    private static void closeStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getFile(String path)  {
        FileInputStream inputStream = null;
        try  {
            inputStream = new FileInputStream(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}