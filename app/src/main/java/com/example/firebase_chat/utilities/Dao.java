package com.example.firebase_chat.utilities;

import com.google.android.gms.tasks.Task;

public interface Dao<T> {

    Task<Void> add(T t);

    Task<Void> delete();
}

