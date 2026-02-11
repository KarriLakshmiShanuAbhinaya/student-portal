package student_portal.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import student_portal.demo.entity.Resume;
import student_portal.demo.entity.Student;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {

    Resume findByStudent(Student student);
}
