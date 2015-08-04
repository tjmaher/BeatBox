package com.java.headfirst.beatbox;

/**
 * Created by T.J. Maher on 8/1/2015.
 */
public enum Instrument {

    BASS_DRUM("Bass Drum", 35),
    SIDE_STICK("Side Stick", 37),
    CLOSED_HIGH_HAT("Closed High Hat", 42),
    OPEN_HIGH_HAT("Open High Hat", 46),
    ACOUSTIC_SNARE("Acoustic Snare", 38),
    CRASH_CYMBAL("Crash Cymbal", 49),
    CHINESE_SYMBOL("Chinese Symbol", 52),
    RIDE_BELL("Ride Bell", 53),
    HAND_CLAP("Hand Clap", 39),
    HIGH_TOM("High Tom", 50),
    HI_BONGO("Hi Bongo", 60),
    MARACAS("Maracas", 70),
    SHORT_WHISTLE("Short Whistle",71),
    LONG_WHISTLE("Long Whistle", 72),
    TAMBOURINE("Tamborine", 54),
    LOW_CONGA("Low Conga", 64),
    COWBELL("Cowbell", 56),
    VIBRASLAP("Vibraslap", 58),
    LOW_MID_TOM("Low-mid Tom", 47),
    HIGH_AGOGO("High Agogo", 67),
    OPEN_HI_CONGA("Open-hi Conga", 63),
    TRIANGLE("Triangle", 81 );

    private String label;
    private int drumKey;

    Instrument(String label, int drumKey){
        this.label = label;
        this.drumKey = drumKey;
    }

    public String getLabel(){
        return this.label;
    }

    public int getDrumKey(){
        return this.drumKey;
    }
}
