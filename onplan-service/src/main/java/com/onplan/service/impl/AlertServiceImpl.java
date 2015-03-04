package com.onplan.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onplan.adapter.HistoricalPriceService;
import com.onplan.adapter.InstrumentService;
import com.onplan.adviser.AdviserListener;
import com.onplan.adviser.AlertInfo;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.PriceTick;
import com.onplan.domain.configuration.adviser.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.adviser.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;
import com.onplan.service.AlertService;
import com.onplan.service.EventNotificationService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adviser.predicate.AdviserPredicateUtil.createAdviserPredicateInfo;
import static com.onplan.service.impl.AdviserFactory.createAlert;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public class AlertServiceImpl implements AlertService {
  private static final Logger LOGGER = Logger.getLogger(AlertServiceImpl.class);

  private final Map<String, Collection<Alert>> alertsMapping = Maps.newHashMap();
  private final AdviserListener<AlertEvent> alertEventListener = new InternalAlertListener();

  private volatile boolean hasAlerts = false;

  @Inject
  private EventNotificationService eventNotificationService;

  @Inject
  private AlertConfigurationDao alertConfigurationDao;

  @Inject
  private InstrumentService instrumentService;

  @Inject
  private HistoricalPriceService historicalPriceService;

  @Override
  public void onPriceTick(final PriceTick priceTick) {
    checkNotNull(priceTick);
    synchronized (alertsMapping) {
      Iterable<Alert> alerts = alertsMapping.get(priceTick.getInstrumentId());
      for (Alert alert : alerts) {
        alert.onPriceTick(priceTick);
      }
    }
  }

  @Override
  public List<Alert> getAlerts() {
    ImmutableList.Builder result = ImmutableList.builder();
    synchronized (alertsMapping) {
      for (Collection<Alert> alerts : alertsMapping.values()) {
        result.addAll(alerts);
      }
    }
    return result.build();
  }

  @Override
  public boolean hasAlerts() {
    return hasAlerts;
  }

  @Override
  public void removeAlert(String alertId) throws Exception {
    synchronized (alertsMapping) {
      hasAlerts = !alertsMapping.isEmpty();
      throw new IllegalArgumentException("Not yet implemented.");
    }
  }

  @Override
  public void addAlert(AlertConfiguration alertConfiguration) throws Exception {
    checkArgument(null == alertConfiguration.getId());
    checkAlertConfiguration(alertConfiguration);
    try {
      loadAlert(alertConfiguration);
      hasAlerts = !alertsMapping.isEmpty();
    } catch (Exception e) {
      LOGGER.error(String.format(
          "Error while loading alert configuration [%s]: [%s].",
          alertConfiguration,
          e.getMessage()));
      throw e;
    }
    String id = alertConfigurationDao.insert(alertConfiguration);
    LOGGER.info(String.format("Alert [%s] saved in database.", id));
  }

  @Override
  public List<AlertInfo> getAlertsInfo() {
    ImmutableList.Builder result = ImmutableList.builder();
    synchronized (alertsMapping) {
      for (Collection<Alert> alerts : alertsMapping.values()) {
        for (Alert alert : alerts) {
          result.add(createAlertInfo(alert));
        }
      }
    }
    return result.build();
  }

  @Override
  public void loadSampleAlerts() throws Exception {
    List<AlertConfiguration> oldAlerts = alertConfigurationDao.findAll();
    List<AlertConfiguration> sampleAlerts = alertConfigurationDao.getSampleAlertsConfiguration();
    LOGGER.info(String.format(
        "Replacing [%d] old alerts with [%d] sample alerts.",
        oldAlerts.size(),
        sampleAlerts.size()));
    alertConfigurationDao.removeAll();
    alertConfigurationDao.insertAll(sampleAlerts);
    try {
      loadAllAlerts();
    } catch (Exception e) {
      LOGGER.error(String.format("Error while loading alerts: [%s].", e.getMessage()));
      alertConfigurationDao.removeAll();
      alertConfigurationDao.insertAll(oldAlerts);
      throw e;
    }
  }

  private static void checkAlertConfiguration(final AlertConfiguration alertConfiguration) {
    checkNotNull(alertConfiguration);
    checkNotNullOrEmpty(alertConfiguration.getAlertMessage());
    checkNotNull(alertConfiguration.getInstrumentId());
    checkNotNull(alertConfiguration.getPredicatesChain());
    checkArgument(!Iterables.isEmpty(alertConfiguration.getPredicatesChain()));
    for (AdviserPredicateConfiguration predicateConfiguration
        : alertConfiguration.getPredicatesChain()) {
      checkNotNullOrEmpty(predicateConfiguration.getClassName());
      checkNotNull(predicateConfiguration.getParameters());
    }
  }

  private void loadAlert(AlertConfiguration alertConfiguration) throws Exception {
    checkAlertConfiguration(alertConfiguration);
    Alert alert = createAlert(
        alertConfiguration, alertEventListener, instrumentService, historicalPriceService);
    Collection<Alert> alertsEntry = alertsMapping.get(alert.getInstrumentId());
    if (null == alertsEntry) {
      alertsEntry = Lists.newArrayList();
      alertsMapping.put(alert.getInstrumentId(), alertsEntry);
    }
    alertsEntry.add(alert);
    LOGGER.info(String.format("Alert [%s]: [%s] loaded.", alert.getId(), alert.getMessage()));
    // TODO(robertom): Update PriceService subscribed instruments by using a listener.
  }

  private void loadAllAlerts() throws Exception {
    LOGGER.info("Loading alerts configuration from database.");
    List<AlertConfiguration> alertConfigurations = alertConfigurationDao.findAll();
    LOGGER.info(String.format("%s alerts found in database.", alertConfigurations.size()));
    for (AlertConfiguration alertConfiguration : alertConfigurations) {
      loadAlert(alertConfiguration);
    }
    LOGGER.info(String.format(
        "[%d] strategies loaded for instruments [%s].",
        alertConfigurations.size(),
        Joiner.on(", ").join(alertsMapping.keySet())));
  }

  private AlertInfo createAlertInfo(final Alert alert) {
    checkNotNull(alert);
    ImmutableList.Builder predicatesInfo = ImmutableList.builder();
    for (AdviserPredicate adviserPredicate : alert.getPredicatesChain()) {
      predicatesInfo.add(createAdviserPredicateInfo(adviserPredicate));
    }
    return new AlertInfo(
        alert.getId(),
        predicatesInfo.build(),
        alert.getMessage(),
        alert.getCreatedOn(),
        alert.getLastFiredOn(),
        alert.getRepeat());
  }

  private final class InternalAlertListener implements AdviserListener<AlertEvent> {
    @Override
    public void onEvent(AlertEvent event) {
      eventNotificationService.notifyAlertEventAsync(event);
    }
  }
}
