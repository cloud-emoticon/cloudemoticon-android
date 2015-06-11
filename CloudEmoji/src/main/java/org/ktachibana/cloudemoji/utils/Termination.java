package org.ktachibana.cloudemoji.utils;

import bolts.Continuation;
import bolts.Task;

public class Termination<TClass> implements Continuation<TClass, Void> {
    public interface Callback<TCallback> {
        void cancelled();

        void faulted(Exception e);

        void succeeded(TCallback result);

        void completed();
    }

    private Callback<TClass> mCallback;

    public Termination(Callback<TClass> callback) {
        mCallback = callback;
    }

    @Override
    public Void then(Task<TClass> task) throws Exception {
        if (task.isCancelled()) {
            mCallback.cancelled();
        }
        if (task.isFaulted()) {
            mCallback.faulted(task.getError());
        }
        if (task.isCompleted()) {
            mCallback.completed();
            if (!task.isFaulted()) {
                mCallback.succeeded(task.getResult());
            }
        }
        return null;
    }
}
