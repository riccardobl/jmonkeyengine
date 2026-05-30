package com.jme3.shader.bufferobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class DirtyRegionsIteratorTest {

    @Test
    public void testNoRegionsFullUpdateRangeAndSliceLength() {
        BufferObject bo = new BufferObject();
        bo.initializeEmpty(8);
        bo.setUpdateNeeded(false);

        BufferRegion region = bo.getDirtyRegions().next();
        assertNotNull(region);
        assertTrue(region.isFullBufferRegion());
        assertEquals(0, region.getStart());
        assertEquals(7, region.getEnd());
        assertEquals(8, region.getData().remaining());

        region.clearDirty();
        bo.clearUpdateNeeded();
        assertFalse(bo.isUpdateNeeded());
    }

    @Test
    public void testMergedDirtyRegionSliceUsesInclusiveEnd() {
        BufferObject bo = new BufferObject();
        bo.setRegions(Arrays.asList(
                new BufferRegion(0, 3),
                new BufferRegion(4, 7),
                new BufferRegion(8, 11)));

        bo.getRegion(0).markDirty();
        bo.getRegion(1).markDirty();
        bo.getRegion(2).clearDirty();

        bo.setUpdateNeeded(false);

        BufferRegion region = bo.getDirtyRegions().next();
        assertNotNull(region);
        assertFalse(region.isFullBufferRegion());
        assertEquals(0, region.getStart());
        assertEquals(7, region.getEnd());
        assertEquals(8, region.getData().remaining());
    }

    @Test
    public void testMergedDirtyRegionGetDataUsesZeroBasedSliceAndPreservesOrder() {
        BufferObject bo = new BufferObject();
        bo.setRegions(Arrays.asList(
                new BufferRegion(0, 3),
                new BufferRegion(4, 7),
                new BufferRegion(8, 11)));

        bo.getByteData().order(ByteOrder.LITTLE_ENDIAN);
        bo.getRegion(0).markDirty();
        bo.getRegion(1).markDirty();
        bo.getRegion(2).clearDirty();
        bo.setUpdateNeeded(false);

        BufferRegion region = bo.getDirtyRegions().next();
        ByteBuffer data = region.getData();

        assertEquals(0, data.position());
        assertEquals(8, data.remaining());
        assertEquals(ByteOrder.LITTLE_ENDIAN, data.order());
    }

    @Test
    public void testAddedDirtyRegionsAreKeptSorted() {
        BufferObject bo = new BufferObject();
        bo.initializeEmpty(16);
        bo.clearUpdateNeeded();

        bo.addDirtyRegion(8, 4);
        bo.addDirtyRegion(0, 4);

        BufferRegion region = bo.getDirtyRegions().next();
        assertNotNull(region);
        assertEquals(0, region.getStart());
        assertEquals(11, region.getEnd());
        assertEquals(12, region.getData().remaining());
    }

    @Test
    public void testSetRegionsSortsRanges() {
        BufferObject bo = new BufferObject();
        bo.initializeEmpty(16);
        bo.setRegions(Arrays.asList(
                new BufferRegion(8, 11),
                new BufferRegion(0, 3)));
        bo.getRegion(0).markDirty();
        bo.getRegion(1).markDirty();
        bo.setUpdateNeeded(false);

        BufferRegion region = bo.getDirtyRegions().next();
        assertNotNull(region);
        assertEquals(0, region.getStart());
        assertEquals(11, region.getEnd());
        assertEquals(12, region.getData().remaining());
    }

    @Test
    public void testNoRegionsHasNextContract() {
        BufferObject bo = new BufferObject();
        bo.initializeEmpty(4);
        bo.setUpdateNeeded(false);

        DirtyRegionsIterator iterator = bo.getDirtyRegions();
        assertTrue(iterator.hasNext());

        BufferRegion region = iterator.next();
        assertNotNull(region);
        assertFalse(iterator.hasNext());
        assertEquals(4, region.getData().remaining());
    }

    @Test
    public void testRegionHasNextSkipsCleanRegions() {
        BufferObject bo = new BufferObject();
        bo.initializeEmpty(12);
        bo.setRegions(Arrays.asList(
                new BufferRegion(0, 3),
                new BufferRegion(4, 7),
                new BufferRegion(8, 11)));
        bo.getRegion(0).clearDirty();
        bo.getRegion(1).clearDirty();
        bo.getRegion(2).markDirty();
        bo.setUpdateNeeded(false);

        DirtyRegionsIterator iterator = bo.getDirtyRegions();
        assertTrue(iterator.hasNext());
        BufferRegion region = iterator.next();
        assertEquals(8, region.getStart());
        assertEquals(11, region.getEnd());
        region.clearDirty();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRegionHasNextReturnsFalseWhenAllRegionsClean() {
        BufferObject bo = new BufferObject();
        bo.initializeEmpty(8);
        bo.setRegions(Arrays.asList(
                new BufferRegion(0, 3),
                new BufferRegion(4, 7)));
        bo.getRegion(0).clearDirty();
        bo.getRegion(1).clearDirty();
        bo.setUpdateNeeded(false);

        DirtyRegionsIterator iterator = bo.getDirtyRegions();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSetDataHandlesSelfAlias() {
        BufferObject bo = new BufferObject();
        ByteBuffer source = ByteBuffer.allocateDirect(4);
        source.put((byte) 0x11);
        source.put((byte) 0x22);
        source.put((byte) 0x33);
        source.put((byte) 0x44);
        source.flip();
        bo.setData(source);

        ByteBuffer sameBuffer = bo.getByteData();
        bo.setData(sameBuffer);

        ByteBuffer result = bo.getByteData();
        assertEquals(4, result.remaining());
        assertEquals((byte) 0x11, result.get());
        assertEquals((byte) 0x22, result.get());
        assertEquals((byte) 0x33, result.get());
        assertEquals((byte) 0x44, result.get());
    }

    @Test
    public void testSetDataPreservesByteOrder() {
        BufferObject bo = new BufferObject();
        ByteBuffer source = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        source.putInt(0x11223344);
        source.flip();

        bo.setData(source);

        assertEquals(ByteOrder.LITTLE_ENDIAN, bo.getByteData().order());
        assertEquals(0x11223344, bo.getByteData().getInt());
    }

    @Test
    public void testRegionResizeCopiesWholeBufferAndPreservesOrder() {
        BufferObject bo = new BufferObject();
        ByteBuffer source = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        source.putInt(0x11223344);
        source.flip();
        bo.setData(source);
        bo.getByteData().position(4);

        bo.setRegions(Arrays.asList(new BufferRegion(0, 7)));
        ByteBuffer resized = bo.getByteData();

        assertEquals(ByteOrder.LITTLE_ENDIAN, resized.order());
        assertEquals(8, resized.limit());
        assertEquals(0x11223344, resized.getInt(0));
    }

    @Test
    public void testClonePreservesByteOrder() {
        BufferObject bo = new BufferObject();
        ByteBuffer source = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        source.putInt(0x11223344);
        source.flip();
        bo.setData(source);

        BufferObject clone = bo.clone();

        assertEquals(ByteOrder.LITTLE_ENDIAN, clone.getByteData().order());
        assertEquals(0x11223344, clone.getByteData().getInt());
    }

    @Test
    public void testSetDataNullClearsBuffer() {
        BufferObject bo = new BufferObject();
        ByteBuffer source = ByteBuffer.allocateDirect(4);
        source.put(new byte[]{1, 2, 3, 4});
        source.flip();
        bo.setData(source);
        assertEquals(4, bo.getByteData().remaining());

        bo.setData(null);
        // getData() auto-allocates an empty buffer when internal data is null
        assertEquals(0, bo.getByteData().remaining());
    }

    @Test
    public void testSetDataNullOnEmptyBufferObject() {
        BufferObject bo = new BufferObject();
        // Should not throw when internal buffer is already null
        bo.setData(null);
        assertEquals(0, bo.getByteData().remaining());
    }
}
