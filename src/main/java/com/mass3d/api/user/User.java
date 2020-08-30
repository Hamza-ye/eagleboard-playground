package com.mass3d.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mass3d.api.common.BaseIdentifiableObject;
import com.mass3d.api.common.MetadataObject;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.StringUtils;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "userinfoid"))
public class User
    extends BaseIdentifiableObject implements MetadataObject {

  /**
   * Required.
   */
  private String surname;

  private String firstName;

  /**
   * Optional.
   */
  private String email;

  private String phoneNumber;

  private String jobTitle;

  private String introduction;

  private String gender;

  private Date birthday;

  private String nationality;

  private String employer;

  private String education;

  private String interests;

  private String languages;

  private String welcomeMessage;

  private Date lastCheckedInterpretations;

  @OneToOne
  @PrimaryKeyJoinColumn
  private UserCredentials userCredentials;

  @ManyToMany
  @JoinTable(name = "usergroupmembers",
      joinColumns = @JoinColumn(name = "userid", referencedColumnName = "userid"),
      inverseJoinColumns = @JoinColumn(name = "usergroupid", referencedColumnName = "usergroupid")
  )
  private Set<UserGroup> groups = new HashSet<>();

  private String whatsApp;

  private String facebookMessenger;

  private String skype;

  private String telegram;

  private String twitter;

//  private FileResource avatar;

//    /**
//     * Organisation units for data input and data capture / write operations.
//     * TODO move to UserCredentials.
//     */
//    private Set<OrganisationUnit> organisationUnits = new HashSet<>();
//
//    /**
//     * Organisation units for data output and data analysis / read operations.
//     */
//    private Set<OrganisationUnit> dataViewOrganisationUnits = new HashSet<>();
//
//    /**
//     * Organisation units for tracked entity instance search operations.
//     */
//    private Set<OrganisationUnit> teiSearchOrganisationUnits = new HashSet<>();

//  /**
//   * Ordered favorite apps.
//   */
//  private List<String> apps = new ArrayList<>();

  // -------------------------------------------------------------------------
  // Logic
  // -------------------------------------------------------------------------

//    public void addOrganisationUnit( OrganisationUnit unit )
//    {
//        organisationUnits.add( unit );
//        unit.getUsers().add( this );
//    }
//
//    public void removeOrganisationUnit( OrganisationUnit unit )
//    {
//        organisationUnits.remove( unit );
//        unit.getUsers().remove( this );
//    }
//
//    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
//    {
//        for ( OrganisationUnit unit : new HashSet<>( organisationUnits ) )
//        {
//            if ( !updates.contains( unit ) )
//            {
//                removeOrganisationUnit( unit );
//            }
//        }
//
//        for ( OrganisationUnit unit : updates )
//        {
//            addOrganisationUnit( unit );
//        }
//    }

  public static String getSafeUsername(String username) {
    return StringUtils.isEmpty(username) ? "[Unknown]" : username;
  }

  /**
   * Returns the concatenated first name and surname.
   */
  @Override
  public String getName() {
    return firstName + " " + surname;
  }

  /**
   * Checks whether the profile has been filled, which is defined as three not-null properties out
   * of all optional properties.
   */
  public boolean isProfileFilled() {
    Object[] props = {jobTitle, introduction, gender, birthday,
        nationality, employer, education, interests, languages};

    int count = 0;

    for (Object prop : props) {
      count = prop != null ? (count + 1) : count;
    }

    return count > 3;
  }

  public String getUsername() {
    return userCredentials != null ? userCredentials.getUsername() : null;
  }

  public boolean isSuper() {
    return userCredentials != null && userCredentials.isSuper();
  }

  /**
   * Tests whether the user has the given authority. Returns true in any case if the user has the
   * ALL authority.
   *
   * @param auth the authority.
   */
  public boolean isAuthorized(String auth) {
    return userCredentials != null && userCredentials.isAuthorized(auth);
  }

//  /**
//   * Indicates whether this user can manage the given user.
//   *
//   * @param user the user to test.
//   * @return true if the given user can be managed by this user, false if not.
//   */
//  public boolean canManage(User user) {
//    if (user == null || user.getGroups() == null) {
//      return false;
//    }

//    for (UserGroup group : user.getGroups()) {
//      if (canManage(group)) {
//        return true;
//      }
//    }

//    return false;
//  }

//  /**
//   * Indicates whether this user is managed by the given user group.
//   *
//   * @param userGroup the user group to test.
//   * @return true if the given user group is managed by this user, false if not.
//   */
//  public boolean isManagedBy(UserGroup userGroup) {
//    return userGroup != null && CollectionUtils.containsAny(groups, userGroup.getManagedGroups());
//  }
//
//  /**
//   * Indicates whether this user is managed by the given user.
//   *
//   * @param user the user  to test.
//   * @return true if the given user is managed by this user, false if not.
//   */
//  public boolean isManagedBy(User user) {
//    if (user == null || user.getGroups() == null) {
//      return false;
//    }
//
//    for (UserGroup group : user.getGroups()) {
//      if (isManagedBy(group)) {
//        return true;
//      }
//    }
//
//    return false;
//  }

  public boolean hasEmail() {
    return email != null && !email.isEmpty();
  }

  // -------------------------------------------------------------------------
  // Getters and setters
  // -------------------------------------------------------------------------

  @JsonProperty
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonProperty
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @JsonProperty
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @JsonProperty
  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  @JsonProperty
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @JsonProperty
  public String getIntroduction() {
    return introduction;
  }

  public void setIntroduction(String introduction) {
    this.introduction = introduction;
  }

  @JsonProperty
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  @JsonProperty
  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  @JsonProperty
  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  @JsonProperty
  public String getEmployer() {
    return employer;
  }

  public void setEmployer(String employer) {
    this.employer = employer;
  }

  @JsonProperty
  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  @JsonProperty
  public String getInterests() {
    return interests;
  }

  public void setInterests(String interests) {
    this.interests = interests;
  }

  @JsonProperty
  public String getLanguages() {
    return languages;
  }

  public void setLanguages(String languages) {
    this.languages = languages;
  }

  @JsonProperty
  public String getWelcomeMessage() {
    return welcomeMessage;
  }

  public void setWelcomeMessage(String welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
  }

  @JsonProperty
  public Date getLastCheckedInterpretations() {
    return lastCheckedInterpretations;
  }

  public void setLastCheckedInterpretations(Date lastCheckedInterpretations) {
    this.lastCheckedInterpretations = lastCheckedInterpretations;
  }

  @JsonProperty
  public UserCredentials getUserCredentials() {
    return userCredentials;
  }

  public void setUserCredentials(UserCredentials userCredentials) {
    this.userCredentials = userCredentials;
  }

  @JsonProperty("userGroups")
  @JsonSerialize(contentAs = BaseIdentifiableObject.class)
  public Set<UserGroup> getGroups() {
    return groups;
  }

  public void setGroups(Set<UserGroup> groups) {
    this.groups = groups;
  }

//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<OrganisationUnit> getTodoTasks()
//    {
//        return organisationUnits;
//    }
//
//    public User setTodoTasks( Set<OrganisationUnit> organisationUnits )
//    {
//        this.organisationUnits = organisationUnits;
//        return this;
//    }
//
//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "dataViewOrganisationUnits", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "dataViewOrganisationUnit", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<OrganisationUnit> getDataViewOrganisationUnits()
//    {
//        return dataViewOrganisationUnits;
//    }
//
//    public void setDataViewOrganisationUnits( Set<OrganisationUnit> dataViewOrganisationUnits )
//    {
//        this.dataViewOrganisationUnits = dataViewOrganisationUnits;
//    }
//
//    @JsonProperty
//    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
//    @JacksonXmlElementWrapper( localName = "teiSearchOrganisationUnits", namespace = DxfNamespaces.DXF_2_0 )
//    @JacksonXmlProperty( localName = "teiSearchOrganisationUnit", namespace = DxfNamespaces.DXF_2_0 )
//    public Set<OrganisationUnit> getTeiSearchOrganisationUnits()
//    {
//        return teiSearchOrganisationUnits;
//    }
//
//    public void setTeiSearchOrganisationUnits( Set<OrganisationUnit> teiSearchOrganisationUnits )
//    {
//        this.teiSearchOrganisationUnits = teiSearchOrganisationUnits;
//    }

//  public List<String> getApps() {
//    return apps;
//  }
//
//  public void setApps(List<String> apps) {
//    this.apps = apps;
//  }

  @JsonProperty
  public String getWhatsApp() {
    return whatsApp;
  }

  public void setWhatsApp(String whatsapp) {
    this.whatsApp = whatsapp;
  }

  @JsonProperty
  public String getFacebookMessenger() {
    return facebookMessenger;
  }

  public void setFacebookMessenger(String facebookMessenger) {
    this.facebookMessenger = facebookMessenger;
  }


  @JsonProperty
  public String getSkype() {
    return skype;
  }

  public void setSkype(String skype) {
    this.skype = skype;
  }


  @JsonProperty
  public String getTelegram() {
    return telegram;
  }

  public void setTelegram(String telegram) {
    this.telegram = telegram;
  }


  @JsonProperty
  public String getTwitter() {
    return twitter;
  }

  public void setTwitter(String twitter) {
    this.twitter = twitter;
  }

//  @JsonProperty
//  @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
//  public FileResource getAvatar() {
//    return avatar;
//  }
//
//  public void setAvatar(FileResource avatar) {
//    this.avatar = avatar;
//  }

  @Override
  public String toString() {
    return "{" +
        "\"id\":\"" + id + "\", " +
        "\"uid\":\"" + uid + "\", " +
        "\"code\":\"" + code + "\", " +
        "\"created\":\"" + created + "\", " +
        "\"lastUpdated\":\"" + lastUpdated + "\", " +
        "\"surname\":\"" + surname + "\", " +
        "\"firstName\":\"" + firstName + "\", " +
        "\"email\":\"" + email + "\", " +
        "\"phoneNumber\":\"" + phoneNumber + "\", " +
        "\"jobTitle\":\"" + jobTitle + "\", " +
        "\"introduction\":\"" + introduction + "\", " +
        "\"gender\":\"" + gender + "\", " +
        "\"birthday\":\"" + birthday + "\", " +
        "\"nationality\":\"" + nationality + "\", " +
        "\"employer\":\"" + employer + "\", " +
        "\"education\":\"" + education + "\", " +
        "\"interests\":\"" + interests + "\", " +
        "\"languages\":\"" + languages + "\", " +
        "\"lastCheckedInterpretations\":\"" + lastCheckedInterpretations + "\", " +
        "\"userCredentials\":\"" + userCredentials + "\", " +
        "}";
  }
}
