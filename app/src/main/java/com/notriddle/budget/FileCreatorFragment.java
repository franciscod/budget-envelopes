/*
 * This file is a part of Budget with Envelopes.
 * Copyright 2013 Michael Howell <michael@notriddle.com>
 *
 * Budget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Budget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Budget. If not, see <http://www.gnu.org/licenses/>.
 */

package com.notriddle.budget;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import com.notriddle.budget.csv.CSVWriter;
import java.io.FileWriter;
import java.io.File;

public abstract class FileCreatorFragment extends DialogFragment {
    @Override public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        Intent i = getFileIntent();
        startActivityForResult(i, 42);
    }

    protected abstract Intent getFileIntent();

    @Override public void onActivityResult(int req, int res, Intent data) {
        if (req == 42) {
            if (res == Activity.RESULT_OK && data != null) {
                act(data.getData());
            } else {
                dismiss();
            }
        } else {
            super.onActivityResult(req, res, data);
        }
    }

    private void act(Uri uri) {
        ProgressDialog prog = (ProgressDialog) getDialog();
        prog.setMessage(uri.getPath());
        (new AsyncTask<Uri, Object, Throwable>() {
            protected Throwable doInBackground(Uri... dests) {
                try {
                    perform(dests[0]);
                    return null;
                } catch (Throwable e) {
                    return e;
                }
            }
            protected void onPostExecute(Throwable e) {
                dismiss();
                if (e != null) {
                    Toast.makeText(
                        getActivity(),
                        e.toString(),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }).execute(uri);
    }

    abstract protected void perform(Uri uri) throws Throwable;
    abstract protected int getButtonTitle();
    abstract protected int getDialogTitle();

    @Override public ProgressDialog onCreateDialog(Bundle state) {
        ProgressDialog retVal = new ProgressDialog(getActivity());
        retVal.setTitle(getActivity().getString(getDialogTitle()));
        return retVal;
    }
};

