package com.epam.restapi;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.dto.BookDTO;
import com.epam.dto.BookResponseDTO;
import com.epam.exceptions.BookException;
import com.epam.service.interfaces.BookService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("books")
@RestController
public class BookRestApi {

	@Autowired
	private BookService bookService;

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Value("${cloud.aws.end-point.uri}")
	private String endpoint;

	@GetMapping("/send/{message}")
	public void sendMessageToQueue(@PathVariable String message)
	{
		queueMessagingTemplate.send(endpoint,MessageBuilder.withPayload(message).build());
	}
	
	@GetMapping
	public ResponseEntity<List<BookResponseDTO>> viewAllBooks() throws BookException{
		List<BookResponseDTO> bookList = bookService.getBooks();
		log.info("books list is retrieved successfully! = {}",bookList);
		return new ResponseEntity<>(bookList, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<BookResponseDTO> getBookById(@PathVariable int id) throws BookException{
		BookResponseDTO dto = bookService.getById(id);
		if(dto.getName()==null)
			throw new BookException("No book with bookId "+id);
		return ResponseEntity.ok(dto);
	}
	
	@PostMapping
	public ResponseEntity<BookDTO> addNewBook(@Valid @RequestBody BookDTO bookDto) throws BookException{
		BookDTO dto = bookService.addBook(bookDto);
		log.info("book created successfully!\n {}",dto);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteBookById(@PathVariable int id) throws BookException{
		bookService.deleteBookById(id);
		log.info("book deleted successfully!");
		return new ResponseEntity<>("Deleted book Successfully", HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> updateBookById(@PathVariable int id,@Valid @RequestBody BookDTO bookDto) throws BookException{
		BookDTO dto =  bookService.updateBook(bookDto);
		log.info("book updated successfully! {}",dto);
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

}
