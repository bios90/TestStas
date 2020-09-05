package com.dimfcompany.teststas;

enum TypeOutput
{
    TXT,
    XML,
    JSON;

    public String getExtension()
    {
        switch (this)
        {
            case TXT:
                return "txt";
            case XML:
                return "xml";
            case JSON:
                return "json";
            default:
                throw new RuntimeException();
        }
    }
}
