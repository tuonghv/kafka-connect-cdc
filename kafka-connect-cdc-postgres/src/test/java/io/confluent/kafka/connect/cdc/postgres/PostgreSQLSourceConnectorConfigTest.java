package io.confluent.kafka.connect.cdc.postgres;

import io.confluent.kafka.connect.utils.config.MarkdownFormatter;
import org.junit.Test;

public class PostgreSQLSourceConnectorConfigTest {

  @Test
  public void doc() {
    System.out.println(MarkdownFormatter.toMarkdown(PostgreSQLSourceConnectorConfig.config()));
  }

}