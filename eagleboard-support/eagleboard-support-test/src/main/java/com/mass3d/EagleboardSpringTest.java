package com.mass3d;

import java.lang.reflect.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

//@RunWith( SpringRunner.class )
//@SpringBootTest
//@ContextConfiguration(classes= ApplicationTest.class)
@RunWith( SpringRunner.class )
@ActiveProfiles( profiles = { "test" } )
@ContextConfiguration( classes = UnitTestConfiguration.class )
@Transactional
public abstract class EagleboardSpringTest
    extends EagleboardConvenienceTest
{
    // -------------------------------------------------------------------------
    // ApplicationContextAware implementation
    // -------------------------------------------------------------------------

    @Autowired
    protected ApplicationContext context;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Before
    public final void before()
        throws Exception
    {
        executeStartupRoutines();
        setUpTest();
    }

    @After
    public final void after()
        throws Exception
    {
        clearSecurityContext();
        tearDownTest();
    }

    /**
     * Method to override.
     */
    protected void setUpTest()
        throws Exception
    {
    }

    protected void tearDownTest()
        throws Exception
    {
    }

    // -------------------------------------------------------------------------
    // Utility methods
    // -------------------------------------------------------------------------

    /**
     * Retrieves a bean from the application context.
     *
     * @param beanId the identifier of the bean.
     */
    protected Object getBean( String beanId )
    {
        return context.getBean( beanId );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void executeStartupRoutines()
        throws Exception
    {
        String id = "com.mass3d.support.system.startup.StartupRoutineExecutor";

        if ( context != null && context.containsBean( id ) )
        {
            Object object = context.getBean( id );

            Method method = object.getClass().getMethod( "executeForTesting", new Class[0] );

            method.invoke( object, new Object[0] );
        }
    }
}
