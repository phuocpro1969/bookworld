package pq.jdev.b001.bookstore.books.service;

import java.io.File;

public interface UploadPathService {

	public File getFilePath(String modifiedFileName, String path);

	public void deleteAllUploadByIdBook(Long id);
}
