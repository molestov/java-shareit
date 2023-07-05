package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.OffsetBasedPageRequest;

public class OffsetBasedPageRequestTest {
    OffsetBasedPageRequest offsetBasedPageRequest;

    @Test
    public void testOffsetBasedPageRequestWithError() {
        try {
            OffsetBasedPageRequest tryToGetResult = new OffsetBasedPageRequest(-1, 1);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Offset index must not be less than zero!", e.getMessage());
        }
    }

    @Test
    public void testOffsetBasedPageRequestWithError2() {
        try {
            OffsetBasedPageRequest tryToGetResult = new OffsetBasedPageRequest(0, 0);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Limit must not be less than one!", e.getMessage());
        }
    }

    @Test
    public void testGetPageNumber() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertEquals(offsetBasedPageRequest.getPageNumber(), 2);
    }

    @Test
    public void testNext() {
        offsetBasedPageRequest = offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertTrue(offsetBasedPageRequest.next().isPaged());
    }

    @Test
    public void testPrevious() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertTrue(offsetBasedPageRequest.previous().isPaged());
    }

    @Test
    public void testPreviousOrFirst() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertTrue(offsetBasedPageRequest.previousOrFirst().isPaged());
    }

    @Test
    public void testFirst() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertTrue(offsetBasedPageRequest.first().isPaged());
    }

    @Test
    public void testWithPage() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertTrue(offsetBasedPageRequest.withPage(1).isPaged());
    }

    @Test
    public void testHasPrevious() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        Assertions.assertTrue(offsetBasedPageRequest.hasPrevious());
    }

    @Test
    public void testEquals() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5);
        OffsetBasedPageRequest offsetBasedPageRequest1 = new OffsetBasedPageRequest(5, 10);
        Assertions.assertFalse(offsetBasedPageRequest.equals(offsetBasedPageRequest1));
    }

    @Test
    public void testHashCode() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(5, 10);
        Assertions.assertEquals(offsetBasedPageRequest.hashCode(), 875504);
    }

    @Test
    public void testToString() {
        offsetBasedPageRequest = new OffsetBasedPageRequest(5, 10);
        Assertions.assertTrue(offsetBasedPageRequest.toString().contains("limit=10,offset=5,sort=UNSORTED"));
    }
}
