public class Book{
		private String title, author, location;

		/**
		 * 
		 * @return book title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * 
		 * @param title Book title
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * 
		 * @return book author
		 */
		public String getAuthor() {
			return author;
		}

		/**
		 * 
		 * @param author Book author
		 */
		public void setAuthor(String author) {
			this.author = author;
		}

		/**
		 * 
		 * @return book location
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * 
		 * @param location Book location
		 */
		public void setLocation(String location) {
			this.location = location;
		}
		
		/**
		 * 
		 * @return True if at least one of the books attributes are null
		 */
		public boolean hasNull(){
			return location == null || author == null || title == null;
		}
		
		/**
		 * 
		 * @return True if all of the books attributes are null
		 */
		public boolean allNull(){
			return location == null && author == null && title == null;
		}
	}