/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


package org.graalvm.compiler.nodes.test;

import org.graalvm.compiler.core.test.GraalCompilerTest;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.DebugHandlersFactory;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.StructuredGraph.AllowAssumptions;
import org.graalvm.compiler.options.OptionValues;
import org.junit.Assert;
import org.junit.Test;

import jdk.vm.ci.meta.ResolvedJavaMethod;

public class StructuredGraphTest extends GraalCompilerTest {

    @Test
    public void testGetBytecodeSize() {
        OptionValues options = getInitialOptions();
        DebugContext debug = DebugContext.create(options, DebugHandlersFactory.LOADER);
        ResolvedJavaMethod rootMethod = getResolvedJavaMethod("testGetBytecodeSize");

        // Test graph with root method and inlined methods
        StructuredGraph graph = new StructuredGraph.Builder(options, debug, AllowAssumptions.YES).method(rootMethod).build();
        ResolvedJavaMethod otherMethod = getResolvedJavaMethod(GraalCompilerTest.class, "createSuites");
        int expectedBytecodeSize = rootMethod.getCodeSize();
        for (int i = 0; i < 10; i++) {
            graph.recordMethod(otherMethod);
            expectedBytecodeSize += otherMethod.getCodeSize();
        }
        Assert.assertEquals(expectedBytecodeSize, graph.getBytecodeSize());

        // Test graph with only root method, no inlined methods
        graph = new StructuredGraph.Builder(options, debug, AllowAssumptions.YES).method(rootMethod).build();
        expectedBytecodeSize = rootMethod.getCodeSize();
        Assert.assertEquals(expectedBytecodeSize, graph.getBytecodeSize());

        // Test graph with no root method, only inlined methods
        graph = new StructuredGraph.Builder(options, debug, AllowAssumptions.YES).build();
        expectedBytecodeSize = 0;
        for (int i = 0; i < 10; i++) {
            graph.recordMethod(otherMethod);
            expectedBytecodeSize += otherMethod.getCodeSize();
        }
        Assert.assertEquals(expectedBytecodeSize, graph.getBytecodeSize());

        // Test graph with no root method, no inlined methods
        graph = new StructuredGraph.Builder(options, debug, AllowAssumptions.YES).build();
        expectedBytecodeSize = 0;
        Assert.assertEquals(expectedBytecodeSize, graph.getBytecodeSize());
    }
}
