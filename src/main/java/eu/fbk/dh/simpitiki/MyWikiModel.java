package eu.fbk.dh.simpitiki;

import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.namespaces.INamespace;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by alessio on 11/06/15.
 */

public class MyWikiModel extends WikiModel {

	/**
	 * @param imageBaseURL a url string which must contains a &quot;${image}&quot; variable
	 *                     which will be replaced by the image name, to create links to
	 *                     images.
	 * @param linkBaseURL  a url string which must contains a &quot;${title}&quot; variable
	 *                     which will be replaced by the topic title, to create links to
	 */
	public MyWikiModel(String imageBaseURL, String linkBaseURL) {
		super(imageBaseURL, linkBaseURL);
	}

	public MyWikiModel(Configuration configuration, String imageBaseURL, String linkBaseURL) {
		super(configuration, imageBaseURL, linkBaseURL);
	}

	public MyWikiModel(Configuration configuration, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(configuration, locale, imageBaseURL, linkBaseURL);
	}

	public MyWikiModel(Configuration configuration, ResourceBundle resourceBundle,
			INamespace namespace, String imageBaseURL, String linkBaseURL) {
		super(configuration, resourceBundle, namespace, imageBaseURL, linkBaseURL);
	}

//	public MyWikiModel(Configuration configuration, Locale locale, ResourceBundle resourceBundle, INamespace namespace, String imageBaseURL, String linkBaseURL) {
//		super(configuration, locale, resourceBundle, namespace, imageBaseURL, linkBaseURL);
//	}

	/**
	 * Substitute the template name by the template content and parameters and
	 * append the new content to the writer.
	 *
	 * @param templateName the name of the template
	 * @param parameterMap the templates parameter <code>java.util.SortedMap</code>
	 * @param writer       the buffer to append the substituted template content
	 * @throws IOException
	 */
	@Override
	public void substituteTemplateCall(String templateName, Map<String, String> parameterMap, Appendable writer) throws IOException {
		return;
//		super.substituteTemplateCall(templateName, parameterMap, writer);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param rawWikiText
	 */
	@Override
	public String render(String rawWikiText) {
		return super.render(rawWikiText);
	}
}
