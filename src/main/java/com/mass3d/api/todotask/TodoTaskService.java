package com.mass3d.api.todotask;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TodoTaskService {

  String ID = TodoTaskService.class.getName();

  // -------------------------------------------------------------------------
  // TodoTask
  // -------------------------------------------------------------------------

  /**
   * Adds a TodoTask.
   *
   * @param todoTask The TodoTask to add.
   * @return The generated unique identifier for this TodoTask.
   */
  Long addTodoTask(TodoTask todoTask);

  /**
   * Updates a TodoTask.
   *
   * @param todoTask The TodoTask to update.
   */
  void updateTodoTask(TodoTask todoTask);

  /**
   * Deletes a TodoTask.
   *
   * @param todoTask The TodoTask to delete.
   */
  void deleteTodoTask(TodoTask todoTask);

  /**
   * Get a TodoTask
   *
   * @param id The unique identifier for the TodoTask to get.
   * @return The TodoTask with the given id or null if it does not exist.
   */
  TodoTask getTodoTask(int id);

  /**
   * Returns the TodoTask with the given UID.
   *
   * @param uid the UID.
   * @return the TodoTask with the given UID, or null if no match.
   */
  TodoTask getTodoTask(String uid);

  /**
   * Returns the TodoTask with the given UID. Bypasses the ACL system.
   *
   * @param uid the UID.
   * @return the TodoTask with the given UID, or null if no match.
   */
  TodoTask getTodoTaskNoAcl(String uid);

  /**
   * Get all TodoTasks.
   *
   * @return A list containing all TodoTasks.
   */
  List<TodoTask> getAllTodoTasks();

  /**
   * Returns a list of todoTasks with the given uids.
   *
   * @param uids the collection of uids.
   * @return a list of todoTasks.
   */
  List<TodoTask> getTodoTasksByUid(Collection<String> uids);


  /**
   * Returns all TodoTasks which are not assigned to any activity.
   *
   * @return all TodoTasks which are not assigned to any activity.
   */
  List<TodoTask> getTodoTasksWithoutActivities();

  /**
   * Returns all TodoTasks which are assigned to at least one activity.
   *
   * @return all TodoTasks which are assigned to at least one activity.
   */
  List<TodoTask> getTodoTasksWithActivities();

  /**
   * Returns the todoTasks associated with the current user. If the current user has the ALL
   * authority then all todoTasks are returned.
   */
  List<TodoTask> getCurrentUserTodoTasks();

  /**
   * Returns all TodoTasks by lastUpdated.
   *
   * @param lastUpdated TodoTasks from this date
   * @return a list of all TodoTasks, or an empty list if there are no TodoTasks.
   */
  List<TodoTask> getAllTodoTasksByLastUpdated(Date lastUpdated);

  /**
   * Creates a mapping between todoTask UID and set of field set UIDs being assigned to the
   * todoTask.
   *
   * @return a map of sets.
   */
  Map<String, Set<String>> getTodoTaskDataSetAssociationMap();

  /**
   * Returns the todoTasks which current user have READ access. If the current user has the ALL
   * authority then all todoTasks are returned.
   */
  List<TodoTask> getAllDataRead();

  /**
   * Returns the todoTasks which current user have WRITE access. If the current user has the ALL
   * authority then all todoTasks are returned.
   */
  List<TodoTask> getAllDataWrite();

}
