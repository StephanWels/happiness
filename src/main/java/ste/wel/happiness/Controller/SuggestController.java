package ste.wel.happiness.Controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ste.wel.happiness.CommonModelAndViewEnhancer;
import ste.wel.happiness.HappinessKeywordsLearner;
import ste.wel.happiness.InputFileProvider;

import java.io.IOException;

@Controller
public class SuggestController {

    private static final Logger LOG = LoggerFactory.getLogger(SuggestController.class);
    private static final String PATH = "main";

    @Autowired
    @Qualifier("goodLearner")
    HappinessKeywordsLearner keywordsLearner;

    @Autowired
    InputFileProvider inputFileProvider;

    @Autowired
    CommonModelAndViewEnhancer commonModelAndViewEnhancer;

    @RequestMapping("/suggestTag")
    public ModelAndView suggestTag(@RequestParam String comment) throws IOException, SolrServerException {
        LOG.info("suggesting tag");

        final String suggestedTag = keywordsLearner.suggestTag(comment);

        final ModelAndView modelAndView = new ModelAndView(PATH);
        modelAndView.addObject("csvPresent", inputFileProvider.getCurrentInputFile().isPresent());
        if (suggestedTag.isEmpty()) {
            modelAndView.addObject("error", "Couldn't find any suitable tag.");
        } else {
            modelAndView.addObject("suggestion", suggestedTag);
        }
        modelAndView.addObject("comment", comment);
        return commonModelAndViewEnhancer.addCommonParameters(modelAndView);
    }

}
