package com.fsck.k9.activity.setup.basics;


import com.fsck.k9.EmailAddressValidator;
import com.fsck.k9.activity.setup.basics.AccountSetupBasicsContract.View;


class AccountSetupBasicsPresenter implements AccountSetupBasicsContract.Presenter {
    private View view;

    AccountSetupBasicsPresenter(View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void validateFields(String email, String password) {
        EmailAddressValidator emailValidator = new EmailAddressValidator();

        boolean valid = email != null && email.length() > 0
                && password != null && password.length() > 0
                && emailValidator.isValidAddressOnly(email);

        if (valid) {
            view.enableNext();
        }
    }
}
