package pq.jdev.b001.bookstore.books.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.books.model.Upload;

@Repository
public interface UploadRepository extends CrudRepository<Upload, Long> {
	@Query("SELECT u FROM Upload u WHERE u.bookId = :id and u.uploadedDate = :uploadedDate")
	public Upload findUploadByIdBookandUploadedDate(@Param("id") Long id, @Param("uploadedDate") Date uploadedDate);
	@Query("SELECT u FROM Upload u WHERE u.bookId = :id")
	public List<Upload> findUploadByIdBook(@Param("id") Long id);
	
	@Modifying
    @Query("Delete FROM Upload u where u.bookId = :id")
    void deleteByIdUpload(@Param("id") Long id);
}
