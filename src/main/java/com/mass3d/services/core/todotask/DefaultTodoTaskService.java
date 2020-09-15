package com.mass3d.services.core.todotask;

import com.google.common.collect.Lists;
import com.mass3d.api.todotask.TodoTask;
import com.mass3d.api.todotask.TodoTaskService;
import com.mass3d.api.todotask.TodoTaskStore;
import com.mass3d.api.user.CurrentUserService;
import com.mass3d.api.user.User;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultTodoTaskService
    implements TodoTaskService {

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------
  private TodoTaskStore todoTaskStore;
  private CurrentUserService currentUserService;

  public void setTodoTaskStore(TodoTaskStore todoTaskStore) {
    this.todoTaskStore = todoTaskStore;
  }

  public void setCurrentUserService(CurrentUserService currentUserService) {
    this.currentUserService = currentUserService;
  }

  // -------------------------------------------------------------------------
  // TodoTask
  // -------------------------------------------------------------------------

  @Override
  public Long addTodoTask(TodoTask todoTask) {
    todoTaskStore.save(todoTask);
    return todoTask.getId();
  }

  @Override
  public void updateTodoTask(TodoTask todoTask) {
    todoTaskStore.update(todoTask);
  }

  @Override
  public void deleteTodoTask(TodoTask todoTask) {
    todoTaskStore.delete(todoTask);
  }

  @Override
  public TodoTask getTodoTask(Long id) {
    return todoTaskStore.get(id);
  }

  @Override
  public TodoTask getTodoTask(String uid) {
    return todoTaskStore.getByUid(uid);
  }

  @Override
  public TodoTask getTodoTaskNoAcl(String uid) {
    return todoTaskStore.getByUidNoAcl(uid);
  }

  @Override
  public List<TodoTask> getAllTodoTasks() {
    return todoTaskStore.getAll();
  }

  @Override
  public List<TodoTask> getTodoTasksByUid(Collection<String> uids) {
    return todoTaskStore.getByUid(uids);
  }

  @Override
  public List<TodoTask> getTodoTasksWithoutActivities() {
    return todoTaskStore.getTodoTasksWithoutActivities();
  }

  @Override
  public List<TodoTask> getTodoTasksWithActivities() {
    return todoTaskStore.getTodoTasksWithActivities();
  }

//  @Override
//  public List<TodoTask> getCurrentUserTodoTasks() {
//    User user = currentUserService.getCurrentUser();
//
//    if (user == null) {
//      return Lists.newArrayList();
//    }
//
//    if (user.isSuper()) {
//      return getAllTodoTasks();
//    } else {
//      return Lists.newArrayList(user.getUserCredentials().getAllTodoTasks());
//    }
//  }

  @Override
  public List<TodoTask> getAllTodoTasksByLastUpdated(Date lastUpdated) {
    return todoTaskStore.getAllTodoTasksByLastUpdated(lastUpdated);
  }

  @Override
  public Map<String, Set<String>> getTodoTaskDataSetAssociationMap() {
    return todoTaskStore.getTodoTaskDataSetAssociationMap();
  }

  @Override
  public List<TodoTask> getUserDataRead(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllTodoTasks() : todoTaskStore.getDataReadAll(user);
  }

  @Override
  public List<TodoTask> getAllDataRead() {
    User user = currentUserService.getCurrentUser();

    return getUserDataRead(user);
  }

  @Override
  public List<TodoTask> getAllDataWrite() {
    User user = currentUserService.getCurrentUser();

    return getUserDataWrite(user);
  }

  @Override
  public List<TodoTask> getUserDataWrite(User user) {
    if (user == null) {
      return Lists.newArrayList();
    }

    return user.isSuper() ? getAllTodoTasks() : todoTaskStore.getDataWriteAll(user);
  }

}
