package eu.fbk.dh.simpitiki.handlers;

import eu.fbk.dh.simpitiki.DumpHandler;

import java.io.File;

/**
 * Created by alessio on 23/09/16.
 */

public class ItalianHandler extends DumpHandler {

    public ItalianHandler(File outputPath) {
        super(outputPath);
    }


    @Override public boolean isGoodPage(String title) {
        if (title.startsWith("Categoria:")) {
            return false;
        }
        if (title.startsWith("Discussioni utente:")) {
            return false;
        }
        if (title.startsWith("Utente:")) {
            return false;
        }
        if (title.startsWith("Wikipedia:")) {
            return false;
        }
        if (title.startsWith("File:")) {
            return false;
        }
        if (title.startsWith("Portale:")) {
            return false;
        }
        if (title.startsWith("Aiuto:")) {
            return false;
        }
        if (title.startsWith("Template:")) {
            return false;
        }
        if (title.startsWith("Discussione:")) {
            return false;
        }
        if (title.startsWith("Discussioni MediaWiki:")) {
            return false;
        }
        return true;
    }

    @Override public String commentMeansSimplification(String comment) {

        if (!comment.toLowerCase().contains("semplif")) {
            return null;
        }

        comment = comment.replaceAll("\\s+", " ").trim();

        if (comment.startsWith("Ha protetto")) {
            return null;
        }
        if (comment.startsWith("Nuova pagina")) {
            return null;
        }
        if (comment.contains("procedura semplificata")) {
            return null;
        }
        if (comment.contains("[[WP:RB|Annullata]]")) {
            return null;
        }
        if (!comment.replaceAll("/\\*.*\\*./", "").contains("semplif")) {
            return null;
        }
        if (comment.contains("template")) {
            return null;
        }

        return comment;
    }
}
