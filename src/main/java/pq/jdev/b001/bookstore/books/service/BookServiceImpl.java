package pq.jdev.b001.bookstore.books.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.model.BookInfo;
import pq.jdev.b001.bookstore.books.model.SelectCategory;
import pq.jdev.b001.bookstore.books.model.Upload;
import pq.jdev.b001.bookstore.books.repository.BookRepository;
import pq.jdev.b001.bookstore.books.repository.UploadRepository;
import pq.jdev.b001.bookstore.books.web.dto.BookDTO;
import pq.jdev.b001.bookstore.books.web.dto.UploadInformationDTO;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.repository.CategoryRepository;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.repository.PublisherRepository;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.service.UserService;

@Service
@Transactional
public class BookServiceImpl implements BookService {

	@Autowired
	private UserService userService;

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private UploadRepository uploadRepository;

	@Autowired
	private UploadPathService uploadPathService;

	@Autowired
	private ZipFileService zipFileService;

	@Autowired
	private ServletContext context;

	/**
	 * Method checkInput is used to check if user didn't miss any important
	 * information
	 */
	public boolean checkInput(UploadInformationDTO dto) {
		if (!dto.getTitle().equals("")) {
			if (dto.getPublisherId() >= 0) {
				for (MultipartFile file : dto.getFiles()) {
					if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/** Method save is used to insert a new book to database */
	public UploadInformationDTO save(UploadInformationDTO dto, Person person, List<String> categoriesId)
			throws Exception {
		try {
			Book book = new Book();
			Upload upload = new Upload();
			/** Handling book first */
			/** Set book.title */
			book.setTitle(dto.getTitle());
			/** Set book.price */
			book.setPrice(dto.getPrice());
			/** Set book.domain */
			book.setDomain(dto.getDomain());
			/** Set book.uploadedDate */
			long millis = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(millis);
			book.setUploadedDate(date);
			/** Set book.authors */
			book.setAuthors(dto.getAuthors());
			/** Set book.person */
			book.setPerson(person);
			/** Set book.publisher */
			Publishers dtoPublisher = publisherRepository.getOne(dto.getPublisherId());
			book.setPublisher(dtoPublisher);
			/** Set book.publishedYear */
			book.setPublishedYear(dto.getPublishedYear());
			/** Set book.description */
			book.setDescription(dto.getDescription());
			/** Set book.picture */
			if (dto.getPictureFile() != null) {
				byte[] image = null;
				try {
					image = dto.getPictureFile().getBytes();
				} catch (IOException e) {
				}
				if (image != null && image.length > 0) {
					book.setPicture(image);
				}
			}

			/** Save book to get book.id */
			Book dbBook = bookRepository.save(book);

			/** Upload book's files and handle upload */
			/** Set upload.uploadedDate */
			long millisUploadedDate = System.currentTimeMillis();
			java.sql.Date dateUploadedDate = new java.sql.Date(millisUploadedDate);
			System.out.println(dateUploadedDate);
			upload.setUploadedDate(dateUploadedDate);
			/** Set upload.book */
			upload.setBookId(dbBook.getId());
			/** Save upload to get upload.id */
			Upload dbUpload = uploadRepository.save(upload);
			/** Upload book's files */
			if (dto.getFiles() != null && dto.getFiles().size() > 0 && dbUpload != null) {
				String originalFileUploadName = "";
				try {
					String sourcePath = context.getRealPath("/");
					/** upload.originalFileName */
					for (MultipartFile file : dto.getFiles()) {
						if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
							originalFileUploadName = file.getOriginalFilename();
							break;
						}
					}
					/** upload.modifiedFilePath */
					String modifiedFilePath = "";
					/** Check to zip files or not when upload book's files */
					if (dto.getFiles().size() > 1) {
						for (MultipartFile file : dto.getFiles()) {
							String filename = file.getOriginalFilename();
							String modifiedFileName = FilenameUtils.getBaseName(filename) + "."
									+ FilenameUtils.getExtension(filename);
							File storeFile = uploadPathService.getFilePath(modifiedFileName,
									"uploads" + File.separator + dbUpload.getId());
							if (storeFile != null) {
								try {
									FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						modifiedFilePath = sourcePath + "uploads" + File.separator + dbUpload.getId() + ".zip";
						/** Zip all book's files */
						File dir = new File(sourcePath + "uploads" + File.separator + dbUpload.getId());
						zipFileService.zipDirectory(dir, modifiedFilePath);
						/** Delete temporary directory */
						FileUtils.deleteDirectory(dir);
						dbUpload.setModifiedFileName(dbUpload.getId() + ".zip");
					} else {
						modifiedFilePath = sourcePath + "uploads" + File.separator + dbUpload.getId() + "."
								+ FilenameUtils.getExtension(originalFileUploadName);
						for (MultipartFile file : dto.getFiles()) {
							String filename = file.getOriginalFilename();
							String modifiedFileName = dbUpload.getId() + "." + FilenameUtils.getExtension(filename);
							File storeFile = uploadPathService.getFilePath(modifiedFileName, "uploads");
							if (storeFile != null) {
								try {
									FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
									if (storeFile.isFile()) {
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						dbUpload.setModifiedFileName(
								dbUpload.getId() + "." + FilenameUtils.getExtension(originalFileUploadName));
					}
					/** Set upload.originalFileName */
					dbUpload.setOriginalFileName(originalFileUploadName);
					/** Set upload.modifiedFileName */
					dbUpload.setModifiedFileName(dbUpload.getId() + ".zip");
					/** Set upload.modifiedFilePath */
					dbUpload.setModifiedFilePath(sourcePath + "uploads" + File.separator + dbUpload.getId());
					/** Save upload */
					uploadRepository.save(dbUpload);
				} catch (Exception e) {
					e.printStackTrace();
				}
				/** Complete handling with upload */
			}
			book.setUploads(dbUpload);
			/** Set book.categories */
			Set<Category> categories = new HashSet<Category>();
			Category t = new Category();
			for (String categoryStringId : categoriesId) {
				Long categoryId = Long.parseLong(categoryStringId);
				t = categoryRepository.getOne(categoryId);
				categories.add(t);
				t = new Category();
			}
			dbBook.setCategories(categories);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** Complete handling with book */
		return dto;
	}

	public boolean checkRightInteraction(User user, Book book) throws Exception {
		Person currentUser = userService.findByUsername(user.getUsername());
		if (currentUser.getPower() == 2) {
			return true;
		} else if (currentUser.getPower() == 1) {
			try {
				Book bookFound = bookRepository.findByPersonIdandBookId(currentUser.getId(), book.getId());
				if (bookFound == null) {
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public UploadInformationDTO update(UploadInformationDTO dto, Person person, List<String> categoriesId,
			Book editBook) throws Exception {
		try {
			Long bookid = editBook.getId();
			Upload upload = new Upload();
			/** Update book.title */
			bookRepository.saveUpdateTitle(bookid, dto.getTitle());
			/** Update book.price */
			bookRepository.saveUpdatePrice(bookid, dto.getPrice());
			/** Update book.domain */
			bookRepository.saveUpdateDomain(bookid, dto.getDomain());
			/** Update book.uploadedDate */
			long millis = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(millis);
			System.out.println(date);
			bookRepository.saveUpdateUploadedDate(bookid, date);
			/** Update book.authors */
			bookRepository.saveUpdateAuthors(bookid, dto.getAuthors());
			/** Update book.person */
			bookRepository.saveUpdatePerson(bookid, person);
			/** Update book.publisher */
			Publishers dtoPublisher = publisherRepository.getOne(dto.getPublisherId());
			bookRepository.saveUpdatePublisher(bookid, dtoPublisher);
			/** Update book.publishedYear */
			bookRepository.saveUpdatePublishedYear(bookid, dto.getPublishedYear());
			/** Update book.description */
			bookRepository.saveUpdateDescription(bookid, dto.getDescription());
			/** Update book.picture */

			/** Set book.picture */
			if (dto.getPictureFile() != null) {
				byte[] image = null;
				try {
					image = dto.getPictureFile().getBytes();
				} catch (IOException e) {
				}
				if (image != null && image.length > 0) {
					bookRepository.saveUpdatePicture(bookid, image);
				}
			}
			/** Check if user upload files or not */
			if (checkInput(dto)) {
				/** Upload book's files and handle upload */
				/** Set upload.uploadedDate */
				long millisUploadedDate = System.currentTimeMillis();
				java.sql.Date dateUploadedDate = new java.sql.Date(millisUploadedDate);
				upload.setUploadedDate(dateUploadedDate);
				/** Set upload.book */
				upload.setBookId(editBook.getId());
				/** Save upload to get upload.id */
				Upload dbUpload = uploadRepository.save(upload);
				/** Upload book's files */
				if (dto.getFiles() != null && dto.getFiles().size() > 0 && dbUpload != null) {
					String originalFileUploadName = "";
					try {
						String sourcePath = context.getRealPath("/");
						/** upload.originalFileName */
						for (MultipartFile file : dto.getFiles()) {
							if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
								originalFileUploadName = file.getOriginalFilename();
								break;
							}
						}
						/** upload.modifiedFilePath */
						String modifiedFilePath = "";
						/** Check to zip files or not when upload book's files */
						if (dto.getFiles().size() > 1) {
							for (MultipartFile file : dto.getFiles()) {
								String filename = file.getOriginalFilename();
								String modifiedFileName = FilenameUtils.getBaseName(filename) + "."
										+ FilenameUtils.getExtension(filename);
								File storeFile = uploadPathService.getFilePath(modifiedFileName,
										"uploads" + File.separator + dbUpload.getId());
								if (storeFile != null) {
									try {
										FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							modifiedFilePath = sourcePath + "uploads" + File.separator + dbUpload.getId() + ".zip";
							/** Zip all book's files */
							File dir = new File(sourcePath + "uploads" + File.separator + dbUpload.getId());
							zipFileService.zipDirectory(dir, modifiedFilePath);
							/** Delete temporary directory */
							FileUtils.deleteDirectory(dir);
						} else {
							modifiedFilePath = sourcePath + "uploads" + File.separator + dbUpload.getId()
									+ FilenameUtils.getExtension(originalFileUploadName);
							for (MultipartFile file : dto.getFiles()) {
								String filename = file.getOriginalFilename();
								String modifiedFileName = dbUpload.getId() + "." + FilenameUtils.getExtension(filename);
								File storeFile = uploadPathService.getFilePath(modifiedFileName, "uploads");
								if (storeFile != null) {
									try {
										FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
										if (storeFile.isFile()) {
										}

									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						/** Set upload.originalFileName */
						dbUpload.setOriginalFileName(originalFileUploadName);
						/** Set upload.modifiedFileName */
						dbUpload.setModifiedFileName(dbUpload.getId() + ".zip");
						/** Set upload.modifiedFilePath */
						dbUpload.setModifiedFilePath(sourcePath + "uploads" + File.separator + dbUpload.getId());
						/** Save upload */
						uploadRepository.save(dbUpload);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				/** Complete handling with upload */
				editBook.setUploads(dbUpload);
			}

			/** Set book.categories */
			Set<Category> categories = new HashSet<Category>();
			Category t = new Category();
			for (String categoryStringId : categoriesId) {
				Long categoryId = Long.parseLong(categoryStringId);
				t = categoryRepository.getOne(categoryId);
				categories.add(t);
				t = new Category();
			}
			editBook.setCategories(categories);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	public List<BookDTO> viewAllBooks() {
		List<Book> allBooks = bookRepository.findAll();
		List<BookDTO> books = new ArrayList<BookDTO>();
		BookDTO temp = new BookDTO();
		for (Book book : allBooks) {
			String stringCategories = "";
			for (Category category : book.getCategories()) {
				stringCategories = stringCategories + category.getName() + ", ";
			}
			temp.setCurrentBook(book);
			books.add(temp);
			temp = new BookDTO();
		}
		return books;
	}

	public Book findBookByID(Long id) {
		Book book = bookRepository.getOne(id);
		return book;
	}

	public List<Publishers> showAllPublishers() {
		List<Publishers> publishers = publisherRepository.findAll();
		return publishers;
	}

	public List<Category> showAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return categories;
	}

	public List<SelectCategory> showAllCategoriesWithFlag(Book editBook) {
		List<Category> categories = categoryRepository.findAll();
		List<SelectCategory> selectCategories = new ArrayList<SelectCategory>();
		SelectCategory temp = new SelectCategory();
		for (int i = 0; i < categories.size(); i++) {
			temp.setCategory(categories.get(i));
			for (Category o : categoryRepository.findCategoryByIdBook(editBook.getId())) {
				if (o.getId() == categories.get(i).getId()) {
					temp.setFlag(1);
				}
			}
			selectCategories.add(temp);
			temp = new SelectCategory();
		}
		return selectCategories;
	}

	@Override
	public void changePublisher(Long idFrom, Long idTo) {
		bookRepository.changePublisher(idFrom, idTo);
	}

	@Override
	public List<Book> findBookByCategories(Collection<Category> categories) {
		return bookRepository.findByCategories(categories);
	}

	// @Override
	// public Book findBook(Long bookId) {
	// return bookRepository.findBook(bookId);
	// }

	@Override
	public void changeCategory(long idTo, long idFrom) {
		Category cateTo = categoryRepository.findById(idTo);
		Collection<Category> categoryCollection = new HashSet<Category>();
		Set<Category> categorySet = new HashSet<Category>();
		categoryCollection.add(categoryRepository.findById(idFrom));
		List<Book> lb = findBookByCategories(categoryCollection);
		for (Book b : lb) {
			List<Category> lc = categoryRepository.findCategoryByIdBook(b.getId());
			for (Category c : lc) {
				if (c.getId() != idFrom)
					categorySet.add(c);
			}
			if (categorySet == null)
				categorySet.add(cateTo);
			Book book = bookRepository.findByid(b.getId());
			book.setCategories(categorySet);
			System.out.println(book.getId());
			bookRepository.save(book);
			categorySet = new HashSet<Category>();
		}
	}

	@Override
	public List<UploadInformationDTO> searchAutocomplete(String keyword) {
		List<UploadInformationDTO> query =  bookRepository.listUploadInformation(keyword);
		return query;
	}

}
