package contact.gojek.com.Rest;

import rx.Observer;

/**
 * Created by Pankaj on 12/02/17.
 */

public abstract class NetworkObserver<T> implements Observer<T> {

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onCompleted() {

    }
}
