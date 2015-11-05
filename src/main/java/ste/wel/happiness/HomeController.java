package ste.wel.happiness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private static final Logger LOG = LoggerFactory.getLogger(Legacy.class);
    private static final String PATH = "main";

    @Autowired
    InputFileProvider inputFileProvider;

    @RequestMapping("/")
    public ModelAndView home() {
        final ModelAndView modelAndView = new ModelAndView(PATH);
        modelAndView.addObject("csvPresent", inputFileProvider.getCurrentInputFile().isPresent());
        return modelAndView;
    }
}
