package com.mass3d.dxf2.synch;

import java.util.Date;
import com.mass3d.dxf2.importsummary.ImportSummary;
import com.mass3d.dxf2.metadata.feedback.ImportReport;
import com.mass3d.dxf2.webmessage.WebMessageParseException;

public interface SynchronizationManager
{
    /**
     * Executes a data push to remote server.
     * 
     * @return an {@link ImportSummary}.
     */
    ImportSummary executeDataPush() throws WebMessageParseException;

//    /**
//     * Executes an event push to remote server.
//     *
//     * @return an {@link ImportSummaries}.
//     */
//    ImportSummaries executeEventPush() throws WebMessageParseException;

    /**
     * Returns the time of the last successful data sync operation.
     *
     * @return the time of the last successful data sync operation as a {@link Date}.
     */
    Date getLastDataSynchSuccess();

    /**
     * Returns the time of the last successful event sync operation.
     *
     * @return the time of the last successful event sync operation as a {@link Date}.
     */
    Date getLastEventSynchSuccess();
    
    /**
     * Executes a meta data pull operation from remote server.
     * 
     * @param url the URL to the remote server.
     * @return an {@link ImportReport}.
     */
    ImportReport executeMetadataPull(String url);
    
    /**
     * Indicates the availability status of the remote server.
     * 
     * @return the {@link AvailabilityStatus} of the remote server.
     */
    AvailabilityStatus isRemoteServerAvailable();
}
