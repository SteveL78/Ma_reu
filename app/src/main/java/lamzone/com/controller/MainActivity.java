package lamzone.com.controller;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lamzone.com.R;
import lamzone.com.di.DI;
import lamzone.com.events.DeleteMeetingEvent;
import lamzone.com.events.OpenMeetingEvent;
import lamzone.com.model.Meeting;
import lamzone.com.model.Room;
import lamzone.com.service.MeetingApiService;
import lamzone.com.ui.DatePickerFragment;
import lamzone.com.ui.MyMeetingRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private MeetingApiService mApiService;
    public MyMeetingRecyclerViewAdapter adapter;
    private RecyclerView rv;
    private State state = State.ALL;
    private String roomName = null;
    private Calendar calendar = null;

    private enum State {
        DATE,
        ROOM,
        ALL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiService = DI.getMeetingApiService();
        rv = findViewById(R.id.meetings_list_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyMeetingRecyclerViewAdapter();
        rv.setAdapter(adapter);

        FloatingActionButton fabBtn = findViewById(R.id.fab_btn);
        fabBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateMeetingActivity.class);
            startActivity(intent);
        });

    }


    // On s'enregistre auprès d'eventBus pour recevoir un évènement (onStart et onStop car cycle de vie de l'activité ou du fragment)
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setData(mApiService.getMeetings());
        adapter.notifyDataSetChanged(); // Refresh
    }


    /**
     * Fired if the user clicks on meeting
     *
     * @param event
     */
    @Subscribe
    public void openMeeting(OpenMeetingEvent event) {
        // On ouvre une nouvelle activité (=CreateMeetingActivity)
        Intent intent = new Intent(this, CreateMeetingActivity.class);
        startActivity(intent);
    }


    /**
     * Fired if the user clicks on delete button
     */
    @Subscribe
    public void deleteMeeting(DeleteMeetingEvent deleteMeetingEvent) {
        mApiService.deleteMeeting(deleteMeetingEvent.getMeeting());
        switch (state){
            case ALL:
                adapter.setData(mApiService.getMeetings());
                break;

            case DATE:
                adapter.setData(mApiService.filterMeetingListForDay(calendar));
                break;

            case ROOM:
                adapter.setData(mApiService.filterMeetingListForRoom(roomName));
                break;
        }
        adapter.notifyDataSetChanged();
    }



    // ==================== FILTRE TOOLBAR ====================

    /**
     * Create the filter menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    /**
     * Set option menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                List<Meeting> meetingsReset = mApiService.getMeetings();

                state = State.ALL;

                adapter.setData(meetingsReset);
                adapter.notifyDataSetChanged(); // Refresh
                return true;

            case R.id.filter_by_date:
                showDateDialog();

                state = State.DATE;

                return true;

            case R.id.filter_by_room:
                roomSelector();

                state = State.ROOM;

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    } // ==================== FIN FILTRE DE LA TOOLBAR ====================


    /**
     * Single Choice Item for Filter Rooms
     */
    private void roomSelector() {

        // Create list of rooms
        List<String> rooms = new ArrayList<>();

        for (Room room : mApiService.getRooms()) {
            rooms.add(room.getName());
        }
        CharSequence[] cs = rooms.toArray(new CharSequence[0]);

        AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        mbuilder.setTitle(R.string.alertDialog_select_a_room); // Set title of AlertDialog
        mbuilder.setIcon(R.drawable.icon);
        mbuilder.setSingleChoiceItems(cs, -1, (dialogInterface, i) -> {
            String roomSelected = rooms.get(i);
            roomName = roomSelected;
            filterItemRoom(roomSelected);

        });
        // Set neutral cancel button
        mbuilder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
        });

        mbuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
        });

        AlertDialog mDialog = mbuilder.create();
        mDialog.show();

    }


    /**
     * date picker for date filter
     */
    public void showDateDialog() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // Je récupère les réunions du jour sélectionné
        List<Meeting> result = mApiService.filterMeetingListForDay(calendar);

        // Afficher ses réunions
        adapter.setData(result);
        adapter.notifyDataSetChanged(); // Refresh

    }


    /**
     * Filter by room
     */
    public void filterItemRoom(String room) {

        // On Récupère les réunions de la salle sélectionnée
        List<Meeting> result = mApiService.filterMeetingListForRoom(room);

        // Afficher ses réunions
        adapter.setData(result);
        adapter.notifyDataSetChanged(); // Refresh
    }
}