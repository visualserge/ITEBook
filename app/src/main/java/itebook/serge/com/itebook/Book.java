package itebook.serge.com.itebook;

public class Book {
      private String id;
      private String title;
      private String description;
      private String image;
      private String isbn;
      private String author;
      private String year;
      private String page;
      public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	private String publisher;
      private String download;
      
      public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDownload() {
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}
	private BookResults results;
      
      public BookResults getResults() {
		return results;
	}

	public void setResults(BookResults results) {
		this.results = results;
	}

	public Book(){
    	  
      }
      
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}

