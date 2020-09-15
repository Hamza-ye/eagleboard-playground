package com.mass3d.services.core.todotask.hibernate;

import com.mass3d.api.common.SetMap;
import com.mass3d.api.todotask.TodoTask;
import com.mass3d.api.todotask.TodoTaskStore;
import com.mass3d.services.core.common.hibernate.HibernateIdentifiableObjectStore;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateTodoTaskStore
    extends HibernateIdentifiableObjectStore<TodoTask>
    implements TodoTaskStore {
  // -------------------------------------------------------------------------
  // TodoTask
  // -------------------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public List<TodoTask> getTodoTasksWithoutActivities() {
    String hql = "from TodoTask d where size(d.activities) = 0";

    return getQuery(hql).setCacheable(true).list();
//        return getQuery( hql ).setParameter( "domainType", DataElementDomain.AGGREGATE ).setCacheable( true ).list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TodoTask> getTodoTasksWithActivities() {
    String hql = "from TodoTask d where size(d.activities) > 0";

    return getQuery(hql).setCacheable(true).list();
  }

  @Override
  public List<TodoTask> getAllTodoTasksByLastUpdated(Date lastUpdated) {
    return getAllGeLastUpdated(lastUpdated);
  }

  @Override
  public Map<String, Set<String>> getTodoTaskDataSetAssociationMap() {
    final String sql = "select ds.uid as ds_uid, tt.uid as tt_uid from datasetsource d " +
        "left join todotask tt on tt.todotask=d.sourceid " +
        "left join dataset ds on ds.datasetid=d.datasetid";

    final SetMap<String, String> map = new SetMap<>();

    jdbcTemplate.query(sql, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        String dataSetId = rs.getString("ds_uid");
        String todoTaskId = rs.getString("tt_uid");
        map.putValue(todoTaskId, dataSetId);
      }
    });

    return map;
  }

}
