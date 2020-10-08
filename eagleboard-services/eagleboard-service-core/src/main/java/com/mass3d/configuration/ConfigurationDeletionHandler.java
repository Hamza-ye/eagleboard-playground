package com.mass3d.configuration;

import org.apache.commons.lang.StringUtils;
import com.mass3d.indicator.IndicatorGroup;
import com.mass3d.system.deletion.DeletionHandler;
import com.mass3d.user.UserAuthorityGroup;
import com.mass3d.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component( "com.mass3d.configuration.ConfigurationDeletionHandler" )
public class ConfigurationDeletionHandler
    extends DeletionHandler {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  @Autowired
  private ConfigurationService configService;

  // -------------------------------------------------------------------------
  // DeletionHandler implementation
  // -------------------------------------------------------------------------

  @Override
  protected String getClassName() {
    return Configuration.class.getSimpleName();
  }

  @Override
  public String allowDeleteUserGroup(UserGroup userGroup) {
    UserGroup feedbackRecipients = configService.getConfiguration().getFeedbackRecipients();

    return (feedbackRecipients != null && feedbackRecipients.equals(userGroup)) ? StringUtils.EMPTY
        : null;
  }

//    @Override
//    public String allowDeleteDataElementGroup( DataElementGroup dataElementGroup )
//    {
//        DataElementGroup infrastructuralDataElements = configService.getConfiguration().getInfrastructuralDataElements();
//
//        return ( infrastructuralDataElements != null && infrastructuralDataElements.equals( dataElementGroup ) ) ? StringUtils.EMPTY : null;
//    }

  @Override
  public String allowDeleteIndicatorGroup(IndicatorGroup indicatorGroup) {
    IndicatorGroup infrastructuralIndicators = configService.getConfiguration()
        .getInfrastructuralIndicators();

    return (infrastructuralIndicators != null && infrastructuralIndicators.equals(indicatorGroup))
        ? StringUtils.EMPTY : null;
  }

//    @Override
//    public String allowDeleteOrganisationUnitLevel( OrganisationUnitLevel level )
//    {
//        OrganisationUnitLevel offlineLevel = configService.getConfiguration().getOfflineOrganisationUnitLevel();
//
//        return ( offlineLevel != null && offlineLevel.equals( level ) ) ? StringUtils.EMPTY : null;
//    }
//
//    @Override
//    public String allowDeleteOrganisationUnit( OrganisationUnit organisationUnit )
//    {
//        OrganisationUnit selfRegOrgUnit = configService.getConfiguration().getSelfRegistrationOrgUnit();
//
//        return ( selfRegOrgUnit != null && selfRegOrgUnit.equals( organisationUnit ) ) ? StringUtils.EMPTY : null;
//    }

  @Override
  public String allowDeleteUserAuthorityGroup(UserAuthorityGroup userAuthorityGroup) {
    UserAuthorityGroup selfRegRole = configService.getConfiguration().getSelfRegistrationRole();

    return (selfRegRole != null && selfRegRole.equals(userAuthorityGroup)) ? StringUtils.EMPTY
        : null;
  }
}
