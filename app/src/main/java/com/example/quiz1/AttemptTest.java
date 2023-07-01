package com.example.quiz1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.quiz1.NotificationService;
import com.example.quiz1.R;
import com.example.quiz1.Question;
import com.example.quiz1.Test;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AttemptTest extends AppCompatActivity {
    ArrayList<Question> questions;
    String[] answers;
    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayout indexLayout;
    GridView quesGrid;
    ArrayList<String> list;
    ArrayList<String> arrayList;
    int flag_controller = 1;
    long timer;
    popGridAdapter popGrid;
    Button next, prev;
    TextView textView;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String TESTNAME;
    private RadioGroup group;
    private int countPaused = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_attempt);
        questions = ((Test) getIntent().getExtras().get("Questions")).getQuestions();
        TESTNAME = (String) getIntent().getExtras().get("TESTNAME");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        answers = new String[questions.size()];
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        recyclerView = findViewById(R.id.recyclerView);
        final QuestionAdapter questionAdapter = new QuestionAdapter(questions);
        recyclerView.setAdapter(questionAdapter);

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = recyclerView.getLayoutManager().getPosition(recyclerView.getChildAt(0));
                if (currentPosition < questions.size() - 1) {
                    recyclerView.smoothScrollToPosition(currentPosition + 1);
                } else {
                    showPopUp();
                }
            }
        });

        prev = findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = recyclerView.getLayoutManager().getPosition(recyclerView.getChildAt(0));
                if (currentPosition > 0) {
                    recyclerView.smoothScrollToPosition(currentPosition - 1);
                }
            }
        });

        setNextPrevButton();
        indexLayout = findViewById(R.id.index_layout);
        indexLayout.setAlpha(.5f);
        quesGrid = findViewById(R.id.pop_grid);
        popGrid = new popGridAdapter(AttemptTest.this);
        quesGrid.setAdapter(popGrid);
        quesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                recyclerView.smoothScrollToPosition(i + 1);
                indexLayout.setVisibility(View.GONE);
            }
        });

        textView = findViewById(R.id.timer);

        timer = TimeUnit.MINUTES.toMillis(10); // Set timer here (10 minutes in this case)
        CountDownTimer countDownTimer = new CountDownTimer(timer, 1000) {
            @Override
            public void onTick(long l) {
                textView.setText(formatTime(l));
                timer = l;
            }

            @Override
            public void onFinish() {
                textView.setText("00:00");
                submitTest();
            }
        };
        countDownTimer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_index:
                if (indexLayout.getVisibility() == View.GONE) {
                    indexLayout.setVisibility(View.VISIBLE);
                } else {
                    indexLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.menu_submit:
                showPopUp();
                break;
        }
        return true;
    }

    private String formatTime(long millis) {
        String output;
        String seconds = String.valueOf((int) (millis / 1000) % 60);
        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(millis));
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        output = minutes + ":" + seconds;
        return output;
    }

    private void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttemptTest.this);
        builder.setTitle("Submit Test")
                .setMessage("Are you sure you want to submit the test?")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitTest();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void submitTest() {
        Toast.makeText(AttemptTest.this, "Test submitted", Toast.LENGTH_SHORT).show();
        // Additional logic for submitting the test and handling the results
        // ...
    }

    private void setNextPrevButton() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int currentPosition = recyclerView.getLayoutManager().getPosition(recyclerView.getChildAt(0));
                if (currentPosition == questions.size() - 1) {
                    next.setText("Submit");
                } else {
                    next.setText("Next");
                }
                if (currentPosition == 0) {
                    prev.setVisibility(View.INVISIBLE);
                } else {
                    prev.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
        private ArrayList<Question> questionList;

        public QuestionAdapter(ArrayList<Question> questionList) {
            this.questionList = questionList;
        }

        @NonNull
        @Override
        public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
            return new QuestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
            Question question = questionList.get(position);
            holder.bind(question);
        }

        @Override
        public int getItemCount() {
            return questionList.size();
        }

        public class QuestionViewHolder extends RecyclerView.ViewHolder {
            private TextView questionTextView;
            private RadioButton option1RadioButton;
            private RadioButton option2RadioButton;
            private RadioButton option3RadioButton;
            private RadioButton option4RadioButton;

            public QuestionViewHolder(@NonNull View itemView) {
                super(itemView);
                questionTextView = itemView.findViewById(R.id.question_text_view);
                option1RadioButton = itemView.findViewById(R.id.option1_radio_button);
                option2RadioButton = itemView.findViewById(R.id.option2_radio_button);
                option3RadioButton = itemView.findViewById(R.id.option3_radio_button);
                option4RadioButton = itemView.findViewById(R.id.option4_radio_button);
            }

            public void bind(final Question question) {
                questionTextView.setText(question.getQuestion());
                option1RadioButton.setText(question.getOption1());
                option2RadioButton.setText(question.getOption2());
                option3RadioButton.setText(question.getOption3());
                option4RadioButton.setText(question.getOption4());

                RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.option1_radio_button:
                                answers[getAdapterPosition()] = question.getOption1();
                                break;
                            case R.id.option2_radio_button:
                                answers[getAdapterPosition()] = question.getOption2();
                                break;
                            case R.id.option3_radio_button:
                                answers[getAdapterPosition()] = question.getOption3();
                                break;
                            case R.id.option4_radio_button:
                                answers[getAdapterPosition()] = question.getOption4();
                                break;
                        }
                    }
                };

                option1RadioButton.setOnCheckedChangeListener(listener);
                option2RadioButton.setOnCheckedChangeListener(listener);
                option3RadioButton.setOnCheckedChangeListener(listener);
                option4RadioButton.setOnCheckedChangeListener(listener);
            }
        }
    }

    private class popGridAdapter extends BaseAdapter {
        Context context;

        public popGridAdapter(Context applicationContext) {
            this.context = applicationContext;
        }

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Object getItem(int i) {
            return questions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.pop_item, null);
            }
            TextView text = convertView.findViewById(R.id.grid_text);
            text.setText(String.valueOf(position + 1));

            if (answers[position] != null) {
                convertView.setBackgroundResource(R.drawable.round_button_green);
            } else {
                convertView.setBackgroundResource(R.drawable.round_button_gray);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerView.smoothScrollToPosition(position + 1);
                    indexLayout.setVisibility(View.GONE);
                }
            });

            return convertView;
        }
    }
}

