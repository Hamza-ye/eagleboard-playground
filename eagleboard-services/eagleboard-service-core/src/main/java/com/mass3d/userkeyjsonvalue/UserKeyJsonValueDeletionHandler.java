package com.mass3d.userkeyjsonvalue;

import com.mass3d.system.deletion.DeletionHandler;
import com.mass3d.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.userkeyjsonvalue.UserKeyJsonValueDeletionHandler" )
public class UserKeyJsonValueDeletionHandler
    extends DeletionHandler
{
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    protected String getClassName()
    {
        return UserKeyJsonValue.class.getSimpleName();
    }

    @Override
    public void deleteUser( User user )
    {
        jdbcTemplate.execute( "DELETE FROM userkeyjsonvalue WHERE userid = " + user.getId());
    }
}
