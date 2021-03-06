/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.cdc;

import com.google.common.io.BaseEncoding;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

class ValueHelper {
  static final Logger log = LoggerFactory.getLogger(ValueHelper.class);

  static Object int8(Object o) {
    if (o instanceof Long) {
      Number number = (Number) o;
      return number.byteValue();
    }
    return o;
  }

  static Object int16(Object o) {
    if (o instanceof Long) {
      Number number = (Number) o;
      return number.shortValue();
    }
    return o;
  }

  static Object int32(Object o) {
    if (o instanceof Long) {
      Number number = (Number) o;
      return number.intValue();
    }

    return o;
  }

  static Object int64(Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return number.longValue();
    }
    return value;
  }

  static Object float64(Object o) {
    if (o instanceof Number) {
      Number integer = (Number) o;
      return integer.doubleValue();
    }

    return o;
  }

  static Object float32(Object o) {
    if (o instanceof Number) {
      Number integer = (Number) o;
      return integer.floatValue();
    }

    return o;
  }

  static Object bytes(Object value) {
    if (value instanceof String) {
      String s = (String) value;
      return BaseEncoding.base64().decode(s);
    }
    return value;
  }

  static Object decimal(Schema schema, Object value) {
    if (value instanceof byte[]) {
      byte[] bytes = (byte[]) value;
      return Decimal.toLogical(schema, bytes);
    }
    if (value instanceof BigDecimal) {
      BigDecimal decimal = (BigDecimal) value;
      final int scale = Integer.parseInt(schema.parameters().get(Decimal.SCALE_FIELD));
      if (scale == decimal.scale()) {
        return decimal;
      } else {
        return decimal.setScale(scale);
      }
    }
    if (value instanceof Number) {
      Number number = (Number) value;
      int scale = Integer.parseInt(schema.parameters().get(Decimal.SCALE_FIELD));
      BigDecimal decimal = BigDecimal.valueOf(number.longValue(), scale);
      return decimal;
    }

    return value;
  }

  static Object date(Schema schema, Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return Date.toLogical(schema, number.intValue());
    }
    return value;
  }

  static Object time(Schema schema, Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return Time.toLogical(schema, number.intValue());
    }
    return value;
  }

  static Object timestamp(Schema schema, Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return Timestamp.toLogical(schema, number.longValue());
    }
    return value;
  }

  public static Object value(Schema schema, Object value) {
    if (null == value) {
      return null;
    }

    Object result;

    switch (schema.type()) {
      case BYTES:
        if (Decimal.LOGICAL_NAME.equals(schema.name())) {
          result = decimal(schema, value);
        } else {
          result = bytes(value);
        }
        break;
      case INT32:
        if (Date.LOGICAL_NAME.equals(schema.name())) {
          result = date(schema, value);
        } else if (Time.LOGICAL_NAME.equals(schema.name())) {
          result = time(schema, value);
        } else {
          result = int32(value);
        }
        break;
      case INT16:
        result = int16(value);
        break;
      case INT64:
        if (Timestamp.LOGICAL_NAME.equals(schema.name())) {
          result = timestamp(schema, value);
        } else {
          result = int64(value);
        }
        break;
      case INT8:
        result = int8(value);
        break;
      case FLOAT32:
        result = float32(value);
        break;
      case FLOAT64:
        result = float64(value);
        break;
      case STRUCT:
        log.trace("struct");
      default:
        result = value;
        break;
    }

    return result;
  }

  public static JsonColumnValue convert(Change.ColumnValue columnValue) {
    JsonColumnValue jsonColumnValue = new JsonColumnValue();
    jsonColumnValue.columnName = columnValue.columnName();
    jsonColumnValue.schema = columnValue.schema();
    jsonColumnValue.value(columnValue.value());
    return jsonColumnValue;
  }

  public Object convert(Object value) {
    Object result;
    if (value instanceof java.sql.Date) {
      java.sql.Date d = (java.sql.Date) value;
      result = new java.util.Date(d.getTime());
    } else {
      result = value;
    }
    return result;
  }
}
