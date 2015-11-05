package ste.wel.happiness;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope
public class InputFileProvider {
    Optional<HappinessIndexInputFile> currentInputFile = Optional.empty();

    public Optional<HappinessIndexInputFile> getCurrentInputFile() {
        return currentInputFile;
    }

    public void setCurrentInputFile(final HappinessIndexInputFile currentCsv) {
        this.currentInputFile = Optional.ofNullable(currentCsv);
    }
}
