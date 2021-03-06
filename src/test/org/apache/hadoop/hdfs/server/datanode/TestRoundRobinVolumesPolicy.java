/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode;

import java.io.IOException;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.hadoop.hdfs.server.datanode.FSDataset.FSVolume;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Test;
import org.mockito.Mockito;

public class TestRoundRobinVolumesPolicy extends TestCase {

  // Test the Round-Robin block-volume choosing algorithm.
  @Test
  public void testRR() throws Exception {
    FSVolume[] volumes = new FSVolume[2];

    // First volume, with 100 bytes of space.
    volumes[0] = Mockito.mock(FSVolume.class);
    Mockito.when(volumes[0].getAvailable()).thenReturn(100L);

    // Second volume, with 200 bytes of space.
    volumes[1] = Mockito.mock(FSVolume.class);
    Mockito.when(volumes[1].getAvailable()).thenReturn(200L);

    RoundRobinVolumesPolicy policy = ReflectionUtils.newInstance(
        RoundRobinVolumesPolicy.class, null);
    
    // Test two rounds of round-robin choosing
    Assert.assertEquals(volumes[0], policy.chooseVolume(volumes, 0));
    Assert.assertEquals(volumes[1], policy.chooseVolume(volumes, 0));
    Assert.assertEquals(volumes[0], policy.chooseVolume(volumes, 0));
    Assert.assertEquals(volumes[1], policy.chooseVolume(volumes, 0));

    // The first volume has only 100L space, so the policy should
    // wisely choose the second one in case we ask for more.
    Assert.assertEquals(volumes[1], policy.chooseVolume(volumes, 150));

    // Fail if no volume can be chosen?
    try {
      policy.chooseVolume(volumes, Long.MAX_VALUE);
      Assert.fail();
    } catch (IOException e) {
      // Passed.
    }
  }

}