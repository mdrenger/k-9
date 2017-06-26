package com.fsck.k9.activity.setup.basics;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import com.fsck.k9.activity.setup.autoconfiguration.AccountSetupAutoConfiguration;
import com.fsck.k9.activity.setup.AccountSetupNames;
import com.fsck.k9.activity.setup.basics.AccountSetupBasicsContract.Presenter;
import timber.log.Timber;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.fsck.k9.Account;
import com.fsck.k9.K9;
import com.fsck.k9.Preferences;
import com.fsck.k9.R;
import com.fsck.k9.activity.K9Activity;
import com.fsck.k9.view.ClientCertificateSpinner.OnClientCertificateChangedListener;


public class AccountSetupBasics extends K9Activity
        implements AccountSetupBasicsContract.View, OnClickListener, TextWatcher {

    private final static String EXTRA_ACCOUNT = "com.fsck.k9.AccountSetupBasics.account";

    private Presenter presenter;
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mNextButton;
    private Account mAccount;

    public static void actionNewAccount(Context context) {
        Intent i = new Intent(context, AccountSetupBasics.class);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setup_basics);
        mEmailView = (EditText)findViewById(R.id.account_email);
        mPasswordView = (EditText)findViewById(R.id.account_password);
        mNextButton = (TextView) findViewById(R.id.next);
        mNextButton.setOnClickListener(this);

        presenter = new AccountSetupBasicsPresenter(this);
    }

    private void initializeViewListeners() {
        mEmailView.addTextChangedListener(this);
        mPasswordView.addTextChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAccount != null) {
            outState.putString(EXTRA_ACCOUNT, mAccount.getUuid());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(EXTRA_ACCOUNT)) {
            String accountUuid = savedInstanceState.getString(EXTRA_ACCOUNT);
            mAccount = Preferences.getPreferences(this).getAccount(accountUuid);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        /*
         * We wait until now to initialize the listeners because we didn't want
         * the OnCheckedChangeListener active while the
         * mClientCertificateCheckBox state was being restored because it could
         * trigger the pop-up of a ClientCertificateSpinner.chooseCertificate()
         * dialog.
         */
        initializeViewListeners();
        presenter.validateFields(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }

    public void afterTextChanged(Editable s) {
        presenter.validateFields(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    private void onNext() {

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        // String[] emailParts = splitEmail(email);
        // String domain = emailParts[1];

        // findProvider(email);

        AccountSetupAutoConfiguration.startAutoConfiguration(this, email, password);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                onNext();
                break;
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void enableNext() {
        mNextButton.setEnabled(true);
    }

}
