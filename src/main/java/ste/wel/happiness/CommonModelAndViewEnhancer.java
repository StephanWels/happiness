package ste.wel.happiness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CommonModelAndViewEnhancer {

    @Autowired
    ConfigurationProvider configurationProvider;

    public ModelAndView addCommonParameters(final ModelAndView modelAndView){
        modelAndView.addObject("k", configurationProvider.getK());
        modelAndView.addObject("minTermFrequency", configurationProvider.getMinTermFrequency());
        modelAndView.addObject("minDocumentFrequency", configurationProvider.getMinDocumentFrequency());

        return modelAndView;
    }
}
