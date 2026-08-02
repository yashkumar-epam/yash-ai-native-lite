package com.epam.taskflow.taskflow_api.repository;

import com.epam.taskflow.taskflow_api.model.Note;
import com.epam.taskflow.taskflow_api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByTask(Task task);

    List<Note> findByTaskId(Long taskId);
}
