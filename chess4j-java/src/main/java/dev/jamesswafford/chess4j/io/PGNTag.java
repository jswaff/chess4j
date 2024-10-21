package dev.jamesswafford.chess4j.io;

public class PGNTag {

    private String tagName;
    private String tagValue;

    public PGNTag(String tagName,String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagValue() {
        return tagValue;
    }

    @Override
    public String toString() {
        return "[" + tagName + " " + tagValue + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PGNTag)) return false;

        PGNTag that = (PGNTag)obj;
        if (!this.getTagName().equals(that.getTagName())) return false;
        if (!this.getTagValue().equals(that.getTagValue())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getTagName().hashCode() + 17 * getTagValue().hashCode();
    }
}
