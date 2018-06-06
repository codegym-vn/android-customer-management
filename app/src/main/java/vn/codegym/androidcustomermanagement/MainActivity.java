package vn.codegym.androidcustomermanagement;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.codegym.androidcustomermanagement.R;
import vn.codegym.androidcustomermanagement.bean.Customer;
import vn.codegym.androidcustomermanagement.constants.Constants;
import vn.codegym.androidcustomermanagement.crud_demo.AddDialogBox;
import vn.codegym.androidcustomermanagement.crud_demo.DeleteDialogBox;
import vn.codegym.androidcustomermanagement.crud_demo.EditDialogBox;

public class MainActivity extends AppCompatActivity implements EditDialogBox.EditDialogBoxListener, AddDialogBox.AddDialogBoxListener, DeleteDialogBox.DeleteDialogBoxListener {

    private static final String TAG = "MainActivity";

    ListView customerListView;
    ProgressDialog progressDialog;
    private List<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        loadData();
    }

    /**
     * method to set the received data, to ListView.
     */
    private void loadListView() {

        customerListView = (ListView) findViewById(R.id.list);
        ArrayAdapter<Customer> adapter = new ArrayAdapter<Customer>(this, R.layout.row_layout, customers) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.row_layout, null);
                }

                TextView id = (TextView) convertView.findViewById(R.id.id_textView);
                id.setText(String.valueOf(customers.get(position).getId()));

                TextView firstName = (TextView) convertView.findViewById(R.id.firstname_textView);
                firstName.setText(customers.get(position).getFirstName());

                TextView lastName = (TextView) convertView.findViewById(R.id.lastname_textView);
                lastName.setText(customers.get(position).getLastName());

                return convertView;
            }
        };
        customerListView.setAdapter(adapter);
        registerForContextMenu(customerListView);
    }

    /**
     * Pop-up menu on long click.
     *
     * @param menu     {@link android.view.Menu}
     * @param v        {@link View}
     * @param menuInfo {@link ContextMenu.ContextMenuInfo}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    /**
     * Check for action to be performed on menu-item click.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = customers.get(info.position).getId();
        Log.d(TAG, "onContextItemSelected: id = " + id);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID, id);

        switch (item.getItemId()) {
            case R.id.edit:
                DialogFragment editDialog = new EditDialogBox();
                editDialog.setArguments(bundle);
                editDialog.show(getFragmentManager(), TAG);
                return true;
            case R.id.delete:
                DialogFragment deleteDialog = new DeleteDialogBox();
                deleteDialog.setArguments(bundle);
                deleteDialog.show(getFragmentManager(), TAG);
                return true;
        }
        return false;
    }

    /**
     * TODO: Get by id
     */
    private void addClickListener() {
        customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

            }
        });
    }

    /**
     * method to pop up add customer Dialog.
     *
     * @param view {@link View}
     */
    public void add(View view) {
        DialogFragment addDialog = new AddDialogBox();
        addDialog.show(getFragmentManager(), TAG);
    }

    /**
     * method to reload the data. make the server call again.
     *
     * @param view {@link View}
     */
    public void refresh(View view) {
        loadData();
    }

    /**
     * POST request to server to add new data.
     *
     * @param customer {@link Customer}
     */
    @Override
    public void createData(Customer customer) {
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "making POST request");

        Gson gson = new Gson();
        String json = gson.toJson(customer);
        HttpEntity entity = null;
        try {
            entity = new cz.msebera.android.httpclient.entity.StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "createData: " + json);
            e.printStackTrace();
        }
        client.post(getApplicationContext(), Constants.URL, entity, Constants.APPLICATION_JSON, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Log.d(TAG, "SUCCESS");
                Toast.makeText(getApplicationContext(), "CREATED", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                Log.d(TAG, "FAILURE");
                Toast.makeText(getApplicationContext(), "Requested resource not found !!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * GET request to server to get the data.
     */
    private void loadData() {

        progressDialog.show();
        customers = new ArrayList<>();

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "making GET request");

        client.get(Constants.URL, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Gson gson = new Gson();
                String content = new String(responseBody);
                customers = gson.fromJson(content, new TypeToken<List<Customer>>() {
                }.getType());
                Log.d(TAG, "onSuccess: customers : " + customers);
                loadListView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                Log.d(TAG, "FAILURE");
                Toast.makeText(getApplicationContext(), "Requested resource not found !!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * PUT request to server to update the data.
     *
     * @param customer
     */
    @Override
    public void updateData(Customer customer) {
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "making PUT request");

        Gson gson = new Gson();
        String json = gson.toJson(customer);
        HttpEntity entity = null;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "updateData: " + json);
            e.printStackTrace();
        }
        client.put(getApplicationContext(), Constants.URL + "/" + customer.getId(), entity, Constants.APPLICATION_JSON, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Log.d(TAG, "SUCCESS");
                Toast.makeText(getApplicationContext(), "UPDATED", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                Log.d(TAG, "FAILURE");
                Toast.makeText(getApplicationContext(), "Requested resource not found !!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * DELETE request to server to delete the record.
     *
     * @param id int
     */
    @Override
    public void deleteData(int id) {
        progressDialog.show();
        customers = new ArrayList<>();

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "making DELETE request");

        client.delete(Constants.URL + "/" + id, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Log.d(TAG, "SUCCESS");
                Toast.makeText(getApplicationContext(), "DELETED", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                Log.d(TAG, "FAILURE");
                Toast.makeText(getApplicationContext(), "Requested resource not found !!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
