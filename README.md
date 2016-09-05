# SIMPITIKI corpus

SIMPITIKI is a Simplification corpus for Italian extracted from Wikipedia.

It is the result of a study aimed at assessing the possibility to leverage a simplification corpus from Wikipedia in a semi-automated way, starting from Wikipedia edits. The study is inspired by the work presented in [(Woodsend and Lapata 2011)](http://homepages.inf.ed.ac.uk/kwoodsen/wiki.html), in which a set of parallel sentences was extracted from Simple Wikipedia revision history.

However, the present work is different in that: (i) we use the Italian Wikipedia revision history, demonstrating that the approach can be applied also to languages other than English and on edits of Wikipedia that were not created for educational purposes, and (ii) we manually select the actual simplifications and label them following the annotation scheme already applied to other Italian corpora. This makes possible the comparison with other resources for text simplification, and allows a seamless integration between different corpora. 

Our methodology can be summarised as follows: we first select the edited sentence pairs which were commented as `simplified' in Wikipedia edits, filtering out some specific simplification types (for example, template pages). Then, we manually check the extracted pairs and, in case of simplification, we annotate the types in compliance with the existing annotation scheme for Italian (see below).

## The corpus

The corpus in XML format can be downloaded directly from this GitHub repository: [resource.xml](https://github.com/dhfbk/simpitiki/blob/master/simpitiki.xml).

In order to develop a corpus which is compliant with the annotation scheme already used in previous works on simplification, we followed the simplification types described in [(Brunato et al., 2015)](http://www.cnr.it/istituti/ProdottoDellaRicerca.html?cds=048&id=332693).
The tagset is included in the XML using the `<legenda>` tag, and can be summarized as follows:

* Split
* Merge
* Reordering
* Insert - Verb
* Insert - Subject
* Insert - Other
* Delete - Verb
* Delete - Subject
* Delete - Other
* Transformation - Lexical Substitution (word level)
* Transformation - Lexical Substitution (phrase level)
* Transformation - Anaphoric replacement
* Transformation - Noun to Verb
* Transformation - Verb to Noun (nominalization)
* Transformation - Verbal Voice
* Transformation - Verbal Features

The `<simplifications>` tag introduces the list of simplifications texts. Each simplification pair uses the `<simplification>` tag: the `type` attribute links the pair to the corresponding simplification type; the `<before>` and `<after>` tags contain the text before and after the simplification, respectively. Inside them, `<ins>` and `<del>` tags are used to highlight the parts where the text has been modified (`<ins>` means 'insert', `<del>` means 'delete').
