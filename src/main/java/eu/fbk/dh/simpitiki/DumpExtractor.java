package eu.fbk.dh.simpitiki;

import eu.fbk.utils.core.CommandLine;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;

/**
 * Created by alessio on 06/07/16.
 */

public class DumpExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumpExtractor.class);
    private static final String DEFAULT_HANDLER = "eu.fbk.dh.simpitiki.handlers.ItalianHandler";

    public static void main(String[] args) {
        try {
            final CommandLine cmd = CommandLine
                    .parser()
                    .withName("command")
                    .withHeader("Description of the command")
                    .withOption("i", "input-path", "Base path of the corpus", "DIR",
                            CommandLine.Type.DIRECTORY_EXISTING, true, false, true)
                    .withOption("o", "output-path", "Output file", "DIR",
                            CommandLine.Type.FILE, true, false, true)
                    .withOption("c", "class", String.format("Handler class (must extend DumpHandler, default %d)", DEFAULT_HANDLER), "CLASSNAME",
                            CommandLine.Type.STRING, true, false, false)
                    .withOption(null, "diff-length", String.format("Diff length (default %d)", DumpHandler.DIFF_DEFAULT), "NUM",
                            CommandLine.Type.INTEGER, true, false, false)
                    .withOption(null, "ignore-diff", String.format("Length for ignoring diff (default %d)", DumpHandler.LEN_IGNORE_DIFF_DEFAULT),
                            "NUM", CommandLine.Type.INTEGER, true, false, false)
                    .withLogger(LoggerFactory.getLogger("eu.fbk")).parse(args);

            final File inputPath = cmd.getOptionValue("i", File.class);
            final File outputPath = cmd.getOptionValue("o", File.class);
//            final String language = cmd.getOptionValue("l", String.class);
            final String className = cmd.getOptionValue("c", String.class, DEFAULT_HANDLER);

            final Integer diffInterval = cmd.getOptionValue("diff-length", Integer.class, DumpHandler.DIFF_DEFAULT);
            final Integer lenToIgnoreDiff = cmd.getOptionValue("ignore-diff", Integer.class, DumpHandler.LEN_IGNORE_DIFF_DEFAULT);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            FileInputStream in = new FileInputStream(inputPath);
            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);

            Class<? extends DumpHandler> cl = (Class<? extends DumpHandler>) Class.forName(className);
            Constructor<?> cons = cl.getConstructor(File.class);
            DumpHandler handler = (DumpHandler) cons.newInstance(outputPath);

            if (diffInterval != null) {
                handler.setDiffInterval(diffInterval);
            }
            if (lenToIgnoreDiff != null) {
                handler.setLenToIgnoreDiff(lenToIgnoreDiff);
            }
            saxParser.parse(bzIn, handler);

            bzIn.close();
            in.close();
        } catch (Exception e) {
            CommandLine.fail(e);
        }

    }
}
