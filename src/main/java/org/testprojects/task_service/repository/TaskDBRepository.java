package org.testprojects.task_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.testprojects.task_service.model.Task;

public interface TaskDBRepository extends JpaRepository<Task, Long> {

}
