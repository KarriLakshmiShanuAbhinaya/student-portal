package student_portal.demo.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import student_portal.demo.entity.Student;
import student_portal.demo.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;



    // ================== STUDENT REGISTRATION ==================
    // Generates branch-wise UserID and sends mail

    public Student register(Student student) {

        long count = studentRepo.countByBranch(student.getBranch());
        String userId = student.getBranch() + (count + 1);

        student.setUserId(userId);

        Student savedStudent = studentRepo.save(student);

//        sendUserIdMail(savedStudent.getEmail(), savedStudent.getUserId());

        return savedStudent;
    }

    // ================== EMAIL USER ID ==================



    // ================== OTP GENERATION ==================


    // ================== SEND OTP ==================


}

