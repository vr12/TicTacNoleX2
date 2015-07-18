package com.android.tictacnole;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;

//used to keep data when screen is rotated
public class MyFragment extends Fragment
{
    //background music
    public MediaPlayer mediaPlayer = new MediaPlayer();

    //keep track of full board
    public char[] board = new char[9];
    
    //0 is no winner
    //1 is player 1
    //2 is player 2
    //3 is a tie
    public int winner = 0;
    
    //0 is hasnt started
    //1 is 1 player
    //2 is 2 player
    public int gameMode = 0;
    
    //who's turn it is
    public int turn = 1;

    //set and start music
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.background);

        mediaPlayer.setLooping(true);

        mediaPlayer.start();
    }
}