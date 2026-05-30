/*
 * Copyright (c) 2026 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.scene;

import com.jme3.shader.bufferobject.BufferRegion;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VertexBufferTest {

    @Test
    public void testCustomAttributeBuffer() {
        Mesh mesh = new Mesh();
        FloatBuffer data = BufferUtils.createFloatBuffer(0f, 0f, 0f);

        mesh.setBuffer("inCustomData", 3, VertexBuffer.Format.Float, data);

        VertexBuffer vb = mesh.getBuffer("inCustomData");
        assertSame(vb, mesh.getBufferList().get(0));
        assertEquals(VertexBuffer.Type.Custom, vb.getBufferType());
        assertEquals("inCustomData", vb.getAttributeName());
        assertEquals("inCustomData", vb.getShaderAttributeName());
        assertThrows(IllegalArgumentException.class, () -> mesh.getBuffer(VertexBuffer.Type.Custom));
    }

    @Test
    public void testMarkElementsDirtyUsesByteRanges() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position);
        vb.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float,
                BufferUtils.createFloatBuffer(0f, 0f, 0f, 1f, 1f, 1f, 2f, 2f, 2f));
        vb.clearUpdateNeeded();

        vb.markElementsDirty(1, 1);

        BufferRegion region = vb.getDirtyRegions().next();
        assertTrue(vb.isUpdateNeeded());
        assertEquals(12, region.getStart());
        assertEquals(23, region.getEnd());
        assertEquals(12, region.length());
    }

    @Test
    public void testDirtyRangesAreValidatedBeforeRendererUpload() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position);
        vb.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float,
                BufferUtils.createFloatBuffer(0f, 0f, 0f, 1f, 1f, 1f));

        assertThrows(IllegalArgumentException.class, () -> vb.markBytesDirty(2, 4));
        assertThrows(IllegalArgumentException.class, () -> vb.markBytesDirty(24, 4));
        assertThrows(IllegalArgumentException.class, () -> vb.markElementsDirty(2, 1));
    }

    @Test
    public void testTypedVertexBuffersRejectByteDataAccess() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position);
        vb.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float,
                BufferUtils.createFloatBuffer(0f, 0f, 0f));

        assertThrows(UnsupportedOperationException.class, () -> vb.getByteData());
        assertThrows(UnsupportedOperationException.class, () -> vb.setData(BufferUtils.createByteBuffer(12)));
    }

    @Test
    public void testByteBackedVertexBufferSetDataPreservesLayoutMetadata() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.BoneIndex);
        vb.setupData(VertexBuffer.Usage.Dynamic, 4, VertexBuffer.Format.UnsignedByte,
                BufferUtils.createByteBuffer(new byte[] {0, 0, 0, 0}));
        vb.clearUpdateNeeded();

        ByteBuffer source = BufferUtils.createByteBuffer(new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
        vb.setData(source);

        assertEquals(VertexBuffer.Format.UnsignedByte, vb.getFormat());
        assertEquals(4, vb.getNumComponents());
        assertEquals(2, vb.getNumElements());
        assertTrue(vb.hasDataSizeChanged());
        assertEquals(8, vb.getByteData().limit());
    }

    @Test
    public void testHalfBuffersUseTwoBytesPerComponent() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.TexCoord);
        ByteBuffer data = BufferUtils.createByteBuffer(6);
        data.putShort((short) 0);
        data.putShort((short) 0);
        data.putShort((short) 0);
        data.clear();

        vb.setupData(VertexBuffer.Usage.Dynamic, 1, VertexBuffer.Format.Half, data);
        assertTrue(vb.invariant());

        vb.clearUpdateNeeded();
        vb.setElementComponent(1, 0, (short) 0x3c00);

        assertEquals((short) 0x3c00, vb.getElementComponent(1, 0));
        BufferRegion region = vb.getDirtyRegions().next();
        assertEquals(2, region.getStart());
        assertEquals(3, region.getEnd());

        vb.compact(2);
        assertEquals(4, vb.getData().limit());
    }
}
