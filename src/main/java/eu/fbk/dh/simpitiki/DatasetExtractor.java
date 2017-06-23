package eu.fbk.dh.simpitiki;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import eu.fbk.dh.tint.runner.TintPipeline;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alessio on 20/06/17.
 */

public class DatasetExtractor {

    public static final Pattern beforePattern = Pattern.compile("</?del>");
    public static final Pattern afterPattern = Pattern.compile("</?ins>");

    public static void main(String[] args) {
        String connectionString = "jdbc:mysql://localhost:12345/simpatico2?user=root&password=iX7E9QeniXzgUs&autoreconnect=true";
        String outFile = "/Users/alessio/Documents/scripts/simpitiki/simpitiki-new.xml";

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("resource");
            doc.appendChild(rootElement);

            Element simplificationsElement = doc.createElement("simplifications");
            rootElement.appendChild(simplificationsElement);

            TintPipeline pipeline = new TintPipeline();
            pipeline.loadDefaultProperties();
            pipeline.setProperty("annotators", "ita_toksent");

            Connection dbconn = DriverManager.getConnection(connectionString);

            Statement stm = dbconn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT d.*, a.before, a.after\n"
                    + "\tFROM done d\n"
                    + "\tLEFT JOIN annotations a ON a.id = d.annotation\n"
                    + "\tWHERE d.result != '0'");
            while (rs.next()) {
                String before = new String(rs.getString("before").getBytes("ISO-8859-1"));
                String after = new String(rs.getString("after").getBytes("ISO-8859-1"));

                Integer result = rs.getInt("result");
                Element simplificationElement = doc.createElement("simplification");
                simplificationElement.setAttribute("origin", "itwiki");
                simplificationElement.setAttribute("type", Integer.toString(result));

                Matcher beforeMatcher = beforePattern.matcher(before);
                Matcher afterMatcher = afterPattern.matcher(after);

                Set<Integer> del = new HashSet<>();
                Set<Integer> ins = new HashSet<>();

                PreparedStatement preparedStatement = dbconn.prepareStatement("SELECT * FROM parts WHERE done = ?");
                preparedStatement.setInt(1, rs.getInt("id"));
                ResultSet myRs = preparedStatement.executeQuery();
                while (myRs.next()) {
                    String box = myRs.getString("box");
                    Integer num = Integer.parseInt(box.substring(3));
                    if (box.startsWith("del")) {
                        del.add((num - 1) * 2);
                    } else if (box.startsWith("ins")) {
                        ins.add((num - 1) * 2);
                    }
                }

                Set<Integer> delToKeep = new HashSet<>();
                Set<Integer> insToKeep = new HashSet<>();

                TreeMap<Integer, String> delSpans = new TreeMap<>();
                TreeMap<Integer, String> insSpans = new TreeMap<>();
                StringBuffer buffer1 = new StringBuffer();
                StringBuffer buffer2 = new StringBuffer();

                setSpans(beforeMatcher, buffer1, delSpans, del, delToKeep);
                setSpans(afterMatcher, buffer2, insSpans, ins, insToKeep);

                StringBuilder out1 = new StringBuilder();
                StringBuilder out2 = new StringBuilder();

                int minIndex = -1;
                int totIndex = 0;

                for (int i = 0; i < buffer1.toString().length(); i++) {
                    char c = buffer1.toString().charAt(i);

                    if (delToKeep.contains(i)) {
                        if (minIndex == -1 || minIndex > i) {
                            minIndex = i;
                        }
                        out1.append("<del>");
                        out1.append(delSpans.get(i));
                        out1.append("</del>");
                        totIndex = 0;
                    } else if (delSpans.containsKey(i)) {
                        out1.append(delSpans.get(i));
                        out2.append(delSpans.get(i));
                        totIndex += delSpans.get(i).length();
                    }
                    if (insToKeep.contains(i)) {
                        if (minIndex == -1 || minIndex > i) {
                            minIndex = i;
                        }
                        out2.append("<ins>");
                        out2.append(insSpans.get(i));
                        out2.append("</ins>");
                        totIndex = 0;
                    }

                    out1.append(c);
                    out2.append(c);
                    totIndex++;
                }

                String b = out1.toString();
                String a = out2.toString();

                Annotation annotationBefore = pipeline.runRaw(b.substring(0, minIndex));
                for (CoreMap sentence : annotationBefore.get(CoreAnnotations.SentencesAnnotation.class)) {
                    minIndex = sentence.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
                }
                Annotation annotationAfter = pipeline.runRaw(a.substring(a.length() - totIndex));
                for (CoreMap sentence : annotationAfter.get(CoreAnnotations.SentencesAnnotation.class)) {
                    totIndex -= sentence.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
                    break;
                }

                String final1 = b.substring(minIndex, b.length() - totIndex);
                Element beforeElement = doc.createElement("before");
                beforeElement.setTextContent(final1);

                String final2 = a.substring(minIndex, a.length() - totIndex);
                Element afterElement = doc.createElement("after");
                afterElement.setTextContent(final2);

                simplificationElement.appendChild(beforeElement);
                simplificationElement.appendChild(afterElement);
                simplificationsElement.appendChild(simplificationElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outFile));
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(source, result);

            dbconn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void setSpans(Matcher matcher, StringBuffer buffer, TreeMap<Integer, String> spans, Set<Integer> inputSet,
            Set<Integer> outputSet) {
        int index = 0;
        while (matcher.find()) {
            StringBuffer tmpBuffer = new StringBuffer();
            matcher.appendReplacement(tmpBuffer, "");
            if (index % 2 == 0) { // open tag
                buffer.append(tmpBuffer);
                if (inputSet.contains(index)) {
                    outputSet.add(buffer.length());
                }
            } else { // close tag
                spans.put(buffer.length(), tmpBuffer.toString());
            }
            index++;
        }
        matcher.appendTail(buffer);
    }
}
