package com.mass3d.fileresource;

import com.google.common.io.ByteSource;
import java.io.File;
import java.net.URI;

public interface FileResourceContentStore {

  /**
   * Get the content bytes of a FileResource from the file store.
   *
   * @param key the key.
   * @return a ByteSource which provides a stream to the content or null if the content cannot be
   * found or read.
   */
  ByteSource getFileResourceContent(String key);

  /**
   * Save the contents of the byte array to the file store.
   *
   * @param fileResource the FileResource object. Must be complete and include the storageKey,
   * contentLength, contentMd5 and name.
   * @param bytes the byte array.
   * @return the key on success or null if saving failed.
   */
  String saveFileResourceContent(FileResource fileResource, byte[] bytes);

  /**
   * Save the contents of the File to the file store.
   *
   * @param fileResource the FileResource object.
   * @param file the File. Will be consumed upon deletion.
   * @return the key on success or null if saving failed.
   */
  String saveFileResourceContent(FileResource fileResource, File file);

  /**
   * Delete the content bytes of a file resource.
   *
   * @param key the key.
   */
  void deleteFileResourceContent(String key);

  /**
   * Check existence of a file.
   *
   * @param key key of the file.
   * @return true if the file exists in the file store, false otherwise.
   */
  boolean fileResourceContentExists(String key);

  /**
   * Create a signed GET request which gives access to the content.
   *
   * @param key the key.
   * @return a URI containing the signed GET request or null if signed requests are not supported.
   */
  URI getSignedGetContentUri(String key);
}
