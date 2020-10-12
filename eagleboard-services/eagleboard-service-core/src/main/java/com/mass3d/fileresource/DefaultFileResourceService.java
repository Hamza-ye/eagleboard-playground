package com.mass3d.fileresource;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.io.ByteSource;
import com.mass3d.common.IdentifiableObjectStore;
import com.mass3d.scheduling.SchedulingManager;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

@Service("com.mass3d.fileresource.FileResourceService")
public class DefaultFileResourceService
    implements FileResourceService {

  private static final Duration IS_ORPHAN_TIME_DELTA = Hours.TWO.toStandardDuration();

  private static final Predicate<FileResource> IS_ORPHAN_PREDICATE =
      (fr -> !fr.isAssigned());

  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private final IdentifiableObjectStore<FileResource> fileResourceStore;

  //  @Autowired
  private final SessionFactory sessionFactory;
  private final FileResourceContentStore fileResourceContentStore;
  private final SchedulingManager schedulingManager;

  public DefaultFileResourceService(IdentifiableObjectStore<FileResource> fileResourceStore,
      SessionFactory sessionFactory,
      FileResourceContentStore fileResourceContentStore, SchedulingManager schedulingManager) {
    checkNotNull(fileResourceStore);
    checkNotNull(sessionFactory);
    checkNotNull(fileResourceContentStore);
    checkNotNull(schedulingManager);

    this.fileResourceStore = fileResourceStore;
    this.sessionFactory = sessionFactory;
    this.fileResourceContentStore = fileResourceContentStore;
    this.schedulingManager = schedulingManager;
  }

//  public void setFileResourceStore(IdentifiableObjectStore<FileResource> fileResourceStore) {
//    this.fileResourceStore = fileResourceStore;
//  }
//
//  public void setFileResourceContentStore(FileResourceContentStore fileResourceContentStore) {
//    this.fileResourceContentStore = fileResourceContentStore;
//  }
//
//  public void setSchedulingManager(SchedulingManager schedulingManager) {
//    this.schedulingManager = schedulingManager;
//  }

//    private FileResourceUploadCallback uploadCallback;
//
//    public void setUploadCallback( FileResourceUploadCallback uploadCallback )
//    {
//        this.uploadCallback = uploadCallback;
//    }

  // -------------------------------------------------------------------------
  // FileResourceService implementation
  // -------------------------------------------------------------------------

  @Override
  @Transactional(readOnly = true)
  public FileResource getFileResource(String uid) {
    return checkStorageStatus(fileResourceStore.getByUid(uid));
  }

  @Override
  @Transactional(readOnly = true)
  public List<FileResource> getFileResources(List<String> uids) {
    return fileResourceStore.getByUid(uids).stream()
        .map(this::checkStorageStatus)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<FileResource> getOrphanedFileResources() {
    return fileResourceStore.getAllLeCreated(new DateTime().minus(IS_ORPHAN_TIME_DELTA).toDate())
        .stream().filter(IS_ORPHAN_PREDICATE).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public String saveFileResource(FileResource fileResource, File file) {
    return saveFileResourceInternal(fileResource,
        () -> fileResourceContentStore.saveFileResourceContent(fileResource, file));
  }

  @Override
  @Transactional
  public String saveFileResource(FileResource fileResource, byte[] bytes) {
    return saveFileResourceInternal(fileResource,
        () -> fileResourceContentStore.saveFileResourceContent(fileResource, bytes));
  }

  @Override
  @Transactional
  public void deleteFileResource(String uid) {
    if (uid == null) {
      return;
    }

    FileResource fileResource = fileResourceStore.getByUid(uid);

    deleteFileResource(fileResource);
  }

  @Override
  @Transactional
  public void deleteFileResource(FileResource fileResource) {
    if (fileResource == null) {
      return;
    }

    fileResourceContentStore.deleteFileResourceContent(fileResource.getStorageKey());
    fileResourceStore.delete(fileResource);
  }

  @Override
  @Transactional(readOnly = true)
  public ByteSource getFileResourceContent(FileResource fileResource) {
    return fileResourceContentStore.getFileResourceContent(fileResource.getStorageKey());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean fileResourceExists(String uid) {
    return fileResourceStore.getByUid(uid) != null;
  }

  @Override
  @Transactional
  public void updateFileResource(FileResource fileResource) {
    fileResourceStore.update(fileResource);
  }

  @Override
  @Transactional(readOnly = true)
  public URI getSignedGetFileResourceContentUri(String uid) {
    FileResource fileResource = getFileResource(uid);

    if (fileResource == null) {
      return null;
    }

    return fileResourceContentStore.getSignedGetContentUri(fileResource.getStorageKey());
  }

  // -------------------------------------------------------------------------
  // Supportive methods
  // -------------------------------------------------------------------------

  private String saveFileResourceInternal(FileResource fileResource,
      Callable<String> saveCallable) {
    fileResource.setStorageStatus(FileResourceStorageStatus.PENDING);
    fileResourceStore.save(fileResource);
    sessionFactory.getCurrentSession().flush();

    final String uid = fileResource.getUid();

    final ListenableFuture<String> saveContentTask = schedulingManager.executeJob(saveCallable);

//        saveContentTask.addCallback( uploadCallback.newInstance( uid ) );

    return uid;
  }

  private FileResource checkStorageStatus(FileResource fileResource) {
    if (fileResource != null) {
      boolean exists = fileResourceContentStore
          .fileResourceContentExists(fileResource.getStorageKey());

      if (exists) {
        fileResource.setStorageStatus(FileResourceStorageStatus.STORED);
      } else {
        fileResource.setStorageStatus(FileResourceStorageStatus.PENDING);
      }
    }

    return fileResource;
  }
}
