package com.mass3d.fileresource;

import com.google.common.io.ByteSource;
import java.io.File;
import java.net.URI;
import java.util.List;

public interface FileResourceService {

  FileResource getFileResource(String uid);

  List<FileResource> getFileResources(List<String> uids);

  List<FileResource> getOrphanedFileResources();

  String saveFileResource(FileResource fileResource, File file);

  String saveFileResource(FileResource fileResource, byte[] bytes);

  void deleteFileResource(String uid);

  void deleteFileResource(FileResource fileResource);

  ByteSource getFileResourceContent(FileResource fileResource);

  boolean fileResourceExists(String uid);

  void updateFileResource(FileResource fileResource);

  URI getSignedGetFileResourceContentUri(String uid);
}
