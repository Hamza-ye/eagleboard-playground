package com.mass3d.fileresource;

public interface ExternalFileResourceService {

  /**
   * Retrieves ExternalFileResource based on accessToken
   *
   * @param accessToken unique token generated to reference the different ExternalFileResources
   */
  ExternalFileResource getExternalFileResourceByAccessToken(String accessToken);

  /**
   * Generates an accessToken before persisting the object.
   *
   * @return accessToken
   */
  String saveExternalFileResource(ExternalFileResource externalFileResource);

}
