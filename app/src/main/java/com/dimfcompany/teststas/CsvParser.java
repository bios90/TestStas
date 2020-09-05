package com.dimfcompany.teststas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CsvParser
{
    interface ParseCallback
    {
        void onSuccess(File file);

        void onError(Exception e);
    }

    private static final String TAG = "CsvParser";

    private static final int BUFFER_SIZE = 1000;
    private static String[] STR_BUFFER = new String[BUFFER_SIZE];
    private static int CURRENT_INDEX = 0;
    private static boolean IS_INITAIL_WRITE_MODE = true;

    public static void parse(final InputStream ins_original, final File output, final TypeOutput type_output, final ParseCallback callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                makeParse(ins_original, output, type_output, callback);
                callback.onSuccess(output);
            }

        }).start();
    }

    private static void makeParse(InputStream ins_original, File output, TypeOutput type_output, ParseCallback callback)
    {
        CURRENT_INDEX = 0;
        STR_BUFFER = new String[BUFFER_SIZE];
        IS_INITAIL_WRITE_MODE = true;

        BufferedWriter br_output = null;
        BufferedReader br_original = null;

        try
        {
            br_original = new BufferedReader(new InputStreamReader(ins_original));
            br_output = new BufferedWriter(new FileWriter(output));

            prepareOutput(br_output, type_output);

            String line;
            while ((line = br_original.readLine()) != null)
            {
                STR_BUFFER[CURRENT_INDEX] = line;
                CURRENT_INDEX++;

                if (CURRENT_INDEX == BUFFER_SIZE)
                {
                    writeLinesToFile(br_output, type_output);
                    CURRENT_INDEX = 0;
                }
            }

            writeLinesToFile(br_output, type_output);
            closeOutput(br_output, type_output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            callback.onError(e);
        }
        finally
        {
            try
            {
                ins_original.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                br_original.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void prepareOutput(BufferedWriter br, TypeOutput type) throws IOException
    {
        switch (type)
        {
            case JSON:
                br.write("[");
                break;
            case XML:
                br.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>");
                break;
        }
    }

    private static void closeOutput(BufferedWriter br, TypeOutput type) throws IOException
    {
        switch (type)
        {
            case JSON:
                br.write("\n]");
                break;
            case XML:
                br.write("\n</root>");
                break;
        }

        br.close();
    }

    private static void writeLinesToFile(BufferedWriter br, TypeOutput type) throws IOException
    {
        for (int i = 0; i < STR_BUFFER.length; i++)
        {
            String formatted_line = STR_BUFFER[i];

            if (formatted_line == null || formatted_line.isEmpty())
            {
                continue;
            }

            switch (type)
            {
                case JSON:
                    formatted_line = "\n\"" + formatted_line + "\"";
                    if (!IS_INITAIL_WRITE_MODE)
                    {
                        formatted_line = "," + formatted_line;
                    }
                    break;
                case XML:
                    formatted_line = "\n<row>" + formatted_line + "</row>";
                    break;
                case TXT:
                    formatted_line = formatted_line + "\n";
                    break;
            }

            br.write(formatted_line);
            IS_INITAIL_WRITE_MODE = false;
        }

        STR_BUFFER = new String[BUFFER_SIZE];
    }
}
