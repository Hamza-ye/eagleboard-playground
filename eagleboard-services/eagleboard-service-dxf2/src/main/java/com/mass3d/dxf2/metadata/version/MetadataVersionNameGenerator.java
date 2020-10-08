package com.mass3d.dxf2.metadata.version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.dxf2.metadata.version.exception.MetadataVersionServiceException;
import com.mass3d.metadata.version.MetadataVersion;
import com.mass3d.metadata.version.MetadataVersionService;

/**
 * Generates the sequential Metadata Version name.
 *
 */
public class MetadataVersionNameGenerator
{
    private static final Log log = LogFactory.getLog( MetadataVersionNameGenerator.class );

    public static String getNextVersionName( MetadataVersion version )
    {
        if ( version == null )
        {
            return MetadataVersionService.METADATAVERSION_NAME_PREFIX + 1;
        }
        else
        {
            String[] versionNameSplit = version.getName().split( "_" );
            String versionNameSuffix = versionNameSplit[1];
            int n;
            try
            {
                n = Integer.parseInt( versionNameSuffix );
            }
            catch ( NumberFormatException nfe )
            {
                String message = "Invalid version name found: " + versionNameSuffix;
                log.error( message, nfe );

                throw new MetadataVersionServiceException( message, nfe );
            }

            return MetadataVersionService.METADATAVERSION_NAME_PREFIX + (n + 1);
        }
    }
}
