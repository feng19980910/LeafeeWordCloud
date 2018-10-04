package LeafeeWordCloud;

class Word {
    private String str;
    private int count;

    public Word(Word w) {
        this(w.str, w.count);
    }
    public Word(String str) {
        this(str, 1);
    }
    public Word(String str, int count) {
        this.str = str;
        this.count = count;
    }
    public boolean equals(Word w) {
        return this.str.equals(w.str);
    }
    public boolean equals(String str) {
        return this.str.equals(str);
    }
    public boolean isBigger(Word w) {return this.count > w.count; }
    public String toString() { return this.str; }
    public void count() {
        ++this.count;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) { this.count = count; }
    public String getString() {
        return this.str;
    }
}

