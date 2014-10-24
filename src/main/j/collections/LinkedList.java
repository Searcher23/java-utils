package j.collections;

import java.util.*;

/**
 * A doubly-linked list that accepts the null element. This class is largely a
 * replica of {@link java.util.LinkedList}, except that it allows direct removal
 * and addition of nodes.
 * 
 * @param <E>
 */
public class LinkedList<E> extends AbstractSequentialList<E> implements
        Cloneable, Deque<E>, List<E>, Queue<E>
{
    public static class Node<E>
    {
        private E value;
        private Node<E> prev, next;

        public Node(E value)
        {
            this.value = value;
        }

        public E getValue()
        {
            return value;
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
            checkForComodification();
            Node<E> lastNext = lastReturned.next;
            try
            {
                LinkedList.this.remove(lastReturned);
            }
            catch (NoSuchElementException e)
            {
                throw new IllegalStateException();
            }
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
        newNode.prev = node.prev;
        newNode.next = node;
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
     * @throws NoSuchElementException
     */
    public void remove(Node<E> node)
    {
        if (node == header)
            throw new NoSuchElementException();

        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = node.next = null;
        size--;
        modCount++;
    }

    public void addFirst(Node<E> newNode)
    {
        addBefore(newNode, header.next);
    }

    public void addLast(Node<E> newNode)
    {
        addBefore(newNode, header);
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
    public int size()
    {
        return size;
    }
}
