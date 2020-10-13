package com.mass3d.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mass3d.common.GenericStore;
import com.mass3d.user.User;
import com.mass3d.user.UserGroup;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("com.mass3d.configuration.ConfigurationService")
//@Transactional
public class DefaultConfigurationService
    implements ConfigurationService {

  private GenericStore<Configuration> configurationStore;

  public DefaultConfigurationService(
      @Qualifier("com.mass3d.configuration.ConfigurationStore") GenericStore<Configuration> configurationStore) {
    checkNotNull(configurationStore);

    this.configurationStore = configurationStore;
  }

  // -------------------------------------------------------------------------
  // ConfigurationService implementation
  // -------------------------------------------------------------------------

  @Override
  @Transactional(readOnly = true)
  public Configuration getConfiguration() {
    Iterator<Configuration> iterator = configurationStore.getAll().iterator();

    return iterator.hasNext() ? iterator.next() : new Configuration();
  }

  @Override
  @Transactional
  public void setConfiguration(Configuration configuration) {
    if (configuration != null && configuration.getId() > 0) {
      configurationStore.update(configuration);
    } else {
      configurationStore.save(configuration);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isCorsWhitelisted(String origin) {
    return getConfiguration().getCorsWhitelist().contains(origin);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUserInFeedbackRecipientUserGroup(User user) {
    UserGroup feedbackRecipients = getConfiguration().getFeedbackRecipients();

    return feedbackRecipients != null && feedbackRecipients.getMembers().contains(user);
  }
}
