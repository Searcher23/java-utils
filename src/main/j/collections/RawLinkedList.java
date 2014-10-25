package j.collections;

import java.util.*;

/**
 * A doubly-linked list that accepts the null element. This class is largely a
 * replica of {@link java.util.LinkedList}, except that it allows direct removal
 * and addition of nodes. This class is not thread-safe.
 */
public class RawLinkedList<E extends RawLinkedList.Node<E>> extends
        AbstractSequentialList<E> implements Deque<E>, List<E>, Queue<E>
{
    public static class Node<E extends Node<E>>
    {
        private Node<E> prev = null, next = null;
        private RawLinkedList<E> parent = null;
    }

    /**
     * With reference to Java 1.6 implementation.
     */
    private class ListIter implements ListIterator<E>
    {
        private Node<E> lastReturned = header;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListIter(int index)
        {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: " + index
                        + ", Size: " + size);

            // determines whether counting from the head or tail is faster.
            if (index < (size >> 1))
            {
                next = header.next;
                for (nextIndex = 0; nextIndex < index; nextIndex++)
                    next = next.next;
            }
            else
            {
                next = header;
                for (nextIndex = size; nextIndex > index; nextIndex--)
                    next = next.prev;
            }
        }

        @Override
        public boolean hasNext()
        {
            return nextIndex != size;
        }

        @Override
        public E next()
        {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            @SuppressWarnings("unchecked")
            E e = (E) lastReturned;
            return e;
        }

        @Override
        public boolean hasPrevious()
        {
            return nextIndex != 0;
        }

        @Override
        public E previous()
        {
            if (nextIndex == 0)
                throw new NoSuchElementException();

            lastReturned = next = next.prev;
            nextIndex--;
            checkForComodification();
            @SuppressWarnings("unchecked")
            E e = (E) lastReturned;
            return e;
        }

        @Override
        public int nextIndex()
        {
            return nextIndex;
        }

        @Override
        public int previousIndex()
        {
            return nextIndex - 1;
        }

        @Override
        public void remove()
        {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();

            @SuppressWarnings("unchecked")
            E lastNext = (E) lastReturned.next;
            RawLinkedList.this.remove(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        @Override
        public void set(E node)
        {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            RawLinkedList.this.addBefore(node, lastReturned);
            RawLinkedList.this.remove(lastReturned);
            lastReturned = node;
            expectedModCount += 2;
        }

        @Override
        public void add(E node)
        {
            checkForComodification();
            lastReturned = header;
            RawLinkedList.this.addBefore(node, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class DescIter implements Iterator<E>
    {
        final ListIter itr = new ListIter(size);

        @Override
        public boolean hasNext()
        {
            return itr.hasPrevious();
        }

        @Override
        public E next()
        {
            return itr.previous();
        }

        @Override
        public void remove()
        {
            itr.remove();
        }
    }

    private transient final Node<E> header = new Node<E>();
    private transient int size = 0;

    public RawLinkedList()
    {
        header.parent = this;
        header.prev = header.next = header;
    }

    private void addBefore(Node<E> newNode, Node<E> node)
    {
        // check whether the node has been added to anywhere.
        if (newNode.parent != null)
            throw new IllegalArgumentException();

        newNode.prev = node.prev;
        newNode.next = node;
        newNode.parent = this;
        node.prev.next = newNode;
        node.prev = newNode;
        size++;
        modCount++;
    }

    /**
     * Removes a node in constant time.
     * 
     * @param node
     *            Must be in the linked list.
     * @throws IllegalArgumentException
     *             if the node is not in the linked list.
     */
    public void remove(Node<E> node)
    {
        if (node.parent != this)
            throw new IllegalArgumentException();

        node.prev.next = node.next;
        node.next.prev = node.prev;
        // set to null to indicate this node is removed.
        node.prev = node.next = null;
        node.parent = null;
        size--;
        modCount++;
    }

    /**
     * @throws IllegalArgumentException
     *             if the node is already added to some list.
     */
    @Override
    public void addFirst(E newNode)
    {
        addBefore(newNode, header.next);
    }

    /**
     * @throws IllegalArgumentException
     *             if the node is already added to some list.
     */
    @Override
    public void addLast(E newNode)
    {
        addBefore(newNode, header);
    }

    @Override
    public E element()
    {
        return getFirst();
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getFirst()
    {
        if (size > 0)
            return (E) header.next;
        throw new NoSuchElementException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getLast()
    {
        if (size > 0)
            return (E) header.prev;
        throw new NoSuchElementException();
    }

    @Override
    public boolean offer(E node)
    {
        addLast(node);
        return true;
    }

    @Override
    public boolean offerFirst(E node)
    {
        addFirst(node);
        return true;
    }

    @Override
    public boolean offerLast(E node)
    {
        addLast(node);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E peek()
    {
        if (size > 0)
            return (E) header.next;
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E peekFirst()
    {
        if (size > 0)
            return (E) header.next;
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E peekLast()
    {
        if (size > 0)
            return (E) header.prev;
        return null;
    }

    @Override
    public E poll()
    {
        return pollFirst();
    }

    @Override
    public E pollFirst()
    {
        if (size == 0)
            return null;
        @SuppressWarnings("unchecked")
        E e = (E) header.next;
        remove(header.next);
        return e;
    }

    @Override
    public E pollLast()
    {
        if (size == 0)
            return null;
        @SuppressWarnings("unchecked")
        E e = (E) header.prev;
        remove(header.prev);
        return e;
    }

    @Override
    public E pop()
    {
        return removeFirst();
    }

    @Override
    public void push(E node)
    {
        addFirst(node);
    }

    @Override
    public E remove()
    {
        return removeFirst();
    }

    @Override
    public E removeFirst()
    {
        if (size == 0)
            throw new NoSuchElementException();
        return pollFirst();
    }

    @Override
    public E removeLast()
    {
        if (size == 0)
            throw new NoSuchElementException();
        return pollLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o)
    {
        if (o != null)
        {
            for (Node<E> cur = header.next; cur != header; cur = cur.next)
            {
                if (o.equals(cur))
                {
                    remove(cur);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o)
    {
        if (o != null)
        {
            for (Node<E> cur = header.prev; cur != header; cur = cur.prev)
            {
                if (o.equals(cur))
                {
                    remove(cur);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ListIterator<E> listIterator(int index)
    {
        return new ListIter(index);
    }

    @Override
    public Iterator<E> descendingIterator()
    {
        return new DescIter();
    }

    @Override
    public int size()
    {
        return size;
    }
}
