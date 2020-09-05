package com.dimfcompany.teststas;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

enum LineState
{
    DELETED,
    ADDED;

    public char getSign()
    {
        switch (this)
        {
            case ADDED:
                return '+';
            case DELETED:
                return '-';
        }

        throw new RuntimeException("**** Error this type has no sign ****");
    }
}

public class DeltaGetter
{
    private static final String TAG = "DeltaGetter";

    public static void makeDelta(InputStream is_old, InputStream is_new, File file_output)
    {
        BufferedWriter br_output = null;
        try
        {
            BufferedReader br_old = new BufferedReader(new InputStreamReader(is_old));
            BufferedReader br_new = new BufferedReader(new InputStreamReader(is_new));
            br_output = new BufferedWriter(new FileWriter(file_output));

            String str_current_old = null;
            String str_current_new = null;
            String str_next_old = br_old.readLine();
            String str_next_new = br_new.readLine();

            boolean has_lines = str_next_old != null || str_next_new != null;

            Log.e(TAG, "Got here!!!: ");

            while (has_lines)
            {
                str_current_old = str_next_old;
                str_current_new = str_next_new;

                str_next_new = br_new.readLine();
                str_next_old = br_old.readLine();

                if (str_current_new != null && str_current_old != null)
                {
                    if (!str_current_old.equals(str_current_new))
                    {
                        if (str_current_new.equals(str_next_old))
                        {
                            addLineToOutput(br_output, str_current_old, LineState.DELETED);
                            str_next_old = br_old.readLine();
                        }
                        else if (str_next_new.equals(str_current_old))
                        {
                            addLineToOutput(br_output, str_current_new, LineState.ADDED);
                            str_next_new = br_new.readLine();
                        }
                    }
                }
                else if (str_current_old != null)
                {
                    addLineToOutput(br_output, str_current_old, LineState.DELETED);
                }
                else if (str_current_new != null)
                {
                    addLineToOutput(br_output, str_current_new, LineState.ADDED);
                }

                has_lines = str_next_old != null || str_next_new != null;
            }
        }
        catch
        (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is_new != null)
            {
                try
                {
                    is_new.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (is_old != null)
            {
                try
                {
                    is_old.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (br_output != null)
            {
                try
                {
                    br_output.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addLineToOutput(BufferedWriter br_output, String str, LineState state) throws IOException
    {
        String str_result = state.getSign() + str+"\n";
        br_output.write(str_result);
    }
}
