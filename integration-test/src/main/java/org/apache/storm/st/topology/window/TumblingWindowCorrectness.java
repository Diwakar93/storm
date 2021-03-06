/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.storm.st.topology.window;

import com.google.common.collect.Lists;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.st.topology.TestableTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.st.utils.StringDecorator;

import java.util.List;

/**
 * Computes sliding window sum
 */
public class TumblingWindowCorrectness implements TestableTopology {
    private static final Logger LOG = LoggerFactory.getLogger(TumblingWindowCorrectness.class);
    private static final String NUMBER_FIELD = "number";
    private static final String STRING_FIELD = "numAsStr";
    private final int tumbleSize;
    private final String spoutName;
    private final int spoutExecutors = 1;
    private final String boltName;
    private final int boltExecutors = 1;

    public TumblingWindowCorrectness(final int tumbleSize) {
        this.tumbleSize = tumbleSize;
        final String prefix = this.getClass().getSimpleName() + "-tumbleSize" + tumbleSize;
        spoutName = prefix + "IncrementingSpout";
        boltName = prefix + "VerificationBolt";
    }

    @Override
    public String getBoltName() {
        return boltName;
    }

    @Override
    public String getSpoutName() {
        return spoutName;
    }
    
    @Override
    public int getBoltExecutors() {
        return boltExecutors;
    }

    @Override
    public int getSpoutExecutors() {
        return spoutExecutors;
    }

    @Override
    public StormTopology newTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(getSpoutName(), new IncrementingSpout(), spoutExecutors);
        builder.setBolt(getBoltName(),
                new VerificationBolt()
                        .withTumblingWindow(new BaseWindowedBolt.Count(tumbleSize)), boltExecutors)
                .shuffleGrouping(getSpoutName());
        return builder.createTopology();
    }
}
