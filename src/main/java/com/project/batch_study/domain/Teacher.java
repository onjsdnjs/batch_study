package com.project.batch_study.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private int age;
    private int classNumber;

    @OneToMany
    List<Student> students;

    @Builder
    public Teacher(String name, int age, int classNumber){
        this.name = name;
        this.age = age;
        this.classNumber = classNumber;
    }

    public void addStudent(Student student){
        if (Optional.ofNullable(students).isPresent() == false) {
            students = new ArrayList<>();
        }
        students.add(student);
    }

}
