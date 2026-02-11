package student_portal.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import student_portal.demo.entity.Project;
import student_portal.demo.entity.Student;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByStudent(Student student);
}

