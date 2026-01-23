package Entities;

public class Books {
    private int id;
    private String title;
    private String author;
    private int publication_year;

    public Books() {

    }

    public Books(String title, String author, int publication_year) {
        setTitle(title);
        setAuthor(author);
        setPublicationYear(publication_year);
    }

    public Books(int id, String title, String author, int publication_year) {
        this(title, author, publication_year);
        setId(id);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public int getPublicationYear() {
        return publication_year;
    }
    public void setPublicationYear(int publication_year) {
        this.publication_year = publication_year;
    }
    @Override
    public String toString() {
        return "Books{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publication_year=" + publication_year +
                '}';
    }
}
