package eu.fbk.dh.simpitiki;

import eu.fbk.utils.core.diff_match_patch;
import eu.fbk.utils.wikipedia.WikipediaText;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

/**
 * Created by alessio on 06/07/16.
 */

abstract public class DumpHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumpHandler.class);
    private BufferedWriter writer;

    private boolean isPage = false;
    private boolean isRevision = false;
    private boolean isContributor = false;
    private boolean isComment = false;
    private boolean isText = false;
    private boolean isSimplifying = false;
    private boolean isGoodPage = false;
    private diff_match_patch diffMatchPatch = new diff_match_patch();

    private String previousText = null;
    private String comment = null;
    private StringBuffer buffer = new StringBuffer();
    private String revisionID = null;
    private String commentHeader = null;
    WikipediaText wikipediaText = new WikipediaText();

    private int pageCount = 0;

    public static int LEN_IGNORE_DIFF_DEFAULT = 200;
    public static int DIFF_DEFAULT = 80;

    private int lenToIgnoreDiff = LEN_IGNORE_DIFF_DEFAULT;
    private int diffInterval = DIFF_DEFAULT;

    public int getLenToIgnoreDiff() {
        return lenToIgnoreDiff;
    }

    public void setLenToIgnoreDiff(int lenToIgnoreDiff) {
        this.lenToIgnoreDiff = lenToIgnoreDiff;
    }

    public int getDiffInterval() {
        return diffInterval;
    }

    public void setDiffInterval(int diffInterval) {
        this.diffInterval = diffInterval;
    }

    public DumpHandler(File outputPath) {
        try {
            writer = new BufferedWriter(new FileWriter(outputPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive notification of the end of the document.
     * <p>
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end
     * of a document (such as finalising a tree or closing an output
     * file).</p>
     *
     * @throws SAXException Any SAX exception, possibly
     *                      wrapping another exception.
     * @see ContentHandler#endDocument
     */
    @Override public void endDocument() throws SAXException {
        super.endDocument();

        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Receive notification of the start of an element.
     * <p>
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the start of
     * each element (such as allocating a new tree node or writing
     * output to a file).</p>
     *
     * @param uri        The Namespace URI, or the empty string if the
     *                   element has no Namespace URI or if Namespace
     *                   processing is not being performed.
     * @param localName  The local name (without prefix), or the
     *                   empty string if Namespace processing is not being
     *                   performed.
     * @param qName      The qualified name (with prefix), or the
     *                   empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *                   there are no attributes, it shall be an empty
     *                   Attributes object.
     * @throws SAXException Any SAX exception, possibly
     *                      wrapping another exception.
     * @see ContentHandler#startElement
     */
    @Override public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        buffer = new StringBuffer();

        if (qName.equals("page")) {
            isPage = true;
            isGoodPage = true;
            if (++pageCount % 100 == 0) {
                LOGGER.info("PAGE COUNT: " + pageCount);
            }
            previousText = null;
        }
        if (qName.equals("revision")) {
            isRevision = true;
            isSimplifying = false;
        }
        if (qName.equals("comment")) {
            isComment = true;
        }
        if (qName.equals("contributor")) {
            isContributor = true;
        }
        if (qName.equals("text")) {
            isText = true;
        }
    }

    abstract public boolean isGoodPage(String title);

    abstract public String commentMeansSimplification(String comment);

    /**
     * Receive notification of the end of an element.
     * <p>
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end of
     * each element (such as finalising a tree node or writing
     * output to a file).</p>
     *
     * @param uri       The Namespace URI, or the empty string if the
     *                  element has no Namespace URI or if Namespace
     *                  processing is not being performed.
     * @param localName The local name (without prefix), or the
     *                  empty string if Namespace processing is not being
     *                  performed.
     * @param qName     The qualified name (with prefix), or the
     *                  empty string if qualified names are not available.
     * @throws SAXException Any SAX exception, possibly
     *                      wrapping another exception.
     * @see ContentHandler#endElement
     */
    @Override public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals("page")) {
            isPage = false;
        }
        if (qName.equals("revision")) {
            isRevision = false;
        }
        if (qName.equals("contributor")) {
            isContributor = false;
        }
        if (qName.equals("title")) {
            isGoodPage = isGoodPage(buffer.toString());

//            if (isGoodPage) {
//                LOGGER.info("TITLE: " + buffer.toString());
//            }
        }
        if (isRevision && !isContributor && qName.equals("id")) {
            revisionID = buffer.toString();
        }
        if (qName.equals("comment")) {
            comment = buffer.toString();

            comment = commentMeansSimplification(comment);

            if (comment != null) {
                StringBuffer header = new StringBuffer();
                header.append("#");
                header.append(revisionID);
                header.append(" COMMENT: ");
                header.append(comment);

                commentHeader = header.toString();
                isSimplifying = isGoodPage;
            }

            isComment = false;
        }
        if (qName.equals("text")) {
            String tmpText = buffer.toString();

            if (isSimplifying && previousText != null) {

                LOGGER.info(commentHeader);

                String text = wikipediaText.parse(tmpText, new Whitelist());
                String shortPrevious = wikipediaText.parse(previousText, new Whitelist());

                try {
                    LinkedList<diff_match_patch.Diff> diffs = diffMatchPatch.diff_main(shortPrevious, text);
                    diffMatchPatch.diff_cleanupSemantic(diffs);

                    if (diffs.size() > 1) {

                        writer.append(commentHeader).append("\n");

                        StringBuffer before = new StringBuffer();
                        StringBuffer after = new StringBuffer();

                        for (int i = 0; i < diffs.size(); i++) {
                            diff_match_patch.Diff diff = diffs.get(i);
                            int len = diff.text.length();

                            switch (diff.operation) {
                            case EQUAL:
                                if (len < lenToIgnoreDiff) {
                                    after.append(diff.text);
                                    before.append(diff.text);
                                } else {
                                    if (i != 0) {
                                        after.append(diff.text.substring(0, diffInterval));
                                        before.append(diff.text.substring(0, diffInterval));
                                    }
                                    after.append(" [...] ");
                                    before.append(" [...] ");
                                    if (i != diffs.size() - 1) {
                                        after.append(diff.text.substring(len - diffInterval, len));
                                        before.append(diff.text.substring(len - diffInterval, len));
                                    }
                                }
                                break;
                            case DELETE:
                                before.append("<del>").append(diff.text).append("</del>");
                                break;
                            case INSERT:
                                after.append("<ins>").append(diff.text).append("</ins>");
                                break;
                            }
                        }

                        writer.append("#BEFORE").append("\n");
                        writer.append(before).append("\n");
                        writer.append("#AFTER").append("\n");
                        writer.append(after).append("\n");

                        writer.append("\n");

                        // Debug: it can be removed
                        writer.flush();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            previousText = tmpText;
            isText = false;
        }
    }

    /**
     * Receive notification of character data inside an element.
     * <p>
     * <p>By default, do nothing.  Application writers may override this
     * method to take specific actions for each chunk of character data
     * (such as adding the data to a node or buffer, or printing it to
     * a file).</p>
     *
     * @param ch     The characters.
     * @param start  The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @throws SAXException Any SAX exception, possibly
     *                      wrapping another exception.
     * @see ContentHandler#characters
     */
    @Override public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        String text = new String(ch, start, length);
        buffer.append(text);
    }
}
