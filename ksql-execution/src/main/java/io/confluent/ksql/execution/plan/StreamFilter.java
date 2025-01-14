/*
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.ksql.execution.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.errorprone.annotations.Immutable;
import io.confluent.ksql.execution.expression.tree.Expression;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Immutable
public class StreamFilter<K> implements ExecutionStep<KStreamHolder<K>> {

  private final ExecutionStepProperties properties;
  private final ExecutionStep<KStreamHolder<K>> source;
  private final Expression filterExpression;

  public StreamFilter(
      @JsonProperty(value = "properties", required = true) ExecutionStepProperties properties,
      @JsonProperty(value = "source", required = true) ExecutionStep<KStreamHolder<K>> source,
      @JsonProperty(value = "filterExpression", required = true)
      Expression filterExpression) {
    this.properties = Objects.requireNonNull(properties, "properties");
    this.source = Objects.requireNonNull(source, "source");
    this.filterExpression = Objects.requireNonNull(filterExpression, "filterExpression");
  }

  @Override
  public ExecutionStepProperties getProperties() {
    return properties;
  }

  @Override
  @JsonIgnore
  public List<ExecutionStep<?>> getSources() {
    return Collections.singletonList(source);
  }

  public Expression getFilterExpression() {
    return filterExpression;
  }

  public ExecutionStep<KStreamHolder<K>> getSource() {
    return source;
  }

  @Override
  public KStreamHolder<K> build(PlanBuilder builder) {
    return builder.visitStreamFilter(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StreamFilter<?> that = (StreamFilter<?>) o;
    return Objects.equals(properties, that.properties)
        && Objects.equals(source, that.source)
        && Objects.equals(filterExpression, that.filterExpression);
  }

  @Override
  public int hashCode() {

    return Objects.hash(properties, source, filterExpression);
  }
}
