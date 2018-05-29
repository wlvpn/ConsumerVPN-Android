package com.wlvpn.slider.whitelabelvpn.utilities;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * An array list implementation that sorts it's data on add, insert, and replace.
 *
 * @param <T> list item type
 */
public class SortedArrayList<T> implements List<T> {

    private final List<T> list;

    @NonNull
    private Comparator<T> comparator;

    public SortedArrayList(@NonNull Comparator<T> comparator) {
        this.comparator = comparator;
        list = new ArrayList<>();
    }

    /**
     * Set a new comparator and perform sort of the list.
     *
     * @param comparator to sort with
     */
    public void setComparator(@NonNull Comparator<T> comparator) {
        this.comparator = comparator;
        sort();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <U> U[] toArray(@NonNull U[] a) {
        //noinspection SuspiciousToArrayCall
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean modified = list.add(t);
        sort();
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        boolean modified = list.addAll(c);
        sort();
        return modified;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        boolean modified = list.addAll(index, c);
        sort();
        return modified;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        T old = list.set(index, element);
        sort();
        return old;
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
        sort();
    }

    @Override
    public T remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    /**
     * Performs in place collection sorting with the current comparator.
     */
    private void sort() {
        Collections.sort(list, comparator);
    }

}
