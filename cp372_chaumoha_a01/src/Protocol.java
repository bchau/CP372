import java.util.ArrayList;


public class Protocol {
	
	//requests
	public static final int NEW = 0;
	public static final int SUBMIT = 1;
	public static final int GET = 2;
	public static final int REMOVE = 3;
	public static final int FAULT = 4;
	
	//keywords
	public static final String[] keywords = {"submit","get","remove"};
	public static final String[] bookKeywords = {"title","author","location"};
	
	//books
	private ArrayList<Book> books = new ArrayList<Book>();
	
	//responses
	public static final String SUBMIT_SUCCESS = "Book submitted successfully.";
	public static final String PARSE_FAIL = "Unable to parse request.";
	public static final String GET_FAIL = "Book cannot be found.";
	public static final String REMOVE_SUCCESS = "Book(s) removed.";
	public static final String REMOVE_FAIL = "Book(s) not found.";
	
	public String processInput(int command, String input){
		String result = "";
		switch(command){
			case NEW:
				result = "Server has been connected.";
				break;
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
				result += " Incorrect command.";
				break;
			default:
				break;
		}
		return result;
	}
	
	private String submit(String input){
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
				temp += tokens[i].trim();
				i++;
			}
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
				return PARSE_FAIL;
		}
		
		if (!book.hasNull()){
			addBook(book);
			return SUBMIT_SUCCESS;
		}
		else{
			return PARSE_FAIL;
		}
			
		
	}
	
	private String get(String input){
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
				temp += tokens[i].trim();
				i++;
			}
			if (!temp.equals("")){
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
		}
		if (!book.allNull()){
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
				return printBooks(bookSearch);
			}
		}
		else{
			return PARSE_FAIL;
		}
	}
	
	private String remove(String input){
		return "remove";
	}
	
	public static boolean isKeyword(String input){
		for (int i = 0; i < keywords.length; i++){
			if (input.toLowerCase().equalsIgnoreCase(keywords[i]))
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
			if (input.toLowerCase().equalsIgnoreCase(bookKeywords[i]))
				return true;
		}
		return false;
	}
	
	private String printBooks(ArrayList<Book> b){
		String result = "success"; //temporary string, will remove later
		for (int i = 0; i < b.size(); i++){
			Book book = b.get(i);
			result += "\n\n"+book.getTitle()+"\n"+book.getAuthor()+"\n"+book.getLocation();
			
		}
		return result;
	}
}
