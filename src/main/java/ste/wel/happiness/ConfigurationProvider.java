package ste.wel.happiness;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ConfigurationProvider {

    private int minTermFrequency = 1;
    private int minDocumentFrequency = 2;
    private int k = 9;

    public int getMinTermFrequency() {
        return minTermFrequency;
    }

    public int getMinDocumentFrequency() {
        return minDocumentFrequency;
    }

    public int getK() {
        return k;
    }

    public void setMinTermFrequency(final int minTermFrequency) {
        this.minTermFrequency = minTermFrequency;
    }

    public void setMinDocumentFrequency(final int minDocumentFrequency) {
        this.minDocumentFrequency = minDocumentFrequency;
    }

    public void setK(final int k) {
        this.k = k;
    }
}
