package com.onplan.adviser.strategy;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Strategy statistics.
 */
public final class StrategyStatistics implements Serializable {
  private long lastReceivedTickTimestamp;
  private long receivedTicks;
  private long eventsDispatchedCounter;
  private long lastCompletionNanoTime;
  private long maxCompletionNanoTime;
  private long cumulatedCompletionNanoTime;

  public long getLastCompletionNanoTime() {
    return lastCompletionNanoTime;
  }

  public void setLastCompletionNanoTime(final long lastCompletionNanoTime) {
    this.lastCompletionNanoTime = lastCompletionNanoTime;
  }

  public long getEventsDispatchedCounter() {
    return eventsDispatchedCounter;
  }

  public void setEventsDispatchedCounter(final long eventsDispatchedCounter) {
    this.eventsDispatchedCounter = eventsDispatchedCounter;
  }

  public long getLastReceivedTickTimestamp() {
    return lastReceivedTickTimestamp;
  }

  public void setLastReceivedTickTimestamp(long lastReceivedTickTimestamp) {
    this.lastReceivedTickTimestamp = lastReceivedTickTimestamp;
  }

  public long getReceivedTicks() {
    return receivedTicks;
  }

  public void setReceivedTicks(long receivedTicks) {
    this.receivedTicks = receivedTicks;
  }

  public long getMaxCompletionNanoTime() {
    return maxCompletionNanoTime;
  }

  public void setMaxCompletionNanoTime(long maxCompletionNanoTime) {
    this.maxCompletionNanoTime = maxCompletionNanoTime;
  }

  public long getCumulatedCompletionNanoTime() {
    return cumulatedCompletionNanoTime;
  }

  public void setCumulatedCompletionNanoTime(long cumulatedCompletionNanoTime) {
    this.cumulatedCompletionNanoTime = cumulatedCompletionNanoTime;
  }

  public void incrementReceivedTicks() {
    this.receivedTicks++;
  }

  public void incrementEventsDispatchedCounter() {
    this.eventsDispatchedCounter++;
  }

  public void incrementCumulatedCompletionNanoTime(final long lastCompletionNanoTime) {
    this.cumulatedCompletionNanoTime += lastCompletionNanoTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyStatistics strategyStatistics = (StrategyStatistics) o;
    return Objects.equal(
            this.lastReceivedTickTimestamp, strategyStatistics.lastReceivedTickTimestamp) &&
        Objects.equal(this.receivedTicks, strategyStatistics.receivedTicks) &&
        Objects.equal(this.eventsDispatchedCounter, strategyStatistics.eventsDispatchedCounter) &&
        Objects.equal(this.lastCompletionNanoTime, strategyStatistics.lastCompletionNanoTime) &&
        Objects.equal(this.maxCompletionNanoTime, strategyStatistics.maxCompletionNanoTime) &&
        Objects.equal(
            this.cumulatedCompletionNanoTime, strategyStatistics.cumulatedCompletionNanoTime);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lastReceivedTickTimestamp, receivedTicks, eventsDispatchedCounter,
        lastCompletionNanoTime, maxCompletionNanoTime, cumulatedCompletionNanoTime);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("lastReceivedTickTimestamp", lastReceivedTickTimestamp)
        .add("receivedTicks", receivedTicks)
        .add("eventsDispatchedCounter", eventsDispatchedCounter)
        .add("lastCompletionNanoTime", lastCompletionNanoTime)
        .add("maxCompletionNanoTime", maxCompletionNanoTime)
        .add("cumulatedCompletionNanoTime", cumulatedCompletionNanoTime)
        .toString();
  }
}
