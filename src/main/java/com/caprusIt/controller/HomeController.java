package com.caprusIt.controller;


import java.util.List;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.caprusIt.entity.MessageModel;
import com.caprusIt.entity.Paganition;
import com.caprusIt.entity.Response;
import com.caprusIt.entity.Student;
import com.caprusIt.entity.UserStorage;
import com.caprusIt.repository.StudentRepository;
import com.caprusIt.service.impl.EmailSenderService;
import com.caprusIt.service.impl.StudentServiceImpl;

import caprusit.security.EncryptionDecryptionAES;



@RestController
 @CrossOrigin("*")
public class HomeController {

  @Autowired
  private StudentServiceImpl studentServiceImpl;
  @Autowired
  private StudentRepository studentRepo;
  @Autowired 
  public EmailSenderService emailSenderService;
  
  @EventListener(ApplicationReadyEvent.class)
	public void sendMail() {
		try {
			String[] toemail={"lokeshallam1999@gmail.com"};
			String body ="Send mail";
			String Subject ="to subject";
			emailSenderService.sendEmail(toemail, Subject, body);
			System.out.println("mail send sucessfully");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	@EventListener(ApplicationReadyEvent.class)
	public void sendMailAttachedment() {
		try {
			String[] toEmail= {"lokeshallam1999@gmail.com","saiabhilash1999@gmail.com"};
			String body="send mail";
String Subject =" send attachment";
String attachment= "C:\\Users\\vishnu.chinthala\\Pictures\\Screenshots\\Screenshot (16).png";
emailSenderService.sendMailWithAttachment(toEmail,body,Subject,attachment);
System.out.println("email send with  attachment");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	    @Autowired
	    private SimpMessagingTemplate simpMessagingTemplate;

	    @MessageMapping("/chat/{to}")
	    public void sendMessage( String to, MessageModel message) {
	        System.out.println("handling send message: " + message + " to: " + to);
	        boolean isExists = UserStorage.getInstance().getUsers().contains(to);
	        if (isExists) {
	            simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
	        }
	    }
	


  @PostMapping(value = "saveStudent")
  public Response saveStudent(@RequestBody Student student) { 
  Response studentResponse=new Response();
  if(student.getName()==null) {
  	studentResponse.setMessage("Name cannot be empty");
  	return studentResponse;
  }
  if(student.getRoll()==null) {
  	studentResponse.setMessage("Roll  cannot be empty");
  	return studentResponse;
  }
 
  
  try {
	String encoderPassword = EncryptionDecryptionAES.encrypt(student.getPassword());
	 student.setPassword(encoderPassword);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
  studentRepo.save(student);


   student.getAddresses();
   studentServiceImpl.saveStudent(student);  
  
   
   studentResponse.setMessage("Student is added successfully");
	return studentResponse;
  
  }
  @GetMapping(value="Paganition")
  public ResponseEntity<Page<Student>>getStudents(Paganition paganition){
	  return new ResponseEntity<>(studentServiceImpl.getStudents(paganition),HttpStatus.OK);
  }
  
  @GetMapping("/registration/{userName}")
  public ResponseEntity<Void> register(@PathVariable String userName) {
      System.out.println("handling register user request: " + userName);
      try {
          UserStorage.getInstance().setUser(userName);
      } catch (Exception e) {
          return ResponseEntity.badRequest().build();
      }
      return ResponseEntity.ok().build();
  }
  
  @GetMapping(value = "getAllStudents")
	public List<Student> findAllStudents(){
  	return studentServiceImpl.findAllStudents();
  }
  @GetMapping(value = "call-procedure")
  public int callProcedure(@RequestParam(required = false, name = "studentId") int studentId, 
		  				   @RequestParam(required = false, name = "addressId") int addressId) {
	  return studentServiceImpl.callProcedure(studentId, addressId);
  }

  
  @PutMapping(value = "updatestudent")
    public Student savestudent( @RequestBody Student student ) {
		return studentServiceImpl.updateStudent(student);
  }
  @DeleteMapping(value = "deletestudentById")
	public String deleteStudent(@RequestParam Integer id) {
		System.out.println("At delete employee");
		studentServiceImpl.deleteStudent (id);
		
		return "Student Deleted";
	}
}

