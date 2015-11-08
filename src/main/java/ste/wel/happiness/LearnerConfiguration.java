package ste.wel.happiness;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LearnerConfiguration {

    @Bean(name = "goodLearner")
    @Scope("singleton")
    public HappinessKeywordsLearner goodLearner(@Qualifier("goodSolr") SolrClient solrClient, ConfigurationProvider config){
        return new HappinessKeywordsLearner(solrClient, config);
    }

    @Bean(name = "badLearner")
    @Scope("singleton")
    public HappinessKeywordsLearner badLearner(@Qualifier("badSolr") SolrClient solrClient, ConfigurationProvider config){
        return new HappinessKeywordsLearner(solrClient, config);
    }

}
