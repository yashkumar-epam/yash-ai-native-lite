package com.epam.taskflow.taskflow_api.repository;

import com.epam.taskflow.taskflow_api.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	Page<Task> findAll(Pageable pageable);

    /**
     * Find all tasks by status.
     *
     * @param status the task status to search for
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(String status);

	Page<Task> findByStatus(String status, Pageable pageable);

    /**
     * Find all tasks by priority.
     *
     * @param priority the task priority to search for
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(String priority);

	Page<Task> findByPriority(String priority, Pageable pageable);

    /**
     * Find all tasks by title containing the keyword (case-insensitive).
     *
     * @param keyword the keyword to search for in task titles
     * @return list of tasks with titles containing the keyword
     */
    List<Task> findByTitleContainingIgnoreCase(String keyword);
}

