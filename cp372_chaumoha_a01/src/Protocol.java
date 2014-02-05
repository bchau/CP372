import java.util.ArrayList;


public class Protocol {
	
	//requests
	public static final int SUBMIT = 0;
	public static final int GET = 1;
	public static final int REMOVE = 2;
	public static final int FAULT = 3;
	
	//keywords
	public static final String[] keywords = {"SUBMIT","GET","REMOVE"};
	public static final String[] bookKeywords = {"TITLE","AUTHOR","LOCATION"};
	
	//books
	private ArrayList<Book> books = new ArrayList<Book>();
	
	//responses
	public static final String SUBMIT_SUCCESS = "Book submitted successfully.\n";
	public static final String PARSE_FAIL = "Unable to parse request.\n";
	public static final String GET_FAIL = "Book(s) cannot be found.\n";
	public static final String REMOVE_SUCCESS = "Book(s) removed.\n";
	public static final String REMOVE_FAIL = "Book(s) not found.\n";
	
	public String processInput(int command, String input){
		String result = "";
		switch(command){
			case SUBMIT:
				//update catalogue
				result = submit(input);
				break;
			case GET:
				//send book list to client where is available
				result = get(input);
				break;
			case REMOVE:
				//remove
				result = remove(input);
				break;
			case FAULT:
				result = PARSE_FAIL;
				break;
			default:
				break;
		}
		return result;
	}
	
	private String submit(String input){
		
		Book book = parseInput(input);
		if (book == null || book.hasNull()){
			return PARSE_FAIL;
		}
		else{
			addBook(book);
			return SUBMIT_SUCCESS;
		}	
	}
	
	private String get(String input){
		Book book = parseInput(input);
		if (book == null || book.allNull()){
			return PARSE_FAIL;
		}
		else{
			ArrayList<Book> bookSearch = new ArrayList<Book>();
			bookSearch.addAll(books);
			if (book.getAuthor() != null){
				for (int j = bookSearch.size()-1; j >= 0; j--){
					if (!books.get(j).getAuthor().equals(book.getAuthor()))
						bookSearch.remove(j);
				}
			}
			if (book.getLocation() != null){
				for (int j = bookSearch.size()-1; j >= 0; j--){
					if (!books.get(j).getLocation().equals(book.getLocation()))
						bookSearch.remove(j);
				}
			}
			if (book.getTitle() != null){
				for (int j = bookSearch.size()-1; j >= 0; j--){
					if (!books.get(j).getTitle().equals(book.getTitle()))
						bookSearch.remove(j);
				}
			}
			
			if (bookSearch.isEmpty()){
				return GET_FAIL;
			}
			else{
				return printLocation(bookSearch);
			}
		}
	}
	
	private String remove(String input){
		Book book = parseInput(input);
		if (book == null || book.allNull()){
			return PARSE_FAIL;
		}
		else{
			int booksRemoved = 0;
			if (book.getAuthor() != null){
				for (int j = books.size()-1; j >= 0; j--){
					if (books.get(j).getAuthor().equals(book.getAuthor())){
						books.remove(j);
						booksRemoved++;
					}
				}
			}
			if (book.getLocation() != null){
				for (int j = books.size()-1; j >= 0; j--){
					if (books.get(j).getLocation().equals(book.getLocation())){
						books.remove(j);
						booksRemoved++;
					}
				}
			}
			if (book.getTitle() != null){
				for (int j = books.size()-1; j >= 0; j--){
					if (books.get(j).getTitle().equals(book.getTitle())){
						books.remove(j);
						booksRemoved++;
					}
				}
			}
			
			if (booksRemoved == 0){
				return REMOVE_FAIL;
			}
			else{
				return REMOVE_SUCCESS;
			}
		}
	}
	
	public static boolean isKeyword(String input){
		for (int i = 0; i < keywords.length; i++){
			if (input.equals(keywords[i]))
				return true;
		}
		return false;
	}

	public ArrayList<Book> getBooks() {
		return books;
	}

	public void addBook(Book b) {
		this.books.add(b);
	}
	
	private static boolean isBookKeyword(String input){
		for (int i = 0; i < bookKeywords.length; i++){
			if (input.equals(bookKeywords[i]))
				return true;
		}
		return false;
	}
	
	private String printLocation(ArrayList<Book> b){
		String result = "";
		for (int i = 0; i < b.size(); i++){
			Book book = b.get(i);
			result += "The book \""+book.getTitle()+"\" "+
			"by "+book.getAuthor()+
			" can be found at "+book.getLocation()+"\n";
			
		}
		return result;
	}
	
	private Book parseInput(String input){
		String[] tokens = input.split(" ");
		
		int i = 1;
		String current = null;
		Book book = new Book();
		while (i < tokens.length){
			if (isBookKeyword(tokens[i])){
				current = tokens[i];
				i++;
			}
			String temp = "";
			while (i < tokens.length && !isBookKeyword(tokens[i])){
				temp += tokens[i].trim()+" ";
				i++;
			}
			temp = temp.trim();
			if (!temp.equals("") && current != null){
				if(current.equals("TITLE")){
					book.setTitle(temp);
				}
				else if(current.equals("AUTHOR")){
					book.setAuthor(temp);
				}
				else if(current.equals("LOCATION")){
					book.setLocation(temp);
				}
			}
			else
				return null;
		}
		return book;
	}
}
