package com.mass3d.message;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import com.mass3d.fileresource.FileResource;
import com.mass3d.user.User;

public interface MessageService {

  String META_USER_AGENT = "User-agent: ";

  Long sendTicketMessage(String subject, String text, String metaData);

  Long sendPrivateMessage(Set<User> recipients, String subject, String text, String metaData,
      Set<FileResource> attachments);

  Long sendSystemMessage(Set<User> recipients, String subject, String text);

  Long sendValidationMessage(Set<User> recipients, String subject, String text,
      MessageConversationPriority priority);

  Long sendMessage(MessageConversationParams params);

  Long sendSystemErrorNotification(String subject, Throwable t);

  void sendReply(MessageConversation conversation, String text, String metaData, boolean internal,
      Set<FileResource> attachments);

  Long saveMessageConversation(MessageConversation conversation);

  void updateMessageConversation(MessageConversation conversation);

  // Todo Eagle commented out sendCompletenessMessage(CompleteDataSetRegistration registration);
//    int sendCompletenessMessage(CompleteDataSetRegistration registration);

  MessageConversation getMessageConversation(Long id);

  MessageConversation getMessageConversation(String uid);

  long getUnreadMessageConversationCount();

  long getUnreadMessageConversationCount(User user);

  /**
   * Get all MessageConversations for the current user.
   *
   * @return a list of all message conversations for the current user.
   */
  List<MessageConversation> getMessageConversations();

  List<MessageConversation> getMessageConversations(int first, int max);

  List<MessageConversation> getMessageConversations(User user, Collection<String> uids);

  void deleteMessages(User sender);

  List<UserMessage> getLastRecipients(int first, int max);

  /**
   * Returns true if user is part of the feedback recipients group.
   *
   * @param user user to check
   * @return true if user is part of the feedback recipients group.
   */
  boolean hasAccessToManageFeedbackMessages(User user);
}
