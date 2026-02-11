package student_portal.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import student_portal.demo.entity.Skill;
import student_portal.demo.entity.Student;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    List<Skill> findByStudent(Student student);
}
