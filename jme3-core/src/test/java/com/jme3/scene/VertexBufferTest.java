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
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    public void testCloneWithOverrideTypeClearsCustomAttributeName() {
        VertexBuffer custom = new VertexBuffer(VertexBuffer.Type.Custom);
        custom.setAttributeName("inCustomData");
        custom.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float,
                BufferUtils.createFloatBuffer(0f, 0f, 0f));

        VertexBuffer position = custom.clone(VertexBuffer.Type.Position);

        assertEquals(VertexBuffer.Type.Position, position.getBufferType());
        assertNull(position.getAttributeName());
        assertEquals("inPosition", position.getShaderAttributeName());
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
    public void testUninitializedVertexBufferRejectsByteDataAccess() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position);

        assertThrows(IllegalStateException.class, () -> vb.getByteData());
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
    public void testByteBackedVertexBufferSetDataPreservesByteOrder() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.BoneIndex);
        vb.setupData(VertexBuffer.Usage.Dynamic, 4, VertexBuffer.Format.UnsignedByte,
                BufferUtils.createByteBuffer(new byte[] {0, 0, 0, 0}));

        ByteBuffer source = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        source.putInt(0x11223344);
        source.flip();

        vb.setData(source);

        assertEquals(ByteOrder.LITTLE_ENDIAN, vb.getByteData().order());
        assertEquals(0x11223344, vb.getByteData().getInt());
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

    @Test
    public void testByteBackedCompactPreservesByteOrder() {
        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.TexCoord);
        ByteBuffer data = ByteBuffer.allocateDirect(6).order(ByteOrder.LITTLE_ENDIAN);
        data.putShort((short) 0x0102);
        data.putShort((short) 0x0304);
        data.putShort((short) 0x0506);
        data.clear();

        vb.setupData(VertexBuffer.Usage.Dynamic, 1, VertexBuffer.Format.Half, data);
        vb.compact(2);

        ByteBuffer compacted = (ByteBuffer) vb.getData();
        assertEquals(ByteOrder.LITTLE_ENDIAN, compacted.order());
        assertEquals((short) 0x0102, compacted.getShort(0));
        assertEquals((short) 0x0304, compacted.getShort(2));
    }
}
