import java.util.ArrayList;


public class Protocol {
	public static final int NEW = 0;
	public static final int SUBMIT = 1;
	public static final int GET = 2;
	public static final int REMOVE = 3;
	public static final int FAULT = 4;
	public static final String[] keywords = {"submit","get","remove"};
	public static final String[] bookKeywords = {"title","author","location"};
	private ArrayList<Book> books = new ArrayList<Book>();
	
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
				result = "Cannot parse command.";
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
		
		if (!book.hasNull()){
			addBook(book);
			return "submit success";
		}
		else{
			return "submit fail";
		}
			
		
	}
	
	private String get(String input){
		return "get";
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
	
	
}
