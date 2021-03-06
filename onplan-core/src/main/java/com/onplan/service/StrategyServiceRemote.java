package com.onplan.service;

import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.domain.configuration.StrategyConfiguration;

import java.io.Serializable;
import java.util.List;

public interface StrategyServiceRemote extends Serializable {
  /**
   * Removes an instantiated strategy by its id.
   *
   * @param strategyId the strategy id.
   */
  public boolean removeStrategy(String strategyId) throws Exception;

  /**
   * Registers and initializes a strategy with the provided configuration. Returns the id of the
   * registered configuration. If the strategy id is already assigned it replaces the previous
   * strategy values with the new data.
   *
   * @param strategyConfiguration the strategy configuration.
   */
  public String addStrategy(StrategyConfiguration strategyConfiguration) throws Exception;

  /**
   * Loads a collection of sample strategies, deleting and replacing all the ones which have been
   * instantiated.
   *
   * @throws Exception error while loading the collection of sample strategies.
   */
  public void loadSampleStrategies() throws Exception;

  /**
   * Returns the collection of strategies info which have been instantiated.
   */
  public List<StrategyInfo> getStrategiesInfo();

  /**
   * Returns the collection of available strategy templates info.
   */
  public List<TemplateInfo> getStrategiesTemplateInfo();

  /**
   * Returns the strategy template info by the its class displayName or null if the template was
   * not found.
   * @param className The class displayName.
   */
  public TemplateInfo getStrategyTemplateInfo(String className);
}
