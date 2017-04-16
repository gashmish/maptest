package maptest.service.callback;

import java.util.List;

import maptest.service.model.Transport;


public interface NewTransportsAddedCallback {

    void onNewTransportsAdded(List<Transport> transports);
}
