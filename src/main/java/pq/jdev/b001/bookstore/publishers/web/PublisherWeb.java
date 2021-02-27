package pq.jdev.b001.bookstore.publishers.web;

import javax.validation.constraints.NotEmpty;

public class PublisherWeb {

	@NotEmpty
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
