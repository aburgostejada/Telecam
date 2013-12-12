/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.abs.telecam.util;

import android.os.Environment;

import com.abs.telecam.R;
import com.abs.telecam.helpers.ImageHelper;

import java.io.File;
import java.lang.String;
public class Images {


    public static String[] getPhotos(){
        String path = Environment.getExternalStorageDirectory().toString()+"/"+ ImageHelper.directory;
        File f = new File(path);
        File file[] = f.listFiles();
        String[] photos;
        if (file != null) {
            photos = new String[file.length];
            for (int i=0; i < file.length; i++)
            {
                photos[i] = file[i].getAbsolutePath();
            }
            return photos;
        }
        return new String[0];
    }

}
