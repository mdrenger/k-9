package com.fsck.k9.activity.setup.basics;


import com.fsck.k9.BasePresenter;
import com.fsck.k9.BaseView;


/**
 * Created by daquexian on 6/26/17.
 */

public interface AccountSetupBasicsContract {
    interface View extends BaseView<Presenter> {
        void enableNext();
    }

    interface Presenter extends BasePresenter {
        void validateFields(String email, String password);
    }
}
