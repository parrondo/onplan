package com.onplan.notification;

import com.google.common.base.Strings;
import com.onplan.strategy.StrategyEvent;
import com.onplan.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Component
public class TwitterNotificationChannel implements NotificationChannel {
  private static final Logger logger = Logger.getLogger(TwitterNotificationChannel.class);
  private static final int MESSAGE_MAX_LENGTH = 140;
  private static final String MESSAGE_TRIMMED_SUFFIX = "..";

  private final Twitter twitterClient = TwitterFactory.getSingleton();

  @Value(value = "${twitter.destination.recipientScreenName}")
  private String destinationRecipentScreenName;

  @Override
  public void notifyStrategyEvent(StrategyEvent strategyEvent) throws Exception {
    checkNotNullOrEmpty(destinationRecipentScreenName);
    logger.info(String.format(
        "Sending Twitter notification to recipient [%s]: [%s]",
        destinationRecipentScreenName,
        strategyEvent.getMessage()));
    sendDirectMessage(destinationRecipentScreenName, strategyEvent.getMessage());
  }

  @Override
  public void notifyMessage(String title, String body) throws Exception {
    checkNotNullOrEmpty(destinationRecipentScreenName);
    String message = String.format("[%s] %s", title, body);
    logger.info(String.format(
        "Sending Twitter notification to recipient [%s]: [%s]",
        destinationRecipentScreenName,
        message));
    sendDirectMessage(destinationRecipentScreenName, message);
  }

  private void sendDirectMessage(String destinationRecipentScreenName, String message)
      throws Exception {
    try {
      twitterClient.sendDirectMessage(
          destinationRecipentScreenName,
          StringUtil.trimMessage(message, MESSAGE_MAX_LENGTH, MESSAGE_TRIMMED_SUFFIX));
    } catch (TwitterException e) {
      logger.error(String.format(
          "Error while sending Twitter notification to recipient [%s]: [%s].",
          destinationRecipentScreenName,
          e.getMessage()));
      throw new Exception(e);
    }
  }

  @Override
  public boolean isActive() {
    try {
      return ! Strings.isNullOrEmpty(twitterClient.getAccountSettings().getScreenName());
    } catch (TwitterException e) {
      return false;
    }
  }
}
