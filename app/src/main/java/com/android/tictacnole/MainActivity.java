package com.android.tictacnole;

import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.DeviceListFragment.WiFiPeerListAdapter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity
{
    WifiP2pManager mManager;
    Channel mChannel;
    WiFiDirectBroadcastReceiver receiver;
    IntentFilter mIntentFilter;

    private MyFragment fragment;
    
    //save views for all boxes
    private View[] boxViews = new View[9];
    private ImageView[] boxImageViews = new ImageView[9];
    
    //save views for other buttons
    private View View1;
    private TextView TV1;
    private TextView TV2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        FragmentManager fmanager = getFragmentManager();
        fragment = (MyFragment) fmanager.findFragmentByTag("f");

        //create fragment
        if (fragment == null)
        {
            fragment = new MyFragment();
            fmanager.beginTransaction().add(fragment, "f").commit();
        }

        //get imageViews
        boxViews[0] = findViewById(R.id.b1);
        boxViews[1] = findViewById(R.id.b2);
        boxViews[2] = findViewById(R.id.b3);
        boxViews[3] = findViewById(R.id.b4);
        boxViews[4] = findViewById(R.id.b5);
        boxViews[5] = findViewById(R.id.b6);
        boxViews[6] = findViewById(R.id.b7);
        boxViews[7] = findViewById(R.id.b8);
        boxViews[8] = findViewById(R.id.b9);

        for (int i = 0; i < 9; i++)
            boxImageViews[i] = (ImageView)boxViews[i];

        setBoxes(false);
        
        View1 = findViewById(R.id.button1);

        TV1 = (TextView)View1;
        TV2 = (TextView)findViewById(R.id.button2);

        fixRotation();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
    }

    private List peers = new ArrayList();

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
            ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
            if (peers.size() == 0) {
                Log.d(WiFiDirectActivity.TAG, "No devices found");
                return;
            }
        }
    }

    @Override
    public void connect() {
        // Picking the first device found on the network.
        WifiP2pDevice device = peers.get(0);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                //error
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
        fragment.mediaPlayer.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
        fragment.mediaPlayer.start();
    }

    //fix widgets when screen is rotated
    private void fixRotation()
    {
        if (fragment.gameMode == 0)
            return;

        //set buttons

        View1.setClickable(false);
        TV2.setText(R.string.newGame);

        //no winner yet
        if (fragment.winner == 0)
        {
            if (fragment.turn == 1)
                TV1.setText(R.string.turn1);
            else
                TV1.setText(R.string.turn2);
        }

        //winner chosen
        else
        {
            setWinner();
        }

        //set boxes
        for (int i = 0; i < 9; i++)
        {
            char c = fragment.board[i];

            if (c == '\0')
            {
                if (fragment.winner == 0)
                    boxViews[i].setClickable(true);
            }
            else if (c == 'x')
                boxImageViews[i].setImageResource(R.drawable.x);
            else
                boxImageViews[i].setImageResource(R.drawable.o);
        }
    }

    public void buttonClick(View view)
    {
        //set gameMode
        if (fragment.gameMode == 0)
        {
            switch (view.getId())
            {
                case R.id.button1:
                    fragment.gameMode = 1;
                    break;

                case R.id.button2:
                    fragment.gameMode = 2;
                    break;
            }

            //change buttons
            setBoxes(true);
            View1.setClickable(false);

            TV1.setText(R.string.turn1);
            TV2.setText(R.string.newGame);
            return;
        }

        //reset for new game
        setBoxes(false);
        for (int i = 0; i < 9; i++)
            boxImageViews[i].setImageResource(R.drawable.gold);

        View1.setClickable(true);

        TV1.setText(R.string.hvc);
        TV2.setText(R.string.hvh);

        fragment.turn = 1;
        fragment.winner = 0;
        for(int i = 0; i < 9; i++)
            fragment.board[i] = '\0';

        fragment.gameMode = 0;
    }

    //make all boxes clickable or unclickable
    private void setBoxes(boolean clickable)
    {
        for (int i = 0; i < 9; i++)
        {
            if (clickable)
                boxViews[i].setClickable(true);
            else
                boxViews[i].setClickable(false);
        }
    }

    public void makeMove(View view)
    {
        if (fragment.winner != 0)
            return;

        //find which box was clicked
        int idNum = 0;

        switch (view.getId())
        {
            case R.id.b1:
                idNum = 0;
                break;

            case R.id.b2:
                idNum = 1;
                break;

            case R.id.b3:
                idNum = 2;
                break;

            case R.id.b4:
                idNum = 3;
                break;

            case R.id.b5:
                idNum = 4;
                break;

            case R.id.b6:
                idNum = 5;
                break;

            case R.id.b7:
                idNum = 6;
                break;

            case R.id.b8:
                idNum = 7;
                break;

            case R.id.b9:
                idNum = 8;
                break;
        }

        boxViews[idNum].setClickable(false);

        if (fragment.turn == 1)
        {
            boxImageViews[idNum].setImageResource(R.drawable.x);
            fragment.board[idNum] = 'x';
        }
        else
        {
            boxImageViews[idNum].setImageResource(R.drawable.o);
            fragment.board[idNum] = 'o';
        }

        //check for winner
        lookforWinner();
        if (fragment.winner != 0)
            return;

        //human vs. human
        if (fragment.gameMode == 2)
        {
            if (fragment.turn == 1)
            {
                fragment.turn = 2;
                TV1.setText(R.string.turn2);
            }
            else
            {
                fragment.turn = 1;
                TV1.setText(R.string.turn1);
            }
            return;
        }

        //human vs. computer
        computerTurn();

        //check for winner
        lookforWinner();

        fragment.turn = 1;
    }

    //see if current player has won
    private void lookforWinner()
    {
        char c;

        if (fragment.turn == 1)
            c = 'x';
        else
            c = 'o';

        int x = 0;
        boolean flag = false;

        for (int i = 0; i < 3; i++)
        {
            //check collumns
            if (fragment.board[i] == fragment.board[i+3] && fragment.board[i+3] == fragment.board[i+6] && fragment.board[i] == c)
                flag = true;

            //check rows
            if (fragment.board[x] == fragment.board[x+1] && fragment.board[x+1] == fragment.board[x+2] && fragment.board[x] == c)
                flag = true;
            x += 3;
        }

        //check diagonals
        if (fragment.board[4] == fragment.board[2] && fragment.board[2] == fragment.board[6] && fragment.board[4] == c)
            flag = true;

        if (fragment.board[4] == fragment.board[0] && fragment.board[0] == fragment.board[8] && fragment.board[4] == c)
            flag = true;

        if (flag)
        {
            fragment.winner = fragment.turn;
            setWinner();
            return;
        }

        //no more moves to make and no one wins
        boolean flag1 = true;
        for (int i = 0; i < 9; i++)
        {
            if (fragment.board[i] == '\0')
            {
                flag1 = false;
                break;
            }
        }

        if (flag1)
        {
            fragment.winner = 3;
            setWinner();
        }
    }

    //set winner variable if there is a winner, stop the current game, and print winner
    private void setWinner()
    {
        Toast toast;
        //winner is 1
        if (fragment.winner == 1)
        {
            if (fragment.gameMode == 1)
            {
                TV1.setText(R.string.win4);
                toast = Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else
            {
                TV1.setText(R.string.win1);
                toast = Toast.makeText(getApplicationContext(), "Player 1 Wins!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        //winner is 2
        else if (fragment.winner == 2)
        {
            if (fragment.gameMode == 1)
            {
                TV1.setText(R.string.computerWin);
                toast = Toast.makeText(getApplicationContext(), "The Computer Wins.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else
            {
                TV1.setText(R.string.win2);
                toast = Toast.makeText(getApplicationContext(), "Player 2 Wins!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        //tie
        else
        {
            TV1.setText(R.string.win3);
            toast = Toast.makeText(getApplicationContext(), "Tie Game.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        setBoxes(false);
    }

    //computer methods

    private void computerTurn()
    {
        fragment.turn = 2;

        //if you can win with this move
        int position = winningMove(false);
        if (position != 10)
        {
            makecomputerMove(position);
            return;
        }

        //to block the opponents winning move
        position = winningMove(true);
        if (position != 10)
        {
            makecomputerMove(position);
            return;
        }

        //take the center spot if it is open
        if (fragment.board[4] == '\0')
        {
            makecomputerMove(4);
            return;
        }

        //chose a random open spot

        //create array to hold position of open spots
        //make enough room incase all spots are open not including 4 because
        //that one will be taken if you are here
        int[] spots = new int[8];
        int arraySlot = 0;

        for (int i = 0; i < 9; i++)
        {
            if (fragment.board[i] == '\0')
            {
                spots[arraySlot] = i;
                arraySlot++;
            }
        }

        if (arraySlot != 1)
        {
            //pass in one less than actual number because it includes 0
            Random r = new Random();
            position = r.nextInt((arraySlot - 1));

            makecomputerMove(spots[position]);
            return;
        }

        makecomputerMove(spots[0]);
    }

    //mark computers move
    private void makecomputerMove(int position)
    {
        boxViews[position].setClickable(false);

        if (fragment.turn == 1)
        {
            boxImageViews[position].setImageResource(R.drawable.x);
            fragment.board[position] = 'x';
        }
        else
        {
            boxImageViews[position].setImageResource(R.drawable.o);
            fragment.board[position] = 'o';
        }
    }

    //returns 10 if a winning move cannot be made and the position if it can
    //if there is more than one winning move it returns the one it finds first
    private int winningMove(boolean opponent)
    {
        char c;

        //find char to look for
        //can be current player or opponent to help with blocking
        if (fragment.turn == 1)
        {
            if (opponent)
                c = 'o';
            else
                c = 'x';
        }
        else
        {
            if (opponent)
                c = 'x';
            else
                c = 'o';
        }

        //check collumn
        for (int i = 0; i < 3; i++)
        {
            if (fragment.board[i] == '\0')
            {
                if (fragment.board[i+3] == fragment.board[i+6] && fragment.board[i+3] == c)
                    return i;
            }
            else if (fragment.board[i] == c)
            {
                if (fragment.board[i+3] == '\0' && fragment.board[i+6] == c)
                    return (i+3);
                else if (fragment.board[i+6] == '\0' && fragment.board[i+3] == c)
                    return (i+6);
            }
        }

        //check row
        for (int i = 0; i < 7; i += 3)
        {
            if (fragment.board[i] == '\0')
            {
                if (fragment.board[i+1] == fragment.board[i+2] && fragment.board[i+1] == c)
                    return i;
            }
            else if (fragment.board[i] == c)
            {
                if (fragment.board[i+2] == '\0' && fragment.board[i+1] == c)
                    return (i+2);
                else if (fragment.board[i+1] == '\0' && fragment.board[i+2] == c)
                    return (i+1);
            }
        }

        //check diagonal
        if (fragment.board[4] == '\0')
        {
            if (fragment.board[2] == fragment.board[6] && fragment.board[2] == c)
                return 4;
            if (fragment.board[0] == fragment.board[8] && fragment.board[0] == c)
                return 4;
        }
        else if (fragment.board[4] == c)
        {
            if (fragment.board[2] == '\0' && fragment.board[6] == c)
                return 2;
            else if (fragment.board[6] == '\0' && fragment.board[2] == c)
                return 6;
            else if (fragment.board[0] == '\0' && fragment.board[8] == c)
                return 0;
            else if (fragment.board[8] == '\0' && fragment.board[0] == c)
                return 8;
        }

        //if there is no winning move
        return 10;
    }
}
