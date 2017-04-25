package maptest.core.callback;

import java.util.List;

import maptest.core.model.Transport;


public interface NewTransportsAddedCallback {

    void onNewTransportsAdded(List<Transport> transports);
}
