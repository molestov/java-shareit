package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.misc.OffsetBasedPageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OffsetBasedPageRequestTest {
    private OffsetBasedPageRequest offsetBasedPageRequest;

    @Test
    public void testOffsetBasedPageRequestWithError() {
        try {
            OffsetBasedPageRequest tryToGetResult = new OffsetBasedPageRequest(-1, 1);
        } catch (IllegalArgumentException e) {
            assertEquals("Offset index must not be less than zero!", e.getMessage());
        }
    }

    @Test
    public void testOffsetBasedPageRequestWithError2() {
        try {
            OffsetBasedPageRequest tryToGetResult = new OffsetBasedPageRequest(0, 0);
        } catch (IllegalArgumentException e) {
            assertEquals("Limit must not be less than one!", e.getMessage());
        }
    }

    @Test
    public void testGetPageNumber() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertEquals(offsetBasedPageRequest.getPageNumber(), 2);
    }

    @Test
    public void testNext() {
        offsetBasedPageRequest = offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertTrue(offsetBasedPageRequest.next().isPaged());
    }

    @Test
    public void testPrevious() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertTrue(offsetBasedPageRequest.previous().isPaged());
    }

    @Test
    public void testPreviousOrFirst() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertTrue(offsetBasedPageRequest.previousOrFirst().isPaged());
    }

    @Test
    public void testFirst() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertTrue(offsetBasedPageRequest.first().isPaged());
    }

    @Test
    public void testWithPage() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertTrue(offsetBasedPageRequest.withPage(1).isPaged());
    }

    @Test
    public void testHasPrevious() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        assertTrue(offsetBasedPageRequest.hasPrevious());
    }

    @Test
    public void testEquals() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        OffsetBasedPageRequest offsetBasedPageRequest1 = new OffsetBasedPageRequest(5, 10);
        assertFalse(offsetBasedPageRequest.equals(offsetBasedPageRequest1));
    }

    @Test
    public void testHashCode() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(5, 10);
        assertEquals(offsetBasedPageRequest.hashCode(), 875504);
    }

    @Test
    public void testToString() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(5, 10);
        assertTrue(offsetBasedPageRequest.toString().contains("limit=10,offset=5,sort=UNSORTED"));
    }
}
