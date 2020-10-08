package com.mass3d.external.location;

import static java.io.File.separator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//@Service
public class DefaultLocationManager
    implements LocationManager {

  private static final Log log = LogFactory.getLog(DefaultLocationManager.class);

  private static final String DEFAULT_DHIS2_HOME = "/opt/dhis2";

  private static final String DEFAULT_ENV_VAR = "DHIS2_HOME";
  private static final String DEFAULT_SYS_PROP = "dhis2.home";

  private String externalDir = null;
  private String environmentVariable;
  private String systemProperty;

  public DefaultLocationManager( String environmentVariable, String systemProperty )
  {
    this.environmentVariable = environmentVariable;
    this.systemProperty = systemProperty;
  }

  public static DefaultLocationManager getDefault()
  {
    return new DefaultLocationManager( DEFAULT_ENV_VAR, DEFAULT_SYS_PROP );
  }

  public void setExternalDir(String externalDir) {
    this.externalDir = externalDir;
  }

  public void setSystemProperty(String systemProperty) {
    this.systemProperty = systemProperty;
  }

  @PostConstruct
  public void init() {
    String path = System.getProperty(systemProperty);

    if (path != null) {
      log.info("System property " + systemProperty + " points to " + path);

      if (directoryIsValid(new File(path))) {
        externalDir = path;
      }
    } else {
      log.info("System property " + systemProperty + " not set");

      path = System.getenv(environmentVariable);

      if (path != null) {
        log.info("Environment variable " + environmentVariable + " points to " + path);

        if (directoryIsValid(new File(path))) {
          externalDir = path;
        }
      } else {
        log.info("Environment variable " + environmentVariable + " not set");

        path = DEFAULT_DHIS2_HOME;

        if (directoryIsValid(new File(path))) {
          externalDir = path;
          log.info("Home directory set to " + DEFAULT_DHIS2_HOME);
        }
      }
    }
  }

  // -------------------------------------------------------------------------
  // Init
  // -------------------------------------------------------------------------

  @Override
  public InputStream getInputStream(String fileName)
      throws LocationManagerException {
    return getInputStream(fileName, new String[0]);
  }

  // -------------------------------------------------------------------------
  // LocationManager implementation
  // -------------------------------------------------------------------------

  // -------------------------------------------------------------------------
  // InputStream
  // -------------------------------------------------------------------------

  @Override
  public InputStream getInputStream(String fileName, String... directories)
      throws LocationManagerException {
    File file = getFileForReading(fileName, directories);

    try {
      InputStream in = new BufferedInputStream(new FileInputStream(file));

      return in;
    } catch (FileNotFoundException ex) {
      throw new LocationManagerException("Could not find file", ex);
    }
  }

  @Override
  public File getFileForReading(String fileName)
      throws LocationManagerException {
    return getFileForReading(fileName, new String[0]);
  }

  // -------------------------------------------------------------------------
  // File for reading
  // -------------------------------------------------------------------------

  @Override
  public File getFileForReading(String fileName, String... directories)
      throws LocationManagerException {
    File directory = buildDirectory(directories);

    File file = new File(directory, fileName);

    if (!canReadFile(file)) {
      throw new LocationManagerException("File " + file.getAbsolutePath() + " cannot be read");
    }

    return file;
  }

  @Override
  public OutputStream getOutputStream(String fileName)
      throws LocationManagerException {
    return getOutputStream(fileName, new String[0]);
  }

  // -------------------------------------------------------------------------
  // OutputStream
  // -------------------------------------------------------------------------

  @Override
  public OutputStream getOutputStream(String fileName, String... directories)
      throws LocationManagerException {
    File file = getFileForWriting(fileName, directories);

    try {
      OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

      return out;
    } catch (FileNotFoundException ex) {
      throw new LocationManagerException("Could not find file", ex);
    }
  }

  @Override
  public File getFileForWriting(String fileName)
      throws LocationManagerException {
    return getFileForWriting(fileName, new String[0]);
  }

  // -------------------------------------------------------------------------
  // File for writing
  // -------------------------------------------------------------------------

  @Override
  public File getFileForWriting(String fileName, String... directories)
      throws LocationManagerException {
    File directory = buildDirectory(directories);

    if (!directoryIsValid(directory)) {
      throw new LocationManagerException(
          "Directory " + directory.getAbsolutePath() + " cannot be created");
    }

    File file = new File(directory, fileName);

    return file;
  }

  @Override
  public File buildDirectory(String... directories)
      throws LocationManagerException {
    if (externalDir == null) {
      throw new LocationManagerException("External directory not set");
    }

    StringBuffer directoryPath = new StringBuffer(externalDir + separator);

    for (String dir : directories) {
      directoryPath.append(dir + separator);
    }

    return new File(directoryPath.toString());
  }

  @Override
  public File getExternalDirectory()
      throws LocationManagerException {
    if (externalDir == null) {
      throw new LocationManagerException("External directory not set");
    }

    return new File(externalDir);
  }

  // -------------------------------------------------------------------------
  // External directory and environment variable
  // -------------------------------------------------------------------------

  @Override
  public String getExternalDirectoryPath()
      throws LocationManagerException {
    if (externalDir == null) {
      throw new LocationManagerException("External directory not set");
    }

    return externalDir;
  }

  @Override
  public boolean externalDirectorySet() {
    return externalDir != null;
  }

  @Override
  public String getEnvironmentVariable() {
    return environmentVariable;
  }

//  public void setEnvironmentVariable(String environmentVariable) {
//    this.environmentVariable = environmentVariable;
//  }

  // -------------------------------------------------------------------------
  // Supportive methods
  // -------------------------------------------------------------------------

  /**
   * Tests whether the file exists and can be read by the application.
   */
  private boolean canReadFile(File file) {
    if (!file.exists()) {
      log.info("File " + file.getAbsolutePath() + " does not exist");

      return false;
    }

    if (!file.canRead()) {
      log.info("File " + file.getAbsolutePath() + " cannot be read");

      return false;
    }

    return true;
  }

  /**
   * Tests whether the directory is writable by the application if the directory exists. Tries to
   * create the directory including necessary parent directories if the directory does not exists,
   * and tests whether the directory construction was successful and not prevented by a
   * SecurityManager in any way.
   */
  private boolean directoryIsValid(File directory) {
    if (directory.exists()) {
      if (!directory.canWrite()) {
        log.info("Directory " + directory.getAbsolutePath() + " is not writeable");

        return false;
      }
    } else {
      try {
        if (!directory.mkdirs()) {
          log.info("Directory " + directory.getAbsolutePath() + " cannot be created");

          return false;
        }
      } catch (SecurityException ex) {
        log.info("Directory " + directory.getAbsolutePath() + " cannot be accessed");

        return false;
      }
    }

    return true;
  }
}
