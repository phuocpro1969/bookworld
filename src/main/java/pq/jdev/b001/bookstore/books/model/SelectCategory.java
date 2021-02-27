package pq.jdev.b001.bookstore.books.model;

import pq.jdev.b001.bookstore.category.model.Category;

public class SelectCategory {

	private Category category;
	private int flag = 0;

	public SelectCategory() {

	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
