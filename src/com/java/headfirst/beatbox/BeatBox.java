package com.java.headfirst.beatbox;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/**
 * Code provided by Head First Java, in their BeatBox application.
 *
 * Modification by T.J. Maher:
 * - Separated the instrument keys and instruments placing them in the enum Instruments.java
 * - Made the program flexible to recognize how many instruments were listed, in
 *     'Instrument.values().length' so by editing the enum file, we can add or change the
 *     instruments.
 * - Created a listener for the instrument checkboxes, so the new beat would play instantly,
 *     instead of waiting for the user to press 'Start'
 * - Changed the GUI a bit
 */
public class BeatBox {

    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    public static void main(String[] args) {
        new BeatBox().buildGUI();
    }

    public void buildGUI() {

        theFrame = new JFrame("Beatbox 0.3");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.X_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton clear = new JButton("Clear");
        clear.addActionListener(new MyClearListener());
        buttonBox.add(clear);

        JButton upTempo = new JButton("Fast");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Slow");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

//        JButton save = new JButton("Save");
//        downTempo.addActionListener(new MySaveListener());
//        buttonBox.add(save);
//
//        JButton load = new JButton("Load");
//        downTempo.addActionListener(new MyLoadListener());
//        buttonBox.add(load);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (Instrument ins : Instrument.values()) {
            nameBox.add(new Label(ins.getLabel()));
        }

        background.add(BorderLayout.NORTH, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(Instrument.values().length, 16);
        grid.setVgap(1);
        grid.setHgap(1);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < (Instrument.values().length*16) ; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            c.addActionListener(new MyStartListener());
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ,4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

        } catch (Exception e) {e.printStackTrace(); }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < Instrument.values().length; i++) {

            trackList = new int[16];
            int key = Instrument.values()[i].getDrumKey();

            for (int j = 0; j < 16; j++) {

                JCheckBox jc = checkboxList.get(j + 16 * i);
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }

        track.add(makeEvent(192,9,1,0,15));

            try {
                sequencer.setSequence(sequence);
                sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
                sequencer.setTempoInBPM(120);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public class MyStartListener implements ActionListener {
            public void actionPerformed(ActionEvent a){
                buildTrackAndStart();
            }
        }

        public class MyStopListener implements ActionListener {
            public void actionPerformed(ActionEvent a){
                sequencer.stop();
            }
        }

        public class MyClearListener implements ActionListener {
            public void actionPerformed(ActionEvent a){
                sequencer.stop();
                initialize();
        }
    }

        public class MyUpTempoListener implements ActionListener {
            public void actionPerformed(ActionEvent a){
                float tempoFactor = sequencer.getTempoFactor();
                sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

        public class MyDownTempoListener implements ActionListener {
            public void actionPerformed(ActionEvent a){
                float tempoFactor = sequencer.getTempoFactor();
                sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    }

    public class MySaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a){

            boolean[] checkboxState = new boolean[256];

            for (int i = 0; i < 256; i++){

                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            }
            try {
                FileOutputStream fileStream = new FileOutputStream(new File("Checkbox.ser"));
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class MyLoadListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            boolean[] checkboxState = null;

            try {
                FileInputStream fileIn = new FileInputStream(new File("Checkbox.ser"));
                ObjectInputStream is = new ObjectInputStream(fileIn);
               checkboxState = (boolean[]) is.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            for (int i = 0; i < 256; i++){

                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (checkboxState[i]) {
                    check.setSelected(true);
                } else {
                    check.setSelected(false);
                }
            }
            sequencer.stop();
            buildTrackAndStart();
        }
    }

    public void initialize() {
        for (int i = 0; i < 256; i++) {
            JCheckBox jc = checkboxList.get(i);
            jc.setSelected(false);
        }
    }

    public void makeTracks(int[] list){

        for (int i = 0; i < 16; i++){
            int key = list [i];

            if (key !=0){
                track.add(makeEvent(144,9,key, 100, i));
                track.add(makeEvent(128,9,key, 100, i+1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) { e.printStackTrace(); }

        return event;
    }
}

