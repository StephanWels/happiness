package ste.wel.happiness;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModelEvaluator {

    public double calcuateAccuracy(final HappinessKeywordsLearner learner) {
        final List<String> ids = learner.getIds();
        double nonEmptyPredictions = 0;
        double correctPredictions = 0;
        for (String id : ids) {
            final List<String> actualTags = learner.getTagsForId(id);
            String tagSuggestion = learner.getTagSuggestionForId(id);
            if (StringUtils.isNotEmpty(tagSuggestion)) {
                nonEmptyPredictions++;
                if (actualTags.contains(tagSuggestion)) {
                    correctPredictions++;
                }
            }
        }
        return correctPredictions / nonEmptyPredictions;
    }
}
