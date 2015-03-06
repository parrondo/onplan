package com.onplan.adviser.alert;

import com.onplan.adviser.AbstractAdviserEvent;
import com.onplan.domain.PriceTick;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class AlertEvent extends AbstractAdviserEvent {
  private final String message;
  private final SeverityLevel severityLevel;

  public AlertEvent(
      SeverityLevel severityLevel, PriceTick priceTick, long createdOn, String message) {
    super(priceTick, createdOn);
    this.message = checkNotNullOrEmpty(message);
    this.severityLevel = checkNotNull(severityLevel);
  }

  public String getMessage() {
    return message;
  }

  public SeverityLevel getSeverityLevel() {
    return severityLevel;
  }
}
