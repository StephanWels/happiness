package ste.wel.happiness.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ste.wel.happiness.CommonModelAndViewEnhancer;
import ste.wel.happiness.ConfigurationProvider;
import ste.wel.happiness.InputFileProvider;
import ste.wel.happiness.ModelEvaluator;

import java.util.Optional;

@Controller
public class ConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluateController.class);
    public static final String PATH = "main";
    @Autowired
    ModelEvaluator modelEvaluator;

    @Autowired
    InputFileProvider inputFileProvider;

    @Autowired
    ConfigurationProvider configurationProvider;

    @Autowired
    CommonModelAndViewEnhancer commonModelAndViewEnhancer;

    @RequestMapping(value = "/configure", method = RequestMethod.POST)
    public ModelAndView configureLearner(@RequestParam Optional<Integer> k,
                                         @RequestParam Optional<Integer> minTermFrequency,
                                         @RequestParam Optional<Integer> minDocumentFrequency) {
        final ModelAndView modelAndView = new ModelAndView(PATH);
        modelAndView.addObject("csvPresent", inputFileProvider.getCurrentInputFile().isPresent());

        k.ifPresent(value -> configurationProvider.setK(value));
        minDocumentFrequency.ifPresent(value -> configurationProvider.setMinDocumentFrequency(value));
        minTermFrequency.ifPresent(value -> configurationProvider.setMinTermFrequency(value));


        return commonModelAndViewEnhancer.addCommonParameters(modelAndView);
    }
}
