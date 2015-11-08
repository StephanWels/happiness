package ste.wel.happiness.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ste.wel.happiness.CommonModelAndViewEnhancer;
import ste.wel.happiness.HappinessKeywordsLearner;
import ste.wel.happiness.InputFileProvider;
import ste.wel.happiness.ModelEvaluator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Controller
public class EvaluateController {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluateController.class);
    public static final String PATH = "main";
    NumberFormat decimalFormat = DecimalFormat.getPercentInstance();

    @Autowired
    @Qualifier("goodLearner")
    HappinessKeywordsLearner goodLearner;

    @Autowired
    @Qualifier("badLearner")
    HappinessKeywordsLearner badLearner;

    @Autowired
    ModelEvaluator modelEvaluator;

    @Autowired
    InputFileProvider inputFileProvider;

    @Autowired
    CommonModelAndViewEnhancer commonModelAndViewEnhancer;

    @RequestMapping(value = "/evaluateSuggestion", method = RequestMethod.GET)
    public ModelAndView evaluateModel() {
        final ModelAndView modelAndView = new ModelAndView(PATH);
        if (!inputFileProvider.getCurrentInputFile().isPresent()){
            modelAndView.addObject("csvPresent", false);
            return commonModelAndViewEnhancer.addCommonParameters(modelAndView);
        }

        modelAndView.addObject("csvPresent", true);
        modelAndView.addObject("badAccuracy", decimalFormat.format(modelEvaluator.calcuateAccuracy(badLearner)));
        modelAndView.addObject("goodAccuracy", decimalFormat.format(modelEvaluator.calcuateAccuracy(goodLearner)));
        return commonModelAndViewEnhancer.addCommonParameters(modelAndView);
    }
}
