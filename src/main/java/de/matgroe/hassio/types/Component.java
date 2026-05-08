/*
 * MIT License
 *
 * Copyright (c) 2026 Matthias Gröger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.matgroe.hassio.types;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Component {
  @SerializedName("platform")
  protected String platform;

  @SerializedName("name")
  protected String name;

  @SerializedName("device")
  protected Device device;

  @SerializedName("entity_category")
  protected String entityCategory;

  @SerializedName("device_class")
  protected String deviceClass;

  @SerializedName("state_topic")
  protected String stateTopic;

  @SerializedName("command_topic")
  protected String commandTopic;

  @SerializedName("unique_id")
  protected String uniqueId;

  @SerializedName("qos")
  protected int qos;

  @SerializedName("retain")
  protected boolean retain;

  @SerializedName("expire_after")
  protected Integer expiresAfter;

  public Component() {
    this.qos = 0;
    this.retain = false;
  }
}
