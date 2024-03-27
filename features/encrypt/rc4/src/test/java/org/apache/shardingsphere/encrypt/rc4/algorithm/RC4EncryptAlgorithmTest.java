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

package org.apache.shardingsphere.encrypt.rc4.algorithm;

import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.algorithm.core.context.AlgorithmSQLContext;
import org.apache.shardingsphere.infra.algorithm.core.exception.AlgorithmInitializationException;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RC4EncryptAlgorithmTest {
    
    private EncryptAlgorithm encryptAlgorithm;
    
    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.put("rc4-key-value", "test-sharding");
        encryptAlgorithm = TypedSPILoader.getService(EncryptAlgorithm.class, "RC4", props);
    }
    
    @Test
    void assertEncode() {
        assertThat(encryptAlgorithm.encrypt("test", mock(AlgorithmSQLContext.class)), is("4Tn7lQ=="));
    }
    
    @Test
    void assertEncryptNullValue() {
        assertNull(encryptAlgorithm.encrypt(null, mock(AlgorithmSQLContext.class)));
    }
    
    @Test
    void assertKeyIsTooLong() {
        Properties props = new Properties();
        props.put("rc4-key-value", IntStream.range(0, 100).mapToObj(each -> "test").collect(Collectors.joining()));
        assertThrows(AlgorithmInitializationException.class, () -> encryptAlgorithm.init(props));
    }
    
    @Test
    void assertKeyIsTooShort() {
        Properties props = new Properties();
        props.put("rc4-key-value", "test");
        assertThrows(AlgorithmInitializationException.class, () -> encryptAlgorithm.init(props));
    }
    
    @Test
    void assertDecode() {
        assertThat(encryptAlgorithm.decrypt("4Tn7lQ==", mock(AlgorithmSQLContext.class)).toString(), is("test"));
    }
    
    @Test
    void assertDecryptNullValue() {
        assertNull(encryptAlgorithm.decrypt(null, mock(AlgorithmSQLContext.class)));
    }
}