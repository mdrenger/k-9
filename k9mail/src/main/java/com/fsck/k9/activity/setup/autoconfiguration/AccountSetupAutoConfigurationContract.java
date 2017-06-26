package com.fsck.k9.activity.setup.autoconfiguration;


import java.security.cert.X509Certificate;

import android.support.annotation.StringRes;

import com.fsck.k9.Account;
import com.fsck.k9.BasePresenter;
import com.fsck.k9.BaseView;
import com.fsck.k9.mail.CertificateValidationException;
import com.fsck.k9.mail.autoconfiguration.AutoConfigure.ProviderInfo;


/**
 * Created by daquexian on 6/26/17.
 */

interface AccountSetupAutoConfigurationContract {
    interface View extends BaseView<Presenter> {
        void goNext(Account account);
        void manualSetup(Account account);
        void showAcceptKeyDialog(final int msgResId,  final String exMessage, String message,
                X509Certificate certificate);
        void showErrorDialog(final int msgResId, final Object... args);
        boolean canceled();
        void setMessage(@StringRes int id);
    }

    interface Presenter extends BasePresenter {
        void skip();
        void autoConfiguration(String email, String password);
        boolean checkSettings(ProviderInfo providerInfo, String email, String password);
        void acceptCertificate(X509Certificate certificate);
    }
}
