package cz.sajwy.silencer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cz.sajwy.silencer.R;

public class NapovedaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.NAP_name);
        setContentView(R.layout.activity_napoveda);

        String pravidlaPouzivani = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nullam sapien sem, ornare ac," +
                " nonummy non, lobortis a enim. Nam quis nulla. Duis viverra diam non justo. Ut tempus purus at lorem. Aliquam " +
                "erat volutpat. Sed vel lectus. Donec odio tempus molestie, porttitor ut, iaculis quis, sem. Integer malesuada. " +
                "Etiam bibendum elit eget erat. Nulla pulvinar eleifend sem. Integer pellentesque quam vel velit. Etiam quis quam. " +
                "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat " +
                "facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Cras elementum. Neque porro quisquam est," +
                " qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora " +
                "incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Vestibulum erat nulla, ullamcorper nec, rutrum non" +
                ", nonummy ac, erat. \n \n" +
                "Sed convallis magna eu sem. Donec ipsum massa, ullamcorper in, auctor et, scelerisque sed, est. Nunc auctor. " +
                "Et harum quidem rerum facilis est et expedita distinctio. Praesent in mauris eu tortor porttitor accumsan. " +
                "In laoreet, magna id viverra tincidunt, sem odio bibendum justo, vel imperdiet sapien wisi sed libero. Itaque " +
                "earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut " +
                "perferendis doloribus asperiores repellat. Etiam dictum tincidunt diam. Quis autem vel eum iure reprehenderit " +
                "qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas " +
                "nulla pariatur? Mauris suscipit, ligula sit amet pharetra semper, nibh ante cursus purus, vel sagittis velit " +
                "mauris vel metus. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, " +
                "nisi ut aliquid ex ea commodi consequatur? Integer tempor. Donec iaculis gravida nulla. Nam libero tempore, cum " +
                "soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis " +
                "voluptas assumenda est, omnis dolor repellendus. Ut tempus purus at lorem. Etiam egestas wisi a erat. In convallis." +
                " Aliquam erat volutpat. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis " +
                "egestas. Nulla pulvinar eleifend sem. \n \n" +
                "Et harum quidem rerum facilis est et expedita distinctio. Nullam justo enim, consectetuer nec, ullamcorper " +
                "ac, vestibulum in, elit. Cras elementum. Nulla non lectus sed nisl molestie malesuada. Fusce consectetuer risus " +
                "a nunc. Donec iaculis gravida nulla. Phasellus faucibus molestie nisl. Integer in sapien. Maecenas lorem. Nullam " +
                "rhoncus aliquam metus. Vivamus ac leo pretium faucibus. Nullam at arcu a est sollicitudin euismod. Nam sed tellus " +
                "id magna elementum tincidunt. Aliquam erat volutpat. Nunc auctor.";

        TextView tvPravidla = (TextView) findViewById(R.id.tvPravidla);
        tvPravidla.setText(pravidlaPouzivani);
    }
}