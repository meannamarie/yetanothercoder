package ru.yetanothercoder.android.tests;

import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;

public class SampleActivityTest extends ActivityInstrumentationTestCase2<SampleActivity> {

    public SampleActivityTest() {
        super("ru.yetanothercoder.android.tests", SampleActivity.class);
    }

    public void testTrue() {
        Assert.assertTrue(true);
    }
}
