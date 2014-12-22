package ru.yetanothercoder.android.googleapi.spreadsheets;

import com.google.api.client.util.Key;

/**
 * @author Mikhail Baturov,  12/22/14 4:17 AM
 */
public class MySheetEntry {
    @Key("gsx:кол1")
    protected String col1;

    @Key("gsx:кол2")
    protected String col2;

    @Key("gsx:кол3")
    protected String col3;


    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public String getCol3() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3 = col3;
    }
}
