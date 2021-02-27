package pq.jdev.b001.bookstore.books.service;

import java.io.File;
import java.io.IOException;

public interface ZipFileService {

	public void zipDirectory(File dir, String zipDirName);

	public int populateFilesList(File dir) throws IOException;

	public void zipSingleFile(File file, String zipFileName);

}
