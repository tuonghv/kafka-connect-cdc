/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.cdc;

import com.google.common.base.Strings;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Assertions {
  public static void assertSchema(final Schema expected, final Schema actual) {
    assertSchema(expected, actual, null);
  }

  public static void assertField(final Field expected, final Field actual, String message) {
    String prefix = Strings.isNullOrEmpty(message) ? "" : message + ": ";
    assertNotNull(expected, prefix + "expected field should not be null.");
    assertNotNull(actual, prefix + "actual field should not be null.");
    assertSchema(expected.schema(), actual.schema(), "schema for field " + expected.name() + " does not match.");
  }

  public static void assertSchema(final Schema expected, final Schema actual, String message) {
    String prefix = Strings.isNullOrEmpty(message) ? "" : message + ": ";
    assertNotNull(expected, prefix + "expected schema should not be null.");
    assertNotNull(actual, prefix + "actual schema should not be null.");
    assertEquals(expected.name(), actual.name(), prefix + "schema.name() should match.");
    assertEquals(expected.type(), actual.type(), prefix + "schema.type() should match.");
    assertEquals(expected.defaultValue(), actual.defaultValue(), prefix + "schema.defaultValue() should match.");
    assertEquals(expected.isOptional(), actual.isOptional(), prefix + "schema.isOptional() should match.");
    assertEquals(expected.doc(), actual.doc(), prefix + "schema.doc() should match.");
    assertEquals(expected.version(), actual.version(), prefix + "schema.version() should match.");
//    assertMap(expected.parameters(), actual.parameters(), prefix + "schema.parameters() should match.");
    assertEquals(expected.parameters(), actual.parameters(), prefix + "schema.parameters() should match.");
    switch (expected.type()) {
      case ARRAY:
        assertSchema(expected.valueSchema(), actual.valueSchema(), message + "valueSchema does not match.");
        break;
      case MAP:
        assertSchema(expected.keySchema(), actual.keySchema(), message + "keySchema does not match.");
        assertSchema(expected.valueSchema(), actual.valueSchema(), message + "valueSchema does not match.");
        break;
      case STRUCT:
        assertEquals(expected.fields().size(), actual.fields().size(), message + "fields().size() does not match.");
        for (Field expectedField : expected.fields()) {
          Field actualField = actual.field(expectedField.name());
          assertField(expectedField, actualField, "field(" + expectedField.name() + ") does not match.");
        }
    }
  }

}
