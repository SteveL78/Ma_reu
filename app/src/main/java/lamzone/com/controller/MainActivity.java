package lamzone.com.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.util.Calendar;

import lamzone.com.R;
import lamzone.com.di.DI;
import lamzone.com.events.DeleteMeetingEvent;
import lamzone.com.events.OpenMeetingEvent;
import lamzone.com.service.MeetingApiService;
import lamzone.com.ui.DatePickerFragment;
import lamzone.com.ui.MyMeetingRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private MeetingApiService mApiService;
    private MyMeetingRecyclerViewAdapter adapter;
    private RecyclerView rv;

    private String filterText;

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
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateMeetingActivity.class);
                startActivity(intent);

            }
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
        // On ouvre une nouvelle activité (=CreateMeetingActivity) quand on clique sur un meeting de la liste
        Intent intent = new Intent(this, CreateMeetingActivity.class);
        intent.putExtra("meeting", event.meeting);
        startActivity(intent);
    }


    /**
     * Fired if the user clicks on delete button
     */
    @Subscribe
    public void deleteMeeting(DeleteMeetingEvent deleteMeetingEvent) {
        mApiService.deleteMeeting(deleteMeetingEvent.getMeeting());
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
        filterText = item.getTitle().toString();
        switch (item.getItemId()) {
            case R.id.reset:
                Toast.makeText(this, "Menu Réinitialiser les réunions sélectionné", Toast.LENGTH_LONG).show();
                return true;


            case R.id.filter_menu:
                Toast.makeText(this, "Menu Filtrer sélectionné", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.filter_by_date:
                Toast.makeText(this, "Menu Filtrer par date sélectionné", Toast.LENGTH_SHORT).show();
                showDateDialog();
                return true;

            case R.id.filter_by_room:
                Toast.makeText(this, "Menu Filtrer par salle sélectionné", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Mario:
                Toast.makeText(this, "Salle Mario sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Luigi:
                Toast.makeText(this, "Salle Luigi sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Peach:
                Toast.makeText(this, "Salle Peach sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Toad:
                Toast.makeText(this, "Salle Toad sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Yoshi:
                Toast.makeText(this, "Salle Yoshi sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Harmonie:
                Toast.makeText(this, "Salle Harmonie sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Wario:
                Toast.makeText(this, "Salle Wario sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Geno:
                Toast.makeText(this, "Salle Géno sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Pauline:
                Toast.makeText(this, "Salle Pauline sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.Funky_Kong:
                Toast.makeText(this, "Salle Funky Kong sélectionnée", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    } // ==================== FIN FILTRE DE LA TOOLBAR ====================

    /**
     * date picker for date filter
     */
    public void showDateDialog() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String filterDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        Toast.makeText(this, filterDate, Toast.LENGTH_SHORT).show();

    }






}