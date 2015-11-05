package ste.wel.happiness;

import java.util.List;

public class Comment {
    String comment;
    List<String> tags;
    String group;

    public Comment(final String comment, List<String> tags, String group) {
        this.comment = comment;
        this.tags = tags;
        this.group = group;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getGroup() {
        return group;
    }
}
