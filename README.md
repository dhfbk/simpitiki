# SIMPITIKI: a Simplification corpus for Italian

SIMPITIKI is a Simplification corpus for Italian and it consists in two sets of simplified pairs: the first one is harvested from the Italian Wikipedia in a semi-automatic way; the second one is manually annotated sentence-by-sentence from documents in the administrative domain.

The first part is the result of a study aimed at assessing the possibility to leverage a simplification corpus from Wikipedia in a semi-automated way, starting from Wikipedia edits. The study is inspired by the work presented in [(Woodsend and Lapata 2011)](http://homepages.inf.ed.ac.uk/kwoodsen/wiki.html), in which a set of parallel sentences was extracted from Simple Wikipedia revision history.
However, the present work is different in that: (i) we use the Italian Wikipedia revision history, demonstrating that the approach can be applied also to languages other than English and on edits of Wikipedia that were not created for educational purposes, and (ii) we manually select the actual simplifications and label them following the annotation scheme already applied to other Italian corpora. This makes possible the comparison with other resources for text simplification, and allows a seamless integration between different corpora. 
Our methodology can be summarised as follows: we first select the edited sentence pairs which were commented as `simplified' in Wikipedia edits, filtering out some specific simplification types (for example, template pages). Then, we manually check the extracted pairs and, in case of simplification, we annotate the types in compliance with the existing annotation scheme for Italian (see below).

The second part is manually created, using the same annotation paradigm, starting from documents in the administrative domain, downloaded from the [Municipality of Trento website](http://www.comune.trento.it/).

## The corpus

The corpus in XML format can be downloaded directly from this GitHub repository: [resource.xml](https://github.com/dhfbk/simpitiki/blob/master/simpitiki.xml).

In order to develop a corpus which is compliant with the annotation scheme already used in previous works on simplification, we followed the simplification types described in [(Brunato et al., 2015)](http://www.cnr.it/istituti/ProdottoDellaRicerca.html?cds=048&id=332693).
The tagset is included in the XML using the `<legenda>` tag, and can be summarized as follows:

| Type | Count (part one) | Count (part two) | Total |
|---|---:|---:|---:|
| Split | 20 | 18 | __38__ |
| Merge | 22 | 0 | __22__ |
| Reordering | 14 | 20 | __34__ |
| Insert - Verb | 11 | 5 | __16__ |
| Insert - Subject | 5 | 1 | __6__ |
| Insert - Other | 58 | 21 | __79__ |
| Delete - Verb | 12 | 1 | __13__ |
| Delete - Subject | 17 | 1 | __18__ |
| Delete - Other | 146 | 31 | __177__ |
| Transformation - Lexical Substitution (word level) | 96 | 253 | __349__ |
| Transformation - Lexical Substitution (phrase level) | 143 | 184 | __327__ |
| Transformation - Anaphoric replacement | 14 | 3 | __17__ |
| Transformation - Noun to Verb | 3 | 32 | __35__ |
| Transformation - Verb to Noun (nominalization) | 2 | 0 | __2__ |
| Transformation - Verbal Voice | 2 | 1 | __3__ |
| Transformation - Verbal Features | 10 | 20 | __30__ |
| __Total__ | __575__ | __591__ | __1166__ |

The `<simplifications>` tag introduces the list of simplifications texts. Each simplification pair uses the `<simplification>` tag: the `type` attribute links the pair to the corresponding simplification type; the `<before>` and `<after>` tags contain the text before and after the simplification, respectively. Inside them, `<ins>` and `<del>` tags are used to highlight the parts where the text has been modified (`<ins>` means 'insert', `<del>` means 'delete').

## Credits

This resource has been developed in the [Digital Humanities Unit](http://dh.fbk.eu/) at [Fondazione Bruno Kessler](http://www.fbk.eu/) by Sara Tonelli, Alessio Palmero Aprosio and Francesca Saltori.

For more information about the corpus, please send an e-mail to [aprosio@fbk.eu](mailto:aprosio@fbk.eu).
