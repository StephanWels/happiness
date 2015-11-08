package ste.wel.happiness;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import java.io.IOException;
import java.nio.file.Path;

public class TempEmbeddedSolrServer extends EmbeddedSolrServer {

    private final Path cleanupPath;

    public TempEmbeddedSolrServer(final CoreContainer container, String coreName, final Path dataDirectory) throws IOException {
        super(container, coreName);
        cleanupPath = dataDirectory;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        try {
            FileUtils.deleteDirectory(cleanupPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
