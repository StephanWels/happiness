package ste.wel.happiness;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HappinessKeywordsLearner {

    private static final Logger LOG = LoggerFactory.getLogger(HappinessKeywordsLearner.class);
    private SolrClient solrClient;
    private Map<String, Long> tagFrequency = new HashMap<>();

    public HappinessKeywordsLearner(final SolrClient solrClient){
        this.solrClient = solrClient;
    }


    public String suggestTag(String comment) {
        try {
            String id = addToIndex(comment);
            LOG.info("id: " + id);
            final List<String> stringTagStream = queryTags(id);
            final String tagSuggestion = stringTagStream.stream()
                    .collect(Collectors.groupingBy(o -> o, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max((o1, o2) -> (int) Math.signum(o1.getValue() - o2.getValue()))
                    .map(Map.Entry::getKey)
                    .orElse("");

            LOG.info("Suggested '" + tagSuggestion + "' for text '" + comment + "'");
            removeFromIndex(id);
            return tagSuggestion;
        } catch (SolrServerException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void removeFromIndex(final String id) throws IOException, SolrServerException {
        solrClient.deleteById(id);
    }

    private List<String> queryTags(final String id) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/mlt");
        query.setQuery("id:" + id);
        query.set(MoreLikeThisParams.SIMILARITY_FIELDS, "text");
        query.set(MoreLikeThisParams.MIN_TERM_FREQ, 1);
        query.set(MoreLikeThisParams.MIN_DOC_FREQ, 2);
        query.set("rows", 9);
        final QueryResponse response = solrClient.query(query);
        return Optional.ofNullable(response.getResults()).orElse(new SolrDocumentList())
                .stream()
                .flatMap(solrDocument -> Optional.ofNullable(solrDocument)
                        .map(document -> document.getFieldValues("tag"))
                        .orElse(Collections.emptyList())
                        .stream())
                .map(tag -> (String) tag)
                .collect(Collectors.toList());
    }

    private String addToIndex(final String comment) throws IOException, SolrServerException {
        String id = UUID.randomUUID().toString();
        SolrInputDocument queryDoc = new SolrInputDocument();

        queryDoc.addField("text", comment);
        queryDoc.addField("id", id);

        solrClient.add(queryDoc);
        solrClient.commit();
        return id;
    }

    public void trainOnData(List<Comment> comments) throws Exception {
        solrClient.deleteByQuery("*");
        int deleteTime = solrClient.commit().getQTime();
        System.out.println("Deleted training data in " + deleteTime + "ms.");
        Collection<SolrInputDocument> docs = new ArrayList<>();
        for (Comment comment : comments) {
            SolrInputDocument doc = toSolrInputDocument(comment);
            docs.add(doc);
        }
        if (docs.isEmpty()) {
            LOG.warn("Nothing to train on.");
            return;
        }
        solrClient.add(docs);
        int qTime = solrClient.commit().getQTime();
        System.out.println("Loaded training data in " + qTime + "ms.");

        calculateTagFrequencies();

    }

    private void calculateTagFrequencies() throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/select");
        query.setQuery("*");
        query.setFacet(true);
        query.addFacetField("tag");
        final QueryResponse response = solrClient.query(query);
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
        return doc;
    }
}
