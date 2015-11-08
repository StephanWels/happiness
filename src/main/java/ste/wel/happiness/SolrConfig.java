package ste.wel.happiness;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

@Configuration
public class SolrConfig {

    private static final String CORE_NAME = "test_core";
    private static final String DATA_DIR_PROPERTY = "solr.data.dir";
    private static final String HOME = "solr-config/solr";

    @Bean(destroyMethod = "shutdown", name = "goodSolr")
    public EmbeddedSolrServer embeddedSolrGood() throws IOException {
        return createSolrServer();
    }

    @Bean(destroyMethod = "shutdown", name = "badSolr")
    public EmbeddedSolrServer embeddedSolrBad() throws IOException {
        return createSolrServer();
    }

    private EmbeddedSolrServer createSolrServer() throws IOException {
        Path dataDirectory = Files.createTempDirectory(Paths.get("./"), "data", new FileAttribute[0]);
        System.setProperty(DATA_DIR_PROPERTY, dataDirectory.toAbsolutePath().toString());
        CoreContainer container = new CoreContainer(HOME);
        container.load();
        final CoreDescriptor coreDescriptor = new CoreDescriptor(container, CORE_NAME, ".");
        container.create(coreDescriptor);
        return new TempEmbeddedSolrServer(container, CORE_NAME, dataDirectory);
    }

    //    @Bean
    public SolrClient localSolr(){
        return  new HttpSolrClient("http://localhost:8983/solr/test_core");
    }
}
