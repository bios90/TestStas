package com.dimfcompany.teststas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception


class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testParser(TypeOutput.TXT)
    }

    private fun testParser(type: TypeOutput)
    {
        val ins_original = assets.open("passport_data.csv")
        val file_output = File.createTempFile("test",type.extension)

        CsvParser.parse(ins_original,file_output,type,object : CsvParser.ParseCallback
        {
            override fun onSuccess(file: File?)
            {
                file?.let(
                    {
                        testResult(it)
                    })
            }

            override fun onError(e: Exception?)
            {
                e?.printStackTrace()
            }
        })
    }

    private fun testDelta()
    {
        val is_old = assets.open("numbers_old.txt")
        val is_new = assets.open("numbers_new.txt")
        val file_output = File.createTempFile("test","txt")

        DeltaGetter.makeDelta(is_old,is_new,file_output)
        testResult(file_output)
    }

    private fun testResult(file: File)
    {
        val ins = FileInputStream(file)

        val buf = BufferedReader(InputStreamReader(ins))
        var line = buf.readLine()
        val sb = StringBuilder()

        while (line != null)
        {
            sb.append(line).append("\n")
            line = buf.readLine()
        }

        val str = sb.toString()
        Log.e("MainActivity", "testResult: Result")
        Log.e("MainActivity", str)

    }

}
