package pq.jdev.b001.bookstore.publishers.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.repository.PublisherRepository;

@Service("publisherService")
@Transactional
public class PublisherServiceImpl implements PublisherService {
	@Autowired
	PublisherRepository publisherRepository;

	@Override
	public List<Publishers> findAll() {
		return (List<Publishers>) publisherRepository.findAll();
	}

	@Override
	public void deletePublisher(int publisherId) {
		publisherRepository.deleteById((long) publisherId);

	}

	@Override
	public List<Publishers> findByPublisher(String publisher) {
		return publisherRepository.findByPublisher(publisher);
	}

	@Override
	public Publishers find(long publisherId) {
		return publisherRepository.findById(publisherId);
	}

	@Override
	public Long count() {
		return publisherRepository.count();
	}

	@Override
	public void delete(long id) {
		publisherRepository.deleteById((long) id);
	}

	@Override
	public Publishers findOne(long id) {
		return publisherRepository.findById((long) id);
	}

	@Override
	public void save(Publishers contact) {
		publisherRepository.save(contact);
	}

	/*
	 * @Override public List<Publishers> search(String name) { // TODO
	 * Auto-generated method stub return publisherRepository.findByName(name); }
	 */

	

}
