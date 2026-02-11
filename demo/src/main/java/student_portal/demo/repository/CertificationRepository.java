package student_portal.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import student_portal.demo.entity.Certification;
import student_portal.demo.entity.Student;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {

    List<Certification> findByStudent(Student student);
}

