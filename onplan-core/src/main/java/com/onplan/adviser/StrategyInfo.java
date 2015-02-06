package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Map;

public class StrategyInfo extends TemplateInfo {
  private String id;
  private Map<String, String> executionParameters;
  private Collection<String> registeredInstruments;
  private StrategyStatistics strategyStatistics;

  public String getId() {
    return id;
  }

  public Map<String, String> getExecutionParameters() {
    return executionParameters;
  }

  public Collection<String> getRegisteredInstruments() {
    return registeredInstruments;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setExecutionParameters(Map<String, String> executionParameters) {
    this.executionParameters = executionParameters;
  }

  public void setRegisteredInstruments(Collection<String> registeredInstruments) {
    this.registeredInstruments = registeredInstruments;
  }

  public StrategyStatistics getStrategyStatistics() {
    return strategyStatistics;
  }

  public void setStrategyStatistics(StrategyStatistics strategyStatistics) {
    this.strategyStatistics = strategyStatistics;
  }

  public StrategyInfo() {
    // Intentionally empty.
  }

  public StrategyInfo(String displayName, String className, Iterable<String> availableParameters,
      String id, Map<String, String> executionParameters, Collection<String> registeredInstruments,
      StrategyStatistics strategyStatistics) {
    super(displayName, className, availableParameters);
    this.id = id;
    this.executionParameters = executionParameters;
    this.registeredInstruments = registeredInstruments;
    this.strategyStatistics = strategyStatistics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyInfo strategyInfo = (StrategyInfo) o;
    return Objects.equal(this.id, strategyInfo.id) &&
        Objects.equal(this.displayName, strategyInfo.displayName) &&
        Objects.equal(this.className, strategyInfo.className) &&
        Objects.equal(this.availableParameters, strategyInfo.availableParameters) &&
        Objects.equal(this.executionParameters, strategyInfo.executionParameters) &&
        Objects.equal(this.registeredInstruments, strategyInfo.registeredInstruments) &&
        Objects.equal(this.strategyStatistics, strategyInfo.strategyStatistics);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, displayName, className, availableParameters, executionParameters,
        registeredInstruments, strategyStatistics);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("displayName", displayName)
        .add("className", className)
        .add("availableParameters", availableParameters)
        .add("executionParameters", executionParameters)
        .add("registeredInstruments", registeredInstruments)
        .add("strategyStatistics", strategyStatistics)
        .toString();
  }
}
