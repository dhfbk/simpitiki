package eu.fbk.dh.simpitiki;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by alessio on 11/06/15.
 */

public class WikipediaText {

	private WikiModel wikiModel;

	public static String br2nl(String html, boolean nice, @Nullable Whitelist whitelist) {
		if (html == null) {
			return html;
		}

		if (whitelist == null) {
			whitelist = Whitelist.none();
		}
		Document document = Jsoup.parse(html);
		document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
		document.select("br").append("\\n");

		document.select("sup").remove();

		if (nice) {
			document.select("h1").prepend("=*=*=* ");
			document.select("h2").prepend("=*=* ");
			document.select("h3").prepend("=* ");
			document.select("li").prepend("* ");
		}

		document.select("p, h1, h2, h3, h4").prepend("\\n\\n");
		document.select("li").append("\\n");

		document.select("div").remove();
		document.select("table").remove();

		String s = document.html().replaceAll("\\\\n", "\n");
		return Jsoup.clean(s, "", whitelist, new Document.OutputSettings().prettyPrint(false));
	}

	public WikipediaText() {
		wikiModel = new MyWikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");

	}

	public String parse(String wikiText, @Nullable Whitelist whitelist) {
		String rawText = wikiModel.render(wikiText);
		rawText = br2nl(rawText, false, whitelist);
//		rawText = rawText.replaceAll("\\[[0-9]+\\]", "");
		rawText = StringEscapeUtils.unescapeHtml4(rawText);
		rawText = rawText.replaceAll("\\n{2,}", "\n\n");
		return rawText;
	}

	public static void main(String[] args) throws IOException {
		String fileName = "/Users/alessio/Documents/Resources/pantheon/it/example.wiki";
		List<String> lines = Files.readLines(new File(fileName), Charsets.UTF_8);

		StringBuilder wikiText = new StringBuilder();
		for (String line : lines) {
			wikiText.append(line.trim()).append("\n");
		}

		WikipediaText wikipediaText = new WikipediaText();

		Whitelist whitelist = Whitelist.none();
		whitelist.addAttributes("a", "href");

		String rawText = wikipediaText.parse(wikiText.toString(), whitelist);
		System.out.println(rawText);
	}
}
