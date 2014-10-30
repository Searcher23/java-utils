package j.collections;

import java.util.*;

/**
 * A doubly-linked list that accepts null. This class is largely similar to
 * {@link java.util.LinkedList}, except that it allows direct removal and
 * addition of nodes. This class is not thread-safe.
 * 
 * @param <E>
 */
public class LinkedList<E> extends AbstractSequentialList<E> implements
        Cloneable, Deque<E>, List<E>, Queue<E>
{
    public static class Node<E>
    {
        private E value;
        private Node<E> prev = null, next = null;
        private LinkedList<E> parent = null;

        public Node(E value)
        {
            this.value = value;
        }

        public final E getValue()
        {
            return value;
        }

        /**
         * Whether this node is added to some {@link LinkedList}.
         * 
         * @return
         */
        public final boolean isAdded()
        {
            return parent != null;
        }
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
            return lastReturned.value;
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
            return lastReturned.value;
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
            Node<E> lastNext = lastReturned.next;
            LinkedList.this.remove(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        @Override
        public void set(E e)
        {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.value = e;
        }

        @Override
        public void add(E e)
        {
            checkForComodification();
            lastReturned = header;
            addBefore(new Node<E>(e), next);
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

    private transient final Node<E> header = new Node<E>(null);
    private transient int size = 0;

    public LinkedList()
    {
        header.parent = this;
        header.prev = header.next = header;
    }

    @Override
    public Object clone()
    {
        LinkedList<E> clone = new LinkedList<E>();

        for (Node<E> e = header.next; e != header; e = e.next)
            clone.addLast(e.value);

        return clone;
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
    public void addFirst(Node<E> newNode)
    {
        addBefore(newNode, header.next);
    }

    /**
     * @throws IllegalArgumentException
     *             if the node is already added to some list.
     */
    public void addLast(Node<E> newNode)
    {
        addBefore(newNode, header);
    }

    @Override
    public boolean add(E e)
    {
        Node<E> newNode = new Node<E>(e);
        addLast(newNode);
        return true;
    }

    @Override
    public void addFirst(E e)
    {
        Node<E> newNode = new Node<E>(e);
        addFirst(newNode);
    }

    @Override
    public void addLast(E e)
    {
        Node<E> newNode = new Node<E>(e);
        addLast(newNode);
    }

    @Override
    public E element()
    {
        return getFirst();
    }

    @Override
    public E getFirst()
    {
        if (size > 0)
            return header.next.value;
        throw new NoSuchElementException();
    }

    @Override
    public E getLast()
    {
        if (size > 0)
            return header.prev.value;
        throw new NoSuchElementException();
    }

    @Override
    public boolean offer(E e)
    {
        addLast(e);
        return true;
    }

    @Override
    public boolean offerFirst(E e)
    {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e)
    {
        addLast(e);
        return true;
    }

    @Override
    public E peek()
    {
        if (size > 0)
            return header.next.value;
        return null;
    }

    @Override
    public E peekFirst()
    {
        if (size > 0)
            return header.next.value;
        return null;
    }

    @Override
    public E peekLast()
    {
        if (size > 0)
            return header.prev.value;
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
        E e = header.next.value;
        remove(header.next);
        return e;
    }

    @Override
    public E pollLast()
    {
        if (size == 0)
            return null;
        E e = header.prev.value;
        remove(header.prev);
        return e;
    }

    @Override
    public E pop()
    {
        return removeFirst();
    }

    @Override
    public void push(E e)
    {
        addFirst(e);
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
    public boolean remove(Object o)
    {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean removeFirstOccurrence(Object o)
    {
        if (o == null)
        {
            for (Node<E> cur = header.next; cur != header; cur = cur.next)
            {
                if (cur.value == null)
                {
                    remove(cur);
                    return true;
                }
            }
        }
        else
        {
            for (Node<E> cur = header.next; cur != header; cur = cur.next)
            {
                if (o.equals(cur.value))
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
        if (o == null)
        {
            for (Node<E> cur = header.prev; cur != header; cur = cur.prev)
            {
                if (cur.value == null)
                {
                    remove(cur);
                    return true;
                }
            }
        }
        else
        {
            for (Node<E> cur = header.prev; cur != header; cur = cur.prev)
            {
                if (o.equals(cur.value))
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
    public Object[] toArray()
    {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> e = header.next; e != header; e = e.next)
            result[i++] = e.value;
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a)
    {
        if (a.length < size)
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
                    .getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> e = header.next; e != header; e = e.next)
            result[i++] = e.value;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    @Override
    public void clear()
    {
        Node<E> e = header.next;
        while (e != header)
        {
            Node<E> next = e.next;
            e.next = e.prev = null;
            e.parent = null;
            e = next;
        }
        // reset state
        header.next = header.prev = header;
        size = 0;
        modCount++;
    }

    @Override
    public int size()
    {
        return size;
    }
}
