package org.ktachibana.cloudemoji.models.disk;

public interface Reorderable<T> {
    public T copy();

    public void overwrite(T object);
}
