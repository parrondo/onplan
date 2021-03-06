package com.onplan.service;

import com.onplan.adviser.alert.Alert;
import com.onplan.domain.transitory.PriceTick;

import java.util.List;

public interface AlertService extends AlertServiceRemote {
  public void onPriceTick(final PriceTick priceTick);
  public void setInstrumentSubscriptionListener(
      InstrumentSubscriptionListener instrumentSubscriptionListener);
  public List<Alert> getAlerts();
  public boolean hasAlerts();
  public void loadAllAlerts() throws Exception;
  public void unLoadAllAlerts() throws Exception;
}
