package com.admin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/8/16.
 */

public class Suggestion
{
    @SerializedName("comf")
    public  Comfort comfort;
    @SerializedName("cw")
    public Carwash carwash;

    public Sport sport;

    @SerializedName("drsg")
    public DressSuggest dressSuggest;

    @SerializedName("flu")
    public FluIndex fluIndex;

    @SerializedName("trav")
    public TravelIndex travelIndex;

    public Uv uv;

    public class DressSuggest
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;
    }

    public class FluIndex
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;
    }

    public class TravelIndex
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;

    }

    public class Uv
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;

    }

    public class Comfort
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;
    }

    public class Carwash
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;
    }

    public class Sport
    {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String text;
    }
}
