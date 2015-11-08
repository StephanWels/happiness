package ste.wel.happiness.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ste.wel.happiness.CommonModelAndViewEnhancer;
import ste.wel.happiness.InputFileProvider;

@Controller
public class HomeController {
    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);
    private static final String PATH = "main";

    @Autowired
    InputFileProvider inputFileProvider;

    @Autowired
    CommonModelAndViewEnhancer commonModelAndViewEnhancer;

    @RequestMapping("/")
    public ModelAndView home() {
        final ModelAndView modelAndView = new ModelAndView(PATH);
        modelAndView.addObject("csvPresent", inputFileProvider.getCurrentInputFile().isPresent());
        return commonModelAndViewEnhancer.addCommonParameters(modelAndView);
    }
}
