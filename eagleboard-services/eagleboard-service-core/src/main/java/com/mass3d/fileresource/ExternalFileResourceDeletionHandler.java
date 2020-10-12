package com.mass3d.fileresource;

import com.mass3d.system.deletion.DeletionHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.fileresource.ExternalFileResourceDeletionHandler" )
public class ExternalFileResourceDeletionHandler
    extends DeletionHandler {

  private JdbcTemplate jdbcTemplate;

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public String getClassName() {
    return ExternalFileResource.class.getSimpleName();
  }

  @Override
  public String allowDeleteFileResource(FileResource fileResource) {
    String sql =
        "SELECT COUNT(*) FROM externalfileresource WHERE fileresourceid=" + fileResource.getId();

    int result = jdbcTemplate.queryForObject(sql, Integer.class);

    return result == 0 || fileResource.getStorageStatus() != FileResourceStorageStatus.STORED ? null
        : ERROR;
  }

  @Override
  public void deleteFileResource(FileResource fileResource) {
    String sql = "DELETE FROM externalfileresource WHERE fileresourceid=" + fileResource.getId();

    jdbcTemplate.execute(sql);
  }
}
