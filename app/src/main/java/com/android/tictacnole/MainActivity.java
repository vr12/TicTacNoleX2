package com.android.tictacnole;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {
    private MyFragment fragment;
	private Context context;

    //save views for all boxes
    private View[] boxViews = new View[9];
    private ImageView[] boxImageViews = new ImageView[9];

    //save views for other buttons
    private View View1;
    private TextView TV1;
    private TextView TV2;

    //For bluetooth connection
    private ArrayAdapter mArrayAdapter;
    BluetoothAdapter mBlue;
    ArrayList<String> btlist;

 /*
    ArrayList<BluetoothDevice> devices;
    public static final UUID uuid = UUID.fromString("e7ed2c99-fecd-4935-952f-1ca273c11485");
    protected static final int success = 0;
    protected static final int message = 1;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case success:
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_SHORT).show();
                    String s = "connected";
                    connectedThread.write(s.getBytes());
                    break;
                case message:
                    byte[] readBuf = (byte[])msg.obj;
                    String s = new String(readBuf);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
*/
    //for extra sound effects and audio control
    private SoundPool soundPool;
    private int flipSound;
	private int winSound;

    int[] myImageList1 = new int[]{R.drawable.x, R.drawable.fsu,R.drawable.uf, R.drawable.usf,R.drawable.ucf, R.drawable.famu,R.drawable.fau};
    int[] myImageList2 = new int[]{R.drawable.o,R.drawable.fsu,R.drawable.uf, R.drawable.usf,R.drawable.ucf, R.drawable.famu,R.drawable.fau};
    int y=0;
    int z=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fmanager = getFragmentManager();
        fragment = (MyFragment) fmanager.findFragmentByTag("f");

        //create fragment
        if (fragment == null) {
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
            boxImageViews[i] = (ImageView) boxViews[i];

        setBoxes(false);

        View1 = findViewById(R.id.button1);

        TV1 = (TextView) View1;
        TV2 = (TextView) findViewById(R.id.button2);

        fixRotation();
		
		//load sound effects
		context = getApplicationContext();
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		flipSound = soundPool.load(context, R.raw.flipsound, 1);

        Spinner icons = (Spinner) findViewById(R.id.icons);
        List<String> SpinnerArray = new ArrayList<String>();
        SpinnerArray.add("Player 1: X");
        SpinnerArray.add("FSU");
        SpinnerArray.add("UF");
        SpinnerArray.add("USF");
        SpinnerArray.add("UCF");
        SpinnerArray.add("FAMU");
        SpinnerArray.add("FAU");
        SpinnerArray.add("Player 2: O");
        SpinnerArray.add("FSU ");
        SpinnerArray.add("UF ");
        SpinnerArray.add("USF ");
        SpinnerArray.add("UCF ");
        SpinnerArray.add("FAMU ");
        SpinnerArray.add("FAU ");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SpinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        icons.setAdapter(adapter);
        icons.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner mySpinner=(Spinner) findViewById(R.id.icons);
                String text = mySpinner.getSelectedItem().toString();

                if (text.equals("Player 1: X"))
                {
                    y=0;
                }

                else if (text.equals("FSU"))
                {
                    y=1;
					winSound = soundPool.load(context, R.raw.fsuchantwin, 1);
                }

                else if (text.equals("UF"))
                {
                    y=2;
				    winSound = soundPool.load(context, R.raw.ufwin, 1);
                }

                else if (text.equals("USF"))
                {
                    y=3;
					winSound = soundPool.load(context, R.raw.usfwin, 1);
                }

                else if (text.equals("UCF"))
                {
                    y=4;
					winSound = soundPool.load(context, R.raw.ucfwin, 1);
                }

                else if (text.equals("FAMU"))
                {
                    y=5;
					winSound = soundPool.load(context, R.raw.famuwin, 1);
                }

                else if (text.equals("FAU"))
                {
                    y=6;
					winSound = soundPool.load(context, R.raw.fauwin, 1);
                }

                else if (text.equals("Player 2: O"))
                {
                    z=0;
                }

                else if (text.equals("FSU"))
                {
                    z=1;
					winSound = soundPool.load(context, R.raw.fsuchantwin, 1);
                }

                else if (text.equals("UF"))
                {
                    z=2;
					winSound = soundPool.load(context, R.raw.ufwin, 1);
                }

                else if (text.equals("USF"))
                {
                    z=3;
					winSound = soundPool.load(context, R.raw.usfwin, 1);
                }

                else if (text.equals("UCF"))
                {
                    z=4;
					winSound = soundPool.load(context, R.raw.ucfwin, 1);
                }

                else if (text.equals("FAMU"))
                {
                    z=5;
					winSound = soundPool.load(context, R.raw.famuwin, 1);
                }

                else if (text.equals("FAU"))
                {
                    z=6;
					winSound = soundPool.load(context, R.raw.fauwin, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragment.mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragment.mediaPlayer.start();
    }

    //fix widgets when screen is rotated
    private void fixRotation() {
        if (fragment.gameMode == 0)
            return;

        //set buttons

        View1.setClickable(false);
        TV2.setText(R.string.newGame);

        //no winner yet
        if (fragment.winner == 0) {
            if (fragment.turn == 1)
                TV1.setText(R.string.turn1);
            else
                TV1.setText(R.string.turn2);
        }

        //winner chosen
        else {
            setWinner();
        }

        //set boxes
        for (int i = 0; i < 9; i++) {
            char c = fragment.board[i];

            if (c == '\0') {
                if (fragment.winner == 0)
                    boxViews[i].setClickable(true);
            } else if (c == 'x')
                boxImageViews[i].setImageResource(myImageList1[y]);
            else
                boxImageViews[i].setImageResource(myImageList2[z]);
        }
    }

    public void buttonClick(View view) {
        //set gameMode
        if (fragment.gameMode == 0) {
            switch (view.getId()) {
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
        for (int i = 0; i < 9; i++)
            fragment.board[i] = '\0';

        fragment.gameMode = 0;
    }

    //make all boxes clickable or unclickable
    private void setBoxes(boolean clickable) {
        for (int i = 0; i < 9; i++) {
            if (clickable)
                boxViews[i].setClickable(true);
            else
                boxViews[i].setClickable(false);
        }
    }

    public void makeMove(View view) {
        if (fragment.winner != 0)
            return;

        //find which box was clicked
        int idNum = 0;

        switch (view.getId()) {
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

        if (fragment.turn == 1) {
            boxImageViews[idNum].setImageResource(myImageList1[y]);
            fragment.board[idNum] = 'x';
			soundPool.play(flipSound, 1, 1, 1, 0, 1f);
        } else {
            boxImageViews[idNum].setImageResource(myImageList2[z]);
            fragment.board[idNum] = 'o';
			soundPool.play(flipSound, 1, 1, 1, 0, 1f);
        }

        //check for winner
        lookforWinner();
        if (fragment.winner != 0)
            return;

        //human vs. human
        if (fragment.gameMode == 2) {
            if (fragment.turn == 1) {
                fragment.turn = 2;
                TV1.setText(R.string.turn2);
            } else {
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
    private void lookforWinner() {
        char c;

        if (fragment.turn == 1)
            c = 'x';
        else
            c = 'o';

        int x = 0;
        boolean flag = false;

        for (int i = 0; i < 3; i++) {
            //check collumns
            if (fragment.board[i] == fragment.board[i + 3] && fragment.board[i + 3] == fragment.board[i + 6] && fragment.board[i] == c)
                flag = true;

            //check rows
            if (fragment.board[x] == fragment.board[x + 1] && fragment.board[x + 1] == fragment.board[x + 2] && fragment.board[x] == c)
                flag = true;
            x += 3;
        }

        //check diagonals
        if (fragment.board[4] == fragment.board[2] && fragment.board[2] == fragment.board[6] && fragment.board[4] == c)
            flag = true;

        if (fragment.board[4] == fragment.board[0] && fragment.board[0] == fragment.board[8] && fragment.board[4] == c)
            flag = true;

        if (flag) {
            fragment.winner = fragment.turn;
            setWinner();
            return;
        }

        //no more moves to make and no one wins
        boolean flag1 = true;
        for (int i = 0; i < 9; i++) {
            if (fragment.board[i] == '\0') {
                flag1 = false;
                break;
            }
        }

        if (flag1) {
            fragment.winner = 3;
            setWinner();
        }
    }

    //set winner variable if there is a winner, stop the current game, and print winner
    private void setWinner() {
        Toast toast;
        //winner is 1
        if (fragment.winner == 1) {
			soundPool.play(winSound, 1, 1, 1, 0, 1f);
            if (fragment.gameMode == 1) {
                TV1.setText(R.string.win4);
                toast = Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                TV1.setText(R.string.win1);
                toast = Toast.makeText(getApplicationContext(), "Player 1 Wins!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        //winner is 2
        else if (fragment.winner == 2) {
            if (fragment.gameMode == 1) {
                TV1.setText(R.string.computerWin);
                toast = Toast.makeText(getApplicationContext(), "The Computer Wins.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                TV1.setText(R.string.win2);
                toast = Toast.makeText(getApplicationContext(), "Player 2 Wins!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                soundPool.play(winSound, 1, 1, 1, 0, 1f);
            }
        }

        //tie
        else {
            TV1.setText(R.string.win3);
            toast = Toast.makeText(getApplicationContext(), "Tie Game.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        setBoxes(false);
    }

    //computer methods

    private void computerTurn() {
        fragment.turn = 2;

        //if you can win with this move
        int position = winningMove(false);
        if (position != 10) {
            makecomputerMove(position);
            return;
        }

        //to block the opponents winning move
        position = winningMove(true);
        if (position != 10) {
            makecomputerMove(position);
            return;
        }

        //take the center spot if it is open
        if (fragment.board[4] == '\0') {
            makecomputerMove(4);
            return;
        }

        //chose a random open spot

        //create array to hold position of open spots
        //make enough room incase all spots are open not including 4 because
        //that one will be taken if you are here
        int[] spots = new int[8];
        int arraySlot = 0;

        for (int i = 0; i < 9; i++) {
            if (fragment.board[i] == '\0') {
                spots[arraySlot] = i;
                arraySlot++;
            }
        }

        if (arraySlot != 1) {
            //pass in one less than actual number because it includes 0
            Random r = new Random();
            position = r.nextInt((arraySlot - 1));

            makecomputerMove(spots[position]);
            return;
        }

        makecomputerMove(spots[0]);
    }

    //mark computers move
    private void makecomputerMove(int position) {
        boxViews[position].setClickable(false);

        if (fragment.turn == 1) {
            boxImageViews[position].setImageResource(myImageList2[z]);
            fragment.board[position] = 'x';
        } else {
            boxImageViews[position].setImageResource(myImageList2[z]);
            fragment.board[position] = 'o';
        }
    }

    //returns 10 if a winning move cannot be made and the position if it can
    //if there is more than one winning move it returns the one it finds first
    private int winningMove(boolean opponent) {
        char c;

        //find char to look for
        //can be current player or opponent to help with blocking
        if (fragment.turn == 1) {
            if (opponent)
                c = 'o';
            else
                c = 'x';
        } else {
            if (opponent)
                c = 'x';
            else
                c = 'o';
        }

        //check collumn
        for (int i = 0; i < 3; i++) {
            if (fragment.board[i] == '\0') {
                if (fragment.board[i + 3] == fragment.board[i + 6] && fragment.board[i + 3] == c)
                    return i;
            } else if (fragment.board[i] == c) {
                if (fragment.board[i + 3] == '\0' && fragment.board[i + 6] == c)
                    return (i + 3);
                else if (fragment.board[i + 6] == '\0' && fragment.board[i + 3] == c)
                    return (i + 6);
            }
        }

        //check row
        for (int i = 0; i < 7; i += 3) {
            if (fragment.board[i] == '\0') {
                if (fragment.board[i + 1] == fragment.board[i + 2] && fragment.board[i + 1] == c)
                    return i;
            } else if (fragment.board[i] == c) {
                if (fragment.board[i + 2] == '\0' && fragment.board[i + 1] == c)
                    return (i + 2);
                else if (fragment.board[i + 1] == '\0' && fragment.board[i + 2] == c)
                    return (i + 1);
            }
        }

        //check diagonal
        if (fragment.board[4] == '\0') {
            if (fragment.board[2] == fragment.board[6] && fragment.board[2] == c)
                return 4;
            if (fragment.board[0] == fragment.board[8] && fragment.board[0] == c)
                return 4;
        } else if (fragment.board[4] == c) {
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


    public void connect(View v) {
        int request_enable_bt = 1;
        //List view to display possible match
        ListView bluetoothList = (ListView) findViewById(R.id.bluetoothlist);
        btlist = new ArrayList<String>();
        //devices = new ArrayList<BluetoothDevice>();

        // Bluetooth connectivity
        mBlue = BluetoothAdapter.getDefaultAdapter();
        BroadcastReceiver mReceiver;


        //Turn on bluetooth if off
        if (!mBlue.isEnabled()) {
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, request_enable_bt);
        }

        //Makes phone discoverable
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        if (mBlue.isEnabled())
        {
            boolean discovery_on = mBlue.startDiscovery();

            // Create a BroadcastReceiver for ACTION_FOUND
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        //devices.add(device);
                        // Add the name and address to an array adapter to show in a ListView
                        btlist.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            };
            ArrayAdapter <String> btAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, btlist);
            bluetoothList.setAdapter(btAdapter);
            bluetoothList.requestLayout();
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            if(bluetoothList.getVisibility()!= View.VISIBLE) {
                bluetoothList.setVisibility(View.VISIBLE);
            }
        }
    }

    public class SpinnerActivity extends Activity implements OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            parent.getItemAtPosition(pos);

            Spinner spinner = (Spinner) findViewById(R.id.icons);
            spinner.setOnItemSelectedListener(this);
        }

        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    }

/*
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(mBlue.isDiscovering()){
            mBlue.cancelDiscovery();
        }
        if(mArrayAdapter.getItem(arg2).contains("Paired")){

            BluetoothDevice selectedDevice = devices.get(arg2);
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();
        }
        else{
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {

            }
            mmSocket = tmp;
        }

        public void run() {
            mBlue.cancelDiscovery();
            try {
                mmSocket.connect();

            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            mHandler.obtainMessage(success, mmSocket).sendToTarget();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(message, bytes, -1, buffer)
                            .sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
*/
}
