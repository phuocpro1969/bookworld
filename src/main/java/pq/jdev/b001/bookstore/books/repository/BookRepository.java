package pq.jdev.b001.bookstore.books.repository;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.web.dto.UploadInformationDTO;
import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.users.model.Person;

@Repository
public interface BookRepository extends CrudRepository<Book, Long>, JpaRepository<Book, Long> {

	@Query("select book from Book book where book.person.id = ?1 and book.id = ?2")
	public Book findByPersonIdandBookId(Long personId, Long bookid);

	@Modifying
	@Query("update Book book set book.picture =:picture where book.id =:bookid")
	public void saveUpdatePicture(@Param("bookid") Long bookid, @Param("picture") byte[] picture);

	@Modifying
	@Query("update Book book set book.title =:title where book.id =:bookid")
	public void saveUpdateTitle(@Param("bookid") Long bookid, @Param("title") String title);

	@Modifying
	@Query("update Book book set book.price =:price where book.id =:bookid")
	public void saveUpdatePrice(@Param("bookid") Long bookid, @Param("price") Double price);

	@Modifying
	@Query("update Book book set book.domain =:domain where book.id =:bookid")
	public void saveUpdateDomain(@Param("bookid") Long bookid, @Param("domain") String domain);

	@Modifying
	@Query("update Book book set book.uploadedDate =:uploadedDate where book.id =:bookid")
	public void saveUpdateUploadedDate(@Param("bookid") Long bookid, @Param("uploadedDate") Date uploadedDate);

	@Modifying
	@Query("update Book book set book.authors =:authors where book.id =:bookid")
	public void saveUpdateAuthors(@Param("bookid") Long bookid, @Param("authors") String authors);

	@Modifying
	@Query("update Book book set book.person =:person where book.id =:bookid")
	public void saveUpdatePerson(@Param("bookid") Long bookid, @Param("person") Person person);

	@Modifying
	@Query("update Book book set book.publishedYear =:publishedYear where book.id =:bookid")
	public void saveUpdatePublishedYear(@Param("bookid") Long bookid, @Param("publishedYear") Integer publishedYear);

	@Modifying
	@Query("update Book book set book.publisher =:publisher where book.id =:bookid")
	public void saveUpdatePublisher(@Param("bookid") Long bookid, @Param("publisher") Publishers publisher);
	
	@Modifying
	@Query("update Book book set book.description =:description where book.id =:bookid")
	public void saveUpdateDescription(@Param("bookid") Long bookid, @Param("description") String description);
	
	@Modifying
    @Query("Delete FROM Book b where b.id = :id")
    void deleteByIdB(@Param("id") Long id);

	public Book findByTitle(String title);
	
	public Book findByid(Long id);
	
	@Modifying
	@Query("Update Book book set book.publisher.id = :idTo where book.publisher.id = :idFrom")
	public void changePublisher(@Param("idFrom") Long idFrom, @Param("idTo") Long idTo);

	@Query("SELECT b FROM Book b INNER JOIN b.categories c WHERE c IN (:categories)")
	List<Book> findByCategories(@Param("categories") Collection<Category> categories);

	@Query("SELECT e FROM Book e WHERE e.id =:bookId ")
	public void findBook(@Param("bookId") Long bookId);

	@Query("SELECT new pq.jdev.b001.bookstore.books.web.dto.UploadInformationDTO (b.id, b.title, b.picture) FROM Book b WHERE b.title LIKE %:keyword%")
    List<UploadInformationDTO> listUploadInformation(@Param("keyword") String keyword);

}
