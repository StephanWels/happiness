package ste.wel.happiness;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class HappinessKeywordsLearner {

    private static final Logger LOG = LoggerFactory.getLogger(HappinessKeywordsLearner.class);

    @Autowired
    private EmbeddedSolrServer solrServer;
    private Map<String, Long> tagFrequency = new HashMap<>();

    public String suggestTag(String comment, final String group) {
        try {
            String id = addToIndex(comment, group);
            final List<String> stringTagStream = queryTags(id);
            final String tagSuggestion = stringTagStream.stream()
                    .collect(Collectors.groupingBy(o -> o, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max((o1, o2) -> (int) Math.signum(o1.getValue() - o2.getValue()))
                    .map(Map.Entry::getKey)
                    .orElse("");

            LOG.info("Suggested '" + tagSuggestion + "' for text '" + comment + "'");
            return tagSuggestion;
        } catch (SolrServerException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<String> queryTags(final String id) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/mlt");
        query.setQuery("id:" + id);
        query.set(MoreLikeThisParams.SIMILARITY_FIELDS, "text");
        query.set(MoreLikeThisParams.MIN_TERM_FREQ, 1);
        query.set(MoreLikeThisParams.MIN_DOC_FREQ, 2);
        query.set("rows", 9);
        final QueryResponse response = solrServer.query(query);
        return Optional.ofNullable(response.getResults()).orElse(new SolrDocumentList())
                .stream()
                .flatMap(solrDocument -> Optional.ofNullable(solrDocument)
                        .map(document -> document.getFieldValues("tag"))
                        .orElse(Collections.emptyList())
                        .stream())
                .map(tag -> (String) tag)
                .collect(Collectors.toList());
    }

    private String addToIndex(final String comment, final String group) throws IOException, SolrServerException {
        String id = UUID.randomUUID().toString();
        SolrInputDocument queryDoc = new SolrInputDocument();

        queryDoc.addField("text", comment);
        queryDoc.addField("group", group);
        queryDoc.addField("id", id);

        solrServer.add(queryDoc);
        solrServer.commit();
        return id;
    }

    public void trainOnData(HappinessIndexInputFile inputCsv) throws Exception {
        solrServer.deleteByQuery("*");
        int deleteTime = solrServer.commit().getQTime();
        System.out.println("Deleted training data in " + deleteTime + "ms.");
        Collection<SolrInputDocument> docs = new ArrayList<>();
        for (Comment comment : inputCsv.getTaggedComments()) {
            SolrInputDocument doc = toSolrInputDocument(comment);
            docs.add(doc);
        }
        if (docs.isEmpty()) {
            LOG.warn("Nothing to train on.");
            return;
        }
        solrServer.add(docs);
        int qTime = solrServer.commit().getQTime();
        System.out.println("Loaded training data in " + qTime + "ms.");

        calculateTagFrequencies();

    }

    private void calculateTagFrequencies() throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/select");
        query.setQuery("*");
        query.setFacet(true);
        query.addFacetField("tag");
        final QueryResponse response = solrServer.query(query);
        Optional.ofNullable(response.getFacetField("tag"))
                .ifPresent(facetField -> facetField.getValues()
                        .forEach(count -> tagFrequency.put(count.getName(), count.getCount())));
    }

    private SolrInputDocument toSolrInputDocument(Comment comment) {
        String id = UUID.randomUUID().toString();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", id);
        doc.addField("text", comment.getComment());
        doc.addField("tag", comment.getTags());
        doc.addField("group", comment.getGroup());
        return doc;
    }
}
