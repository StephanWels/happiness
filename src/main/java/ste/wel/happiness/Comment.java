package ste.wel.happiness;

import java.util.List;

public class Comment {
    String comment;
    List<String> tags;

    public Comment(final String comment, List<String> tags) {
        this.comment = comment;
        this.tags = tags;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getTags() {
        return tags;
    }

}
