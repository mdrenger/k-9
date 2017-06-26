package com.fsck.k9.activity.setup.autoconfiguration;


import java.security.cert.X509Certificate;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.fsck.k9.Account;
import com.fsck.k9.R;
import com.fsck.k9.activity.K9MaterialActivity;
import com.fsck.k9.activity.setup.AccountSetupAccountType;
import com.fsck.k9.activity.setup.AccountSetupNames;
import com.fsck.k9.activity.setup.autoconfiguration.AccountSetupAutoConfigurationContract.Presenter;
import com.fsck.k9.fragment.ConfirmationDialogFragment;
import com.fsck.k9.fragment.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class AccountSetupAutoConfiguration extends K9MaterialActivity
        implements AccountSetupAutoConfigurationContract.View,
        ConfirmationDialogFragmentListener {

    private static final String EXTRA_EMAIL = "email";

    private static final String EXTRA_PASSWORD = "password";

    private Presenter presenter;
    private boolean mDestroyed;
    private boolean mCanceled;
    private TextView mMessageView;
    private MaterialProgressBar mProgressBar;

    private String email;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setup_autoconfiguration);
        mMessageView = (TextView)findViewById(R.id.message);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.progress);

        setMessage(R.string.account_setup_check_settings_retr_info_msg);
        mProgressBar.setIndeterminate(true);

        email = getIntent().getStringExtra(EXTRA_EMAIL);
        password = getIntent().getStringExtra(EXTRA_PASSWORD);

        presenter = new AccountSetupAutoConfigurationPresenter(this);

        presenter.autoConfiguration(email, password);
    }

    public static void startAutoConfiguration(Context context, String email, String password) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_EMAIL, email);
        bundle.putString(EXTRA_PASSWORD, password);

        Intent intent = new Intent(context, AccountSetupAutoConfiguration.class);

        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void goNext(Account account) {
        AccountSetupNames.actionSetNames(this, account);
    }

    @Override
    public void manualSetup(Account account) {
        AccountSetupAccountType.actionSelectAccountType(this, account, false);
    }

    @Override
    public void showAcceptKeyDialog(final int msgResId, final String exMessage, String message,
            final X509Certificate certificate) {

        // TODO: refactor with DialogFragment.
        // This is difficult because we need to pass through chain[0] for onClick()
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.account_setup_failed_dlg_invalid_certificate_title))
                //.setMessage(getString(R.string.account_setup_failed_dlg_invalid_certificate)
                .setMessage(getString(msgResId, exMessage)
                        + " " + message
                )
                .setCancelable(true)
                .setPositiveButton(
                        getString(R.string.account_setup_failed_dlg_invalid_certificate_accept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.acceptCertificate(certificate);
                            }
                        })
                .setNegativeButton(
                        getString(R.string.account_setup_failed_dlg_invalid_certificate_reject),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .show();
    }

    @Override
    public void showErrorDialog(@StringRes final int msgResId, final Object... args) {
        showDialogFragment(R.id.dialog_account_setup_error, getString(msgResId, args));
    }

    @Override
    public boolean canceled() {
        return mCanceled | mDestroyed;
    }

    @Override
    public void setMessage(@StringRes int id) {
        mMessageView.setText(getString(id));
    }

    private void showDialogFragment(int dialogId, String customMessage) {
        if (mDestroyed) {
            return;
        }

        DialogFragment fragment;
        switch (dialogId) {
            case R.id.dialog_account_setup_error: {
                fragment = ConfirmationDialogFragment.newInstance(dialogId,
                        getString(R.string.account_setup_failed_dlg_title),
                        customMessage,
                        getString(R.string.account_setup_failed_dlg_edit_details_action),
                        getString(R.string.account_setup_failed_dlg_continue_action)
                );
                break;
            }
            default: {
                throw new RuntimeException("Called showDialog(int) with unknown dialog id.");
            }
        }

        FragmentTransaction ta = getFragmentManager().beginTransaction();
        ta.add(fragment, getDialogTag(dialogId));
        ta.commitAllowingStateLoss();

        // TODO: commitAllowingStateLoss() is used to prevent https://code.google.com/p/android/issues/detail?id=23761
        // but is a bad...
        //fragment.show(ta, getDialogTag(dialogId));
    }

    private String getDialogTag(int dialogId) {
        return String.format(Locale.US, "dialog-%d", dialogId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        mCanceled = true;
    }

    @Override
    public void doPositiveClick(int dialogId) {
        switch (dialogId) {
            case R.id.dialog_account_setup_error: {
                finish();
                break;
            }
        }
    }

    @Override
    public void doNegativeClick(int dialogId) {
        switch (dialogId) {
            case R.id.dialog_account_setup_error: {
                presenter.skip();
                presenter.autoConfiguration(email, password);
                break;
            }
        }
    }

    @Override
    public void dialogCancelled(int dialogId) {
        // nothing to do here...
    }
}
