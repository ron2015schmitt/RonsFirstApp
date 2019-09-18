/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rfschmitt.weatherone.persistence;

import android.util.Log;

import com.rfschmitt.weatherone.UserDataSource;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;


/**
 * Using the Room database as a data source.
 */
public class LocalUserDataSource implements UserDataSource {
    private static final String TAG = LocalUserDataSource.class.getSimpleName();

    private final UserDao mUserDao;

    public LocalUserDataSource(UserDao userDao) {
        mUserDao = userDao;
    }

    @Override
    public Flowable<User> getUser() {
        Log.println(Log.INFO, TAG, "getUser mUserDao.getUser()="+mUserDao.getUser());

        return mUserDao.getUser();
    }

    @Override
    public Completable insertOrUpdateUser(User user) {
        Log.println(Log.INFO, TAG, "insertOrUpdateUser user.getUserName()="+user.getUserName());

        return mUserDao.insertUser(user);
    }

    @Override
    public Completable deleteAllUsers() {
        Log.println(Log.INFO, TAG, "deleteAllUsers()");
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mUserDao.deleteAllUsers();
            }
        });
    }
}
